package semantic_analyzer.symbol_table.types;

public abstract class PrimitiveType<T extends Object> extends Type {
	private T defaultValue;

	public PrimitiveType(String type, T defaultValue) {
		super(type);
		this.defaultValue = defaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}
}
