package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;

public class AttributeEntry extends VariableEntry {
	private boolean isPublic = false;

	public AttributeEntry(String name, Type type, int position, boolean isPublic) {
		super(name, type, position);
		this.isPublic = isPublic;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}
}
