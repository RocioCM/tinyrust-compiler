package semantic_analyzer.types;

public class Char extends PrimitiveType<Character> {
	public Char() {
		super("Char", ' ');
	}

	public Char(Character value) {
		super("Char", ' ', value);
	}
}
