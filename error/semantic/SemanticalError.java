package error.semantic;

public class SemanticalError extends Exception {
	public SemanticalError(String message) {
		super(message);
	}

	public SemanticalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
