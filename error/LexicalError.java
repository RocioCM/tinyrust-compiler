package error;

public abstract class LexicalError extends Exception {
	public LexicalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
