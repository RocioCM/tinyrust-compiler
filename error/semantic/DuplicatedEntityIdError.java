package error.semantic;

import semantic_analyzer.symbol_table.Location;

public class DuplicatedEntityIdError extends SemanticalError {

	public DuplicatedEntityIdError(String entityType, String id, Location loc) {
		super("UNA DECLARACION DE" + entityType + " CON EL NOMBRE \"" + id + "\" YA EXISTE EN ESTE AMBITO.", loc);
	}
}
