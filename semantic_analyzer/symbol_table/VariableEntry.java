package semantic_analyzer.symbol_table;

import semantic_analyzer.types.Type;
import util.Json;

public class VariableEntry implements TableElement {
	protected String name;
	protected Type type;
	protected int position;

	public VariableEntry(String name, Type type, int position) {
		this.name = name;
		this.type = type;
		this.position = position;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("posicion", position);
		json.addAttr("tipo", type);
		return json.toString();
	}

	public String name() {
		return name;
	}

	public Type type() {
		return type;
	}

	public int position() {
		return position;
	}
}
