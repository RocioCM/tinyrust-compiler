package error.semantic;

import semantic_analyzer.symbol_table.Location;

public class SemanticalError extends Exception {
	public SemanticalError(String message) {
		super(message); // Excepciones internas no incluyen fila y columna.
	}

	public SemanticalError(String message, Location loc) {
		super("| LINEA " + loc.getLine() + " | COLUMNA " + loc.getCol() + " | " + message + " |");
	}

	public SemanticalError(int line, int column, String message) {
		super("| LINEA " + line + " | COLUMNA " + column + " | " + message + " |");
	}
}
