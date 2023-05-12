package error.semantic;

import semantic_analyzer.symbol_table.Location;

public class IllegalSelfDeclarationError extends SemanticalError {

	public IllegalSelfDeclarationError(Location loc) {
		super("NO ESTA PERMITIDO ASIGNAR EL IDENTIFICADOR \"self\" A ATRIBUTOS, VARIABLES O METODOS.", loc);
	}

}
