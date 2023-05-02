package semantic_analyzer.symbol_table;

import java.util.HashMap;

import error.semantic.ConsolidationError;
import error.semantic.DuplicatedEntityIdError;
import error.semantic.IllegalBaseExtendError;
import error.semantic.IllegalSelfDeclarationError;
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
		this.constructor = new ConstructorEntry();
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

	public MethodEntry addMethod(String name, boolean isStatic) throws DuplicatedEntityIdError {
		if (methods.containsKey(name)) {
			throw new DuplicatedEntityIdError("L METODO", name);
		}

		MethodEntry method = new MethodEntry(name, isStatic, methods.size() + 1);
		methods.put(name, method);
		return method;
	}

	public ConstructorEntry addConstructor() {
		ConstructorEntry constructor = new ConstructorEntry();
		this.constructor = constructor;
		return constructor;
	}

	public void consolidate(HashMap<String, ClassEntry> superClassesTree) throws ConsolidationError {
		if (!isConsolidated() && extendsFrom != null) {
			ClassEntry superClass = superClassesTree.get(name);
			superClass.consolidate(superClassesTree);

			// Consolidar atributos.
			superClass.attributes().forEach((name, superAttr) -> {
				if (attributes.containsKey(name)) {
					// No es válido redeclarar atributos.
					/// TODO throw new ConsolidationError("no se puede redefinir attrs");
				} else {
					// Se agrega a la subclase el atributo de la superclase.
					attributes.put(name, superAttr);
				}
			});

			// Consolidar métodos.
			superClass.methods().forEach((name, superMethod) -> {
				if (methods.containsKey(name)) {
					// Si la subclase redeclara un método, se valida que la firma coincida.
					MethodEntry subMethod = methods.get(name);
					if (superMethod.isStatic()) {
						// No es válido redeclarar métodos estáticos.
						/// TODO throw new ConsolidationError("no se puede redefinir un metodo
						/// estatico");
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
								.allMatch((superArgument) -> subMethodArgTypes.get(superArgument.position()) == superArgument.type())) {
							// No es válido redeclarar métodos del mismo nombre con distinta firma.
							/// TODO throw new ConsolidationError("no se puede redefinir un metodo
							/// con distinta firma);
						}
					}
				} else {
					// Se agrega a la subclase el método de la superclase.
					methods.put(name, superMethod);
				}
			});
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
