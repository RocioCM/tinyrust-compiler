package error.semantic;

public class SemanticalError extends Exception {
	public SemanticalError(String message) {
		super(message); // Linea y columna son agregados posteriormente.
	}

	public SemanticalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
