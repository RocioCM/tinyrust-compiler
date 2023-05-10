package semantic_analyzer.types;

import semantic_analyzer.symbol_table.TableElement;

public abstract class Type implements TableElement {
	private String type;

	protected Type(String type) {
		this.type = type;
	}

	@Override
	public String toJson() {
		return '"' + type + '"';
	}

	public String type() {
		return type;
	}
}
