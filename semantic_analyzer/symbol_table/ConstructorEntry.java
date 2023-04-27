package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Void;

public class ConstructorEntry extends MethodEntry {

	public ConstructorEntry() {
		super("constructor", new Void());
	}
}
