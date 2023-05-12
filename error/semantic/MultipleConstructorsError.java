package error.semantic;

import semantic_analyzer.symbol_table.Location;

public class MultipleConstructorsError extends SemanticalError {
	public MultipleConstructorsError(Location loc) {
		super("SOLO SE PERMITE DECLARAR HASTA UN CONSTRUCTOR POR CLASE.", loc);
	}
}
