package error.lexic;

public class InvalidLiteralError extends LexicalError {
	public InvalidLiteralError(int line, int column, String message) {
		super(line, column, "LITERAL MAL FORMADO: " + message);
	}
}
