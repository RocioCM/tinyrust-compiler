package error.semantic;

public class IllegalBaseExtendError extends SemanticalError {
	public IllegalBaseExtendError(String className) {
		super("NO ESTA PERMITIDO HEREDAR DE LA CLASE PREDEFINIDA " + className);
	}
}
