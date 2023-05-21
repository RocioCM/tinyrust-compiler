package error.semantic.sentences;

import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.types.Type;

public class UnmatchedTypeError extends ASTError {

	public UnmatchedTypeError(Location loc, Type expected, Type resolved) {
		super(loc,
				"SE ESPERABA UNA EXPRESION DE TIPO " + expected.type() + " PERO SE ENCONTRO UNA EXPRESION DE TIPO "
						+ resolved);
	}

}
