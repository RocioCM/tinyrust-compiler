package semantic_analyzer.symbol_table;

import util.Json;

public class ConstructorEntry extends MethodEntry {
	private boolean alreadyDeclared = false;

	public ConstructorEntry(boolean explicitDeclaration, Location loc) {
		super("constructor", false, 0, loc);
		this.alreadyDeclared = explicitDeclaration; // La declaración puede ser explícita o implícita.
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("argumentosFormales", super.arguments());
		return json.toString();
	}

	public boolean isAlreadyDeclared() {
		return alreadyDeclared;
	}
}
