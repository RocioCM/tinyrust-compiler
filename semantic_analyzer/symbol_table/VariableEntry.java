package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;

public class VariableEntry implements TableElement {
	private String name;
	private Type type;
	private int position;

	public VariableEntry(String name, Type type, int position) {
		this.name = name;
		this.type = type;
		this.position = position;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}
}
