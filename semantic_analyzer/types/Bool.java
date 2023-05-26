package semantic_analyzer.types;

public class Bool extends PrimitiveType<Boolean> {
	public Bool() {
		super("Bool", false);
	}

	public Bool(Boolean value) {
		super("Bool", false, value);
	}
}
