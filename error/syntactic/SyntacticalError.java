package error.syntactic;

public abstract class SyntacticalError extends Exception {
	public SyntacticalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
