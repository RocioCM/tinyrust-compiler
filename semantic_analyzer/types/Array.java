package semantic_analyzer.types;

public class Array extends Type {
	public Array() {
		super("Array");
	}

	public Array(Type type) {
		super("Array-" + type.type());
	}
}
