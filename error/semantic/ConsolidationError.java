package error.semantic;

public class ConsolidationError extends SemanticalError {
	public ConsolidationError(int line, int col, String message) {
		super(line, col, message);
	}
}
