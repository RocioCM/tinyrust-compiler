package semantic_analyzer.symbol_table;

import java.util.HashMap;

import java.util.Iterator;

import error.semantic.BadFormedConstructorError;
import error.semantic.ConsolidationError;
import error.semantic.DuplicatedEntityIdError;
import error.semantic.IllegalBaseExtendError;
import error.semantic.IllegalSelfDeclarationError;
import error.semantic.MultipleConstructorsError;
import semantic_analyzer.symbol_table.types.Type;
import util.Json;

public class ClassEntry implements TableElement {
	private String name;
	private String extendsFrom = "Object";
	private Location locationExtendsFrom;
	private boolean extendable = true;
	private ConstructorEntry constructor;
	private TableList<MethodEntry> methods;
	private TableList<AttributeEntry> attributes;
	private boolean consolidated = false;

	public ClassEntry(String name) {
		this.name = name;
		this.constructor = new ConstructorEntry(false);
		this.methods = new TableList<MethodEntry>();
		this.attributes = new TableList<AttributeEntry>();
	}

	public ClassEntry(String name, boolean extendable, ConstructorEntry constructor) {
		this(name);
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

	public AttributeEntry addAttribute(String name, Type type, boolean isPublic, int line, int col)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError {
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError();
		}
		if (attributes.containsKey(name)) {
			throw new DuplicatedEntityIdError("L ATRIBUTO", name);
		}

		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, isPublic, line, col);
		attributes.put(name, attr);
		return attr;
	}

	public MethodEntry addMethod(String name, boolean isStatic, int line, int col)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError, BadFormedConstructorError {
		if (methods.containsKey(name)) {
			throw new DuplicatedEntityIdError("L METODO", name);
		}
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError();
		}
		if (name.equals("create")) {
			throw new BadFormedConstructorError();
		}

		MethodEntry method = new MethodEntry(name, isStatic, methods.size() + 1, line, col);
		methods.put(name, method);
		return method;
	}

	public ConstructorEntry addConstructor() throws MultipleConstructorsError {
		if (this.constructor.isAlreadyDeclared()) {
			throw new MultipleConstructorsError();
		}

		ConstructorEntry constructor = new ConstructorEntry(true);
		this.constructor = constructor;
		return constructor;
	}

	public void consolidate(HashMap<String, ClassEntry> superClassesTree) throws ConsolidationError {
		if (!isConsolidated() && extendsFrom != null) {
			ClassEntry superClass = superClassesTree.get(name);
			superClass.consolidate(superClassesTree);

			// Consolidar atributos.
			Iterator<AttributeEntry> superAttrsIter = superClass.attributes().values().iterator();
			while (superAttrsIter.hasNext()) { // Iterar sobre cada atributo de la supercalse.
				AttributeEntry superAttr = superAttrsIter.next();
				if (attributes.containsKey(superAttr.name())) {
					// No es válido redeclarar atributos.
					throw new ConsolidationError(superAttr.locationDecl().getLine(), superAttr.locationDecl().getCol(),
							"NO ESTA PERMITIDO REDEFINIR ATRIBUTOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL ATRIBUTO "
									+ superAttr.name());
				} else {
					// El atributo no se redefine, entonces se agrega a la subclase.
					AttributeEntry newAttr = new AttributeEntry(superAttr, attributes.size() + 1); // Clonar
					attributes.put(newAttr.name(), newAttr);
				}
			}

			// Consolidar métodos.
			Iterator<MethodEntry> superMethodsIter = superClass.methods().values().iterator();
			while (superMethodsIter.hasNext()) { // Iterar sobre cada método de la superclase.
				MethodEntry superMethod = superMethodsIter.next();
				if (methods.containsKey(superMethod.name())) {
					// Si la subclase redeclara un método, se valida que la firma coincida.
					MethodEntry subMethod = methods.get(superMethod.name());
					if (superMethod.isStatic() || subMethod.isStatic()) {
						// No es válido redeclarar métodos estáticos.
						throw new ConsolidationError(subMethod.locationDecl().getLine(), subMethod.locationDecl().getCol(),
								"NO ESTA PERMITIDO REDEFINIR METODOS ESTATICOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL METODO ESTATICO "
										+ superMethod.name());
					}

					// Validar mismo tipo de retorno.
					if (!(superMethod.returnType().type().equals(subMethod.returnType().type()))) {
						throw new ConsolidationError(subMethod.locationDecl().getLine(), subMethod.locationDecl().getCol(),
								"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTA FIRMA: TIPO DE RETORNO NO COINCIDE. (METODO "
										+ superMethod.name() + ")");
					}

					// Validar misma cantidad de argumentos.
					if (superMethod.arguments().size() != subMethod.arguments().size()) {
						throw new ConsolidationError(subMethod.locationDecl().getLine(), subMethod.locationDecl().getCol(),
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
								return subMethodArgTypes.get(superArgument.position()).type().equals(superArgument.type().type());
							})) {
						throw new ConsolidationError(subMethod.locationDecl().getLine(), subMethod.locationDecl().getCol(),
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

	public String extendsFrom() {
		return extendsFrom;
	}

	public void setExtendsFrom(String extendsFrom, int line, int col) throws IllegalBaseExtendError {
		if (extendsFrom.equals("Object") || extendsFrom.equals("IO")) {
			// El resto de clases base son detectadas y manejadas por el analizador
			// sintáctico.
			throw new IllegalBaseExtendError(extendsFrom);
		}
		this.extendsFrom = extendsFrom;
		this.locationExtendsFrom = new Location(line, col);
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
