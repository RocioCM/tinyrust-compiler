package error.syntactic;

public class PlaceholderError extends SyntacticalError {
	public PlaceholderError(int line, int column) {
		super(line, column, "SOME ERROR");
	}
}
