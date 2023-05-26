package error.semantic.declarations;

import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.Location;

public class MultipleConstructorsError extends SemanticalError {
	public MultipleConstructorsError(Location loc) {
		super("SOLO SE PERMITE DECLARAR HASTA UN CONSTRUCTOR POR CLASE.", loc);
	}
}
