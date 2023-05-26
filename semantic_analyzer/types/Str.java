package semantic_analyzer.types;

public class Str extends PrimitiveType<String> {
	public Str() {
		super("Str", "");
	}

	public Str(String value) {
		super("Str", "", value);
	}
}
