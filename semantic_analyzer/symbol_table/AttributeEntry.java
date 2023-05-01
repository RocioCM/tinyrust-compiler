package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;
import util.Json;

public class AttributeEntry extends VariableEntry {
	private boolean isPublic = false;

	public AttributeEntry(String name, Type type, int position, boolean isPublic) {
		super(name, type, position);
		this.isPublic = isPublic;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("posicion", position);
		json.addAttr("public", isPublic);
		json.addAttr("tipo", type);
		return json.toString();
	}
}
