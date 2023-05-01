package error.semantic;

public class InternalError extends SemanticalError {
	public InternalError(int line, int column, String message) {
		super(line, column, "ERROR INTERNO: " + message);
	}
}
