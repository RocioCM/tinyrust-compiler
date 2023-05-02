package semantic_analyzer.symbol_table.types;

public class Array extends Type {
	public Array() {
		super("Array");
	}

	public Array(Type type) {
		super("Array-" + type.type());
	}
}
