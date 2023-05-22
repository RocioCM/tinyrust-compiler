package error.semantic.declarations;

import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.Location;

public class BadFormedConstructorError extends SemanticalError {
	public BadFormedConstructorError(Location loc) {
		super("LA SINTAXIS CORRECTA PARA DECLARAR EL CONSTRUCTOR create ES \"create(<parametros>) {<sentencias>}\".",
				loc);
	}
}
