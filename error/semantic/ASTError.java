package error.semantic;

public class ASTError extends SemanticalError {
	public ASTError(int line, int column, String message) {
		super(line, column, message);
	}
}
