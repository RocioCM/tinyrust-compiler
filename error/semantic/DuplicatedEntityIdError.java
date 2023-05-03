package error.semantic;

public class DuplicatedEntityIdError extends SemanticalError {

	public DuplicatedEntityIdError(String entityType, String id) {
		super("UNA DECLARACION DE" + entityType + " CON EL NOMBRE \"" + id + "\" YA EXISTE EN ESTE AMBITO.");
	}
}
