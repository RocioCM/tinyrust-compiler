package semantic_analyzer.symbol_table;

import error.semantic.DuplicatedEntityIdError;
import error.semantic.IllegalSelfDeclarationError;
import semantic_analyzer.symbol_table.types.Type;
import semantic_analyzer.symbol_table.types.Void;
import util.Json;

public class MethodEntry implements TableElement {
	private String name;
	private int position;
	private Type returnType;
	private boolean isStatic = false;
	private TableList<ArgumentEntry> arguments;

	public MethodEntry(String name, boolean isStatic, int position) {
		this.name = name;
		this.position = position;
		this.isStatic = isStatic;
		this.returnType = new Void();
		this.arguments = new TableList<ArgumentEntry>();
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

	public void addArgument(String name, Type type) throws IllegalSelfDeclarationError, DuplicatedEntityIdError {
		if (name == "self") {
			throw new IllegalSelfDeclarationError();
		}
		if (arguments.containsKey(name)) {
			throw new DuplicatedEntityIdError("L PARAMETRO FORMAL", name);
		}

		ArgumentEntry arg = new ArgumentEntry(name, type, arguments.size() + 1);
		arguments.put(name, arg);
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
}
