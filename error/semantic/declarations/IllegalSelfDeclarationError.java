package error.semantic.declarations;

import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.Location;

public class IllegalSelfDeclarationError extends SemanticalError {

	public IllegalSelfDeclarationError(Location loc) {
		super("NO ESTA PERMITIDO ASIGNAR EL IDENTIFICADOR \"self\" A ATRIBUTOS, VARIABLES O METODOS.", loc);
	}

}
