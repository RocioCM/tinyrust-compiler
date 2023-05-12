package error.semantic;

import semantic_analyzer.symbol_table.Location;

public class ConsolidationError extends SemanticalError {
	public ConsolidationError(Location loc, String message) {
		super(message, loc);
	}
}
