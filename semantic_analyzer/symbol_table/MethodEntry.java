package semantic_analyzer.symbol_table;

import java.util.Iterator;

import error.semantic.ConsolidationError;
import error.semantic.DuplicatedEntityIdError;
import error.semantic.IllegalSelfDeclarationError;
import semantic_analyzer.types.Type;
import semantic_analyzer.types.Void;
import util.Json;

public class MethodEntry implements TableElement {
	private String name;
	private Location locationDecl;
	private int position;
	private Type returnType;
	private boolean isStatic = false;
	private TableList<ArgumentEntry> arguments;

	public MethodEntry(String name, boolean isStatic, int position, Location loc) {
		// Inicialización por defecto.
		this.name = name;
		this.position = position;
		this.isStatic = isStatic;
		this.returnType = new Void();
		this.arguments = new TableList<ArgumentEntry>();
		this.locationDecl = loc;
	}

	public MethodEntry(MethodEntry method, int position) {
		// Clonar instancia.
		this.name = method.name;
		this.position = position;
		this.isStatic = method.isStatic;
		this.returnType = method.returnType;
		this.arguments = new TableList<ArgumentEntry>(method.arguments);
		this.locationDecl = method.locationDecl;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("posicion", position);
		json.addAttr("static", isStatic);
		json.addAttr("tipoRetorno", returnType);
		json.addAttr("argumentosFormales", arguments);
		return json.toString();
	}

	public void addArgument(String name, Type type, Location loc)
			throws IllegalSelfDeclarationError, DuplicatedEntityIdError {
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError(loc);
		}
		if (arguments.containsKey(name)) {
			throw new DuplicatedEntityIdError("L PARAMETRO FORMAL", name, loc);
		}

		ArgumentEntry arg = new ArgumentEntry(name, type, arguments.size() + 1, loc);
		arguments.put(name, arg);
	}

	/**
	 * Durante la etapa de consolidación, este método valida que el tipo de cada
	 * argumento formal del método se corresponda con una clase declarada.
	 */
	public void validateArgumentTypes(TableList<ClassEntry> classes) throws ConsolidationError {
		Iterator<ArgumentEntry> argumentsIter = arguments.values().iterator();
		while (argumentsIter.hasNext()) { // Iterar sobre cada método de la clase.
			ArgumentEntry arg = argumentsIter.next();
			if (classes.get(arg.type().type()) == null) {
				// Lanzar error si la clase del argumento no está declarada.
				throw new ConsolidationError(
						arg.locationDecl(),
						"EL ARGUMENTO " + arg.name() + " DEL METODO " + name + " ES DEL TIPO NO DECLARADO " + arg.type().type());
			}
		}
	}

	public String name() {
		return name;
	}

	public Type returnType() {
		return returnType;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	protected TableList<ArgumentEntry> arguments() {
		return arguments;
	}

	public Location locationDecl() {
		return locationDecl;
	}
}
