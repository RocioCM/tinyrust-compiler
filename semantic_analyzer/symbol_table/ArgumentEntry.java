package semantic_analyzer.symbol_table;

import semantic_analyzer.types.Type;

public class ArgumentEntry extends VariableEntry {
	public ArgumentEntry(String name, Type type, int position, Location loc) {
		super(name, type, position, loc);
	}
}
