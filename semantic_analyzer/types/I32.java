package semantic_analyzer.types;

public class I32 extends PrimitiveType<Integer> {
	public I32() {
		super("I32", 0);
	}

	public I32(Integer value) {
		super("I32", 0, value);
	}
}
