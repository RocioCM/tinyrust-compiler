package semantic_analyzer.types;

public abstract class PrimitiveType<T> extends Type {
	private T defaultValue;

	public PrimitiveType(String type, T defaultValue) {
		super(type);
		this.defaultValue = defaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}
}
