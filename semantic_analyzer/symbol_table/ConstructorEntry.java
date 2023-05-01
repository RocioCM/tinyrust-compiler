package semantic_analyzer.symbol_table;

import util.Json;

public class ConstructorEntry extends MethodEntry {
	public ConstructorEntry() {
		super("constructor", false, 0);
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("argumentosFormales", super.getArguments());
		return json.toString();
	}
}
