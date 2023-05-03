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
		if (name == "Object" || name == "main") {
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

	public AttributeEntry addAttribute(String name, Type type, boolean isPublic)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError {
		if (name == "self") {
			throw new IllegalSelfDeclarationError();
		}
		if (attributes.containsKey(name)) {
			throw new DuplicatedEntityIdError("L ATRIBUTO", name);
		}

		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, isPublic);
		attributes.put(name, attr);
		return attr;
	}

	public MethodEntry addMethod(String name, boolean isStatic)
			throws DuplicatedEntityIdError, IllegalSelfDeclarationError, BadFormedConstructorError {
		if (methods.containsKey(name)) {
			throw new DuplicatedEntityIdError("L METODO", name);
		}
		if (name == "self") {
			throw new IllegalSelfDeclarationError();
		}
		if (name == "create") {
			throw new BadFormedConstructorError();
		}

		MethodEntry method = new MethodEntry(name, isStatic, methods.size() + 1);
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
					throw new ConsolidationError("NO ESTA PERMITIDO REDEFINIR ATRIBUTOS DE UNA SUPERCLASE");
				} else {
					// El atributo no se redefine, entonces se agrega a la subclase.
					AttributeEntry newAttr = new AttributeEntry(superAttr, attributes.size()); // Clonar
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
					if (superMethod.isStatic()) {
						// No es válido redeclarar métodos estáticos.
						throw new ConsolidationError("NO ESTA PERMITIDO REDEFINIR METODOS ESTATICOS DE UNA SUPERCLASE");
					}

					if (!(superMethod.returnType().equals(subMethod.returnType()) // Mismo tipo de retorno.
							&& superMethod.arguments().size() == subMethod.arguments().size() // Misma cant. de argumentos.
					)) {
						// Por practicidad, se arma un mapeo de posición y tipo de cada argumento del
						// método de la subclase.
						HashMap<Number, Type> subMethodArgTypes = new HashMap<Number, Type>();
						subMethod.arguments().values().forEach((arg) -> {
							subMethodArgTypes.put(arg.position(), arg.type());
						});
						if (superMethod.arguments().values().stream() // Validar misma posición y tipo de cada argumento.
								.anyMatch((superArgument) -> subMethodArgTypes.get(superArgument.position()) != superArgument.type())) {
							// No es válido redeclarar métodos del mismo nombre con distinta firma.
							throw new ConsolidationError(
									"NO ESTA PERMITIDO REDEFINIR METODOS DE UNA SUPERCLASE CON DISTINTA FIRMA (TIPO DE RETORNO Y CANTIDAD, TIPO Y ORDEN DE ARGUMENTOS)");
						}
					}
				} else {
					// El método no se redefine, entonces se agrega a la subclase.
					MethodEntry newMethod = new MethodEntry(superMethod, methods.size()); // Clonar
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

	public void setExtendsFrom(String extendsFrom) throws IllegalBaseExtendError {
		if (extendsFrom == "Object" || extendsFrom == "IO") {
			// El resto de clases base son detectadas y manejadas por el analizador
			// sintáctico.
			throw new IllegalBaseExtendError(extendsFrom);
		}
		this.extendsFrom = extendsFrom;
	}

	public boolean isConsolidated() {
		return consolidated;
	}

	public void setConsolidated(boolean consolidated) {
		this.consolidated = consolidated;
	}

}
