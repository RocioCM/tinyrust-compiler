package semantic_analyzer.symbol_table;

import java.util.Iterator;

import error.semantic.declarations.ConsolidationError;
import error.semantic.declarations.DuplicatedEntityIdError;
import error.semantic.declarations.IllegalSelfDeclarationError;
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
	private TableList<VariableEntry> blockVariables;

	public MethodEntry(String name, boolean isStatic, int position, Type returnType, Location loc) {
		// Inicialización por defecto.
		this.name = name;
		this.position = position;
		this.isStatic = isStatic;
		this.returnType = returnType;
		this.arguments = new TableList<ArgumentEntry>();
		this.blockVariables = new TableList<VariableEntry>();
		this.locationDecl = loc;
	}

	public MethodEntry(String name, boolean isStatic, int position, Location loc) {
		// Inicialización por defecto.
		this.name = name;
		this.position = position;
		this.isStatic = isStatic;
		this.returnType = new Void();
		this.arguments = new TableList<ArgumentEntry>();
		this.blockVariables = new TableList<VariableEntry>();
		this.locationDecl = loc;
	}

	public MethodEntry(MethodEntry method, int position) {
		// Clonar instancia.
		this.name = method.name;
		this.position = position;
		this.isStatic = method.isStatic;
		this.returnType = method.returnType;
		this.arguments = new TableList<ArgumentEntry>(method.arguments);
		this.blockVariables = new TableList<VariableEntry>(method.blockVariables);
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
		json.addAttr("variablesBloque", blockVariables);
		return json.toString();
	}

	/** Agregar argumento formal al método. */
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
	 * Agregar declaración de variable de bloque al método. Estas variables pueden
	 * ser utilizadas posteriormente por el AST durante la consolidación.
	 */
	public void addVar(String name, Type type, Location loc)
			throws IllegalSelfDeclarationError, DuplicatedEntityIdError {
		if (name.equals("self")) {
			throw new IllegalSelfDeclarationError(loc);
		}
		if (blockVariables.containsKey(name)) {
			throw new DuplicatedEntityIdError(" LA VARIABLE", name, loc);
		}
		if (arguments.containsKey(name)) {
			throw new DuplicatedEntityIdError(" UN ARGUMENTO", name, loc);
		}

		VariableEntry newVar = new VariableEntry(name, type, blockVariables.size() + 1, loc);
		blockVariables.put(name, newVar);
	}

	/**
	 * Durante la etapa de consolidación, este método valida que el tipo de cada
	 * argumento formal, el tipo de retorno y el tipo de cada variable
	 * declarada en el bloque se correspondan con una clase declarada.
	 */
	public void validate(TableList<ClassEntry> classes) throws ConsolidationError {
		// 1. Validar el tipo de retorno.
		if (returnType.type() != "void" && classes.get(returnType.type()) == null) {
			// Lanzar error si la clase de retorno no está declarada.
			throw new ConsolidationError(locationDecl,
					"LA CLASE DE RETORNO DEL METODO " + name + " ES DEL TIPO NO DECLARADO " + returnType);
		}

		// 2. Validar el tipo de los argumentos.
		Iterator<ArgumentEntry> argumentsIter = arguments.values().iterator();
		while (argumentsIter.hasNext()) { // Iterar sobre cada método de la clase.
			ArgumentEntry arg = argumentsIter.next();
			if (classes.get(arg.type().type()) == null) {
				// Lanzar error si la clase del argumento no está declarada.
				throw new ConsolidationError(
						arg.locationDecl(),
						"EL ARGUMENTO " + arg.name() + " DEL METODO " + name + " ES DEL TIPO NO DECLARADO "
								+ arg.type().type());
			}
		}

		// 3. Validar el tipo de las variables del bloque.
		Iterator<VariableEntry> varsIter = blockVariables.values().iterator();
		while (varsIter.hasNext()) { // Iterar sobre cada método de la clase.
			VariableEntry var = varsIter.next();
			if (classes.get(var.type().type()) == null) {
				// Lanzar error si la clase de la variable no está declarada.
				throw new ConsolidationError(
						var.locationDecl(),
						"LA VARIABLE " + var.name() + " EN EL BLOQUE DEL METODO " + name + " ES DEL TIPO NO DECLARADO "
								+ var.type().type());
			}
		}
	}

	/**
	 * Devuelve la variable con el nombre dado si existe como variable del bloque o
	 * argumento formal del método. De no hallarse devuelve null.
	 * 
	 * @see - Validación de AST
	 * @param name - nombre de la variable
	 * @return Variable hallada o null si no existe.
	 */
	public VariableEntry getVariable(String name) {
		VariableEntry var = blockVariables.get(name); // Buscar la variable en el bloque.
		if (var == null) {
			var = arguments.get(name); // Buscar la variable como argumento formal.
		}
		return var; // Retornar la variable o null si no se halló.
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

	public TableList<ArgumentEntry> arguments() {
		return arguments;
	}

	public Location locationDecl() {
		return locationDecl;
	}
}
