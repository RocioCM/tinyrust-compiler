package semantic_analyzer.types;

public abstract class PrimitiveType<T> extends Type {
	private T defaultValue;
	private T value;

	public PrimitiveType(String type, T defaultValue) {
		super(type);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public PrimitiveType(String type, T defaultValue, T value) {
		super(type);
		this.defaultValue = defaultValue;
		this.value = value;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public T value() {
		return value;
	}
}
