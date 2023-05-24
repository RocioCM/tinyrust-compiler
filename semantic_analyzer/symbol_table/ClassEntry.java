package semantic_analyzer.symbol_table;

import java.util.HashMap;

import java.util.Iterator;

import error.semantic.declarations.BadFormedConstructorError;
import error.semantic.declarations.ConsolidationError;
import error.semantic.declarations.DuplicatedEntityIdError;
import error.semantic.declarations.IllegalSelfDeclarationError;
import error.semantic.declarations.MultipleConstructorsError;
import semantic_analyzer.types.ClassType;
import semantic_analyzer.types.Type;
import util.Json;

public class ClassEntry implements TableElement {
	private String name;
	private Location locationDecl;
	private String extendsFrom = "Object";
	private Location locationExtendsFrom;
	private boolean extendable = true;
	private ConstructorEntry constructor;
	private TableList<MethodEntry> methods;
	private TableList<AttributeEntry> attributes;
	private boolean consolidated = false;

	public ClassEntry(String name, Location loc) {
		this.name = name;
		this.locationDecl = loc;
		this.constructor = new ConstructorEntry(false, new ClassType(name), loc);
		this.methods = new TableList<MethodEntry>();
		this.attributes = new TableList<AttributeEntry>();
	}

	public ClassEntry(String name, boolean extendable, ConstructorEntry constructor, Location loc) {
		this(name, loc);
		this.extendable = extendable;
		this.constructor = constructor;
		if (name.equals("Object") || name.equals("main")) {
			this.extendsFrom = null;
		}
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("heredaDe", extendsFrom);
		json.addAttr("heredable", extendable);
		json.addAttr("atributos", attributes);
		json.addAttr("constructor", constructor);
		json.addAttr("metodos", methods);
		return json.toString();
	}

	public AttributeEntry addAttribute(String name, Type type, boolean isPublic, Location loc)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError {
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError(loc);
		}
		if (attributes.containsKey(name)) {
			throw new DuplicatedEntityIdError("L ATRIBUTO", name, loc);
		}

		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, isPublic, loc);
		attributes.put(name, attr);
		return attr;
	}

	public MethodEntry addMethod(String name, boolean isStatic, Location loc)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError, BadFormedConstructorError {
		if (methods.containsKey(name)) {
			throw new DuplicatedEntityIdError("L METODO", name, loc);
		}
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError(loc);
		}
		if (name.equals("create")) {
			throw new BadFormedConstructorError(loc);
		}

		MethodEntry method = new MethodEntry(name, isStatic, methods.size() + 1, loc);
		methods.put(name, method);
		return method;
	}

	public ConstructorEntry addConstructor(Location loc) throws MultipleConstructorsError {
		if (this.constructor.isAlreadyDeclared()) {
			throw new MultipleConstructorsError(loc);
		}

		ConstructorEntry constructor = new ConstructorEntry(true, new ClassType(name), loc);
		this.constructor = constructor;
		return constructor;
	}

	public void consolidate(TableList<ClassEntry> classes, HashMap<String, ClassEntry> superClassesTree)
			throws ConsolidationError {
		if (!isConsolidated() && extendsFrom != null) {
			ClassEntry superClass = superClassesTree.get(name);
			superClass.consolidate(classes, superClassesTree);

			// 1. Validar que exista el tipo de cada atributo.
			Iterator<AttributeEntry> attributesIter = attributes.values().iterator();
			while (attributesIter.hasNext()) { // Iterar sobre cada atributo de la clase.
				AttributeEntry attr = attributesIter.next();
				if (classes.get(attr.type().type()) == null) {
					throw new ConsolidationError(attr.locationDecl(),
							"EL ATRIBUTO " + attr.name() + " ES DEL TIPO NO DECLARADO " + attr.type().type());
				}
			}

			// 2. Consolidar atributos.
			Iterator<AttributeEntry> superAttrsIter = superClass.attributes().values().iterator();
			while (superAttrsIter.hasNext()) { // Iterar sobre cada atributo de la supercalse.
				AttributeEntry superAttr = superAttrsIter.next();
				if (attributes.containsKey(superAttr.name())) {
					// No es válido redeclarar atributos.
					throw new ConsolidationError(superAttr.locationDecl(),
							"NO ESTA PERMITIDO REDEFINIR ATRIBUTOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL ATRIBUTO "
									+ superAttr.name());
				} else {
					// El atributo no se redefine, entonces se agrega a la subclase.
					AttributeEntry newAttr = new AttributeEntry(superAttr, attributes.size() + 1); // Clonar
					attributes.put(newAttr.name(), newAttr);
				}
			}

			// 3. Validar que exista el tipo de retorno y el tipo de cada argumento formal
			// de cada método.
			Iterator<MethodEntry> methodsIter = methods.values().iterator();
			while (methodsIter.hasNext()) { // Iterar sobre cada método de la clase.
				MethodEntry method = methodsIter.next();
				method.validate(classes);
			}

			// 4. Consolidar métodos.
			Iterator<MethodEntry> superMethodsIter = superClass.methods().values().iterator();
			while (superMethodsIter.hasNext()) { // Iterar sobre cada método de la superclase.
				MethodEntry superMethod = superMethodsIter.next();
				if (methods.containsKey(superMethod.name())) {
					// Si la subclase redeclara un método, se valida que la firma coincida.
					MethodEntry subMethod = methods.get(superMethod.name());
					if (superMethod.isStatic()) {
						// No es válido redeclarar métodos estáticos.
						throw new ConsolidationError(subMethod.locationDecl(),
								"NO ESTA PERMITIDO REDEFINIR METODOS ESTATICOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL METODO ESTATICO "
										+ superMethod.name());
					}

					if (subMethod.isStatic()) {
						// No es válido redeclarar un método estático si en la superclase era dinámico.
						throw new ConsolidationError(subMethod.locationDecl(),
								"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTO MODIFICADOR. EL SUB METODO "
										+ superMethod.name()
										+ " ES ESTATICO, MIENTRAS QUE EL SUPER METODO ES DINAMICO.");
					}

					// Validar mismo tipo de retorno.
					if (!(superMethod.returnType().type().equals(subMethod.returnType().type()))) {
						throw new ConsolidationError(subMethod.locationDecl(),
								"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTA FIRMA: TIPO DE RETORNO NO COINCIDE. (METODO "
										+ superMethod.name() + ")");
					}

					// Validar misma cantidad de argumentos.
					if (superMethod.arguments().size() != subMethod.arguments().size()) {
						throw new ConsolidationError(subMethod.locationDecl(),
								"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTA FIRMA: CANTIDAD DE ARGUMENTOS NO COINCIDE. (METODO "
										+ superMethod.name() + ")");
					}

					// Por practicidad, se arma un mapeo de posición y tipo de cada argumento del
					// método de la subclase.
					HashMap<Number, Type> subMethodArgTypes = new HashMap<Number, Type>();
					subMethod.arguments().values().forEach((arg) -> {
						subMethodArgTypes.put(arg.position(), arg.type());
					});
					// Validar misma posición y tipo de cada argumento.
					if (!superMethod.arguments().values().stream().allMatch(
							(superArgument) -> {
								return subMethodArgTypes.get(superArgument.position()).type()
										.equals(superArgument.type().type());
							})) {
						throw new ConsolidationError(subMethod.locationDecl(),
								"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTA FIRMA: TIPO Y ORDEN DE ARGUMENTOS NO COINCIDE. (METODO "
										+ superMethod.name() + ")");
					}
				} else {
					// El método no se redefine, entonces se agrega a la subclase.
					MethodEntry newMethod = new MethodEntry(superMethod, methods.size() + 1); // Clonar
					methods.put(newMethod.name(), newMethod);
				}
			}
		}
		setConsolidated(true);
	}

	public String name() {
		return name;
	}

	public TableList<AttributeEntry> attributes() {
		return this.attributes;
	}

	public TableList<MethodEntry> methods() {
		return this.methods;
	}

	public MethodEntry constructor() {
		return this.constructor;
	}

	public String extendsFrom() {
		return extendsFrom;
	}

	public Location locationDecl() {
		return locationDecl;
	}

	public void setExtendsFrom(String extendsFrom, Location loc) {
		this.extendsFrom = extendsFrom;
		this.locationExtendsFrom = loc;
	}

	public Location locationExtendsFrom() {
		return locationExtendsFrom;
	}

	public boolean isConsolidated() {
		return consolidated;
	}

	public void setConsolidated(boolean consolidated) {
		this.consolidated = consolidated;
	}

}
