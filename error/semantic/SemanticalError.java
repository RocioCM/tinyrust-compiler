package error.semantic;

public class SemanticalError extends Exception {
	public SemanticalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
