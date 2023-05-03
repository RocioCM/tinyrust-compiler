package semantic_analyzer.symbol_table;

import util.Json;

public class ConstructorEntry extends MethodEntry {
	private boolean alreadyDeclared = false;

	public ConstructorEntry(boolean explicitDeclaration) {
		super("constructor", false, 0, -1, -1);
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
