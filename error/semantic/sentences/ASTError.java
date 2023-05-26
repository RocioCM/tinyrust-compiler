package error.semantic.sentences;

import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.Location;

public class ASTError extends SemanticalError {
	public ASTError(Location location, String message) {
		super(location.getLine(), location.getCol(), message);
	}

	public ASTError(int line, int column, String message) {
		super(line, column, message);
	}
}
