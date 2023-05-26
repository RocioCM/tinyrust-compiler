package error.semantic.declarations;

import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.Location;

public class ConsolidationError extends SemanticalError {
	public ConsolidationError(Location loc, String message) {
		super(message, loc);
	}
}
