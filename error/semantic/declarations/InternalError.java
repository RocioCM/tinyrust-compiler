package error.semantic.declarations;

import error.semantic.SemanticalError;

/**
 * Esta excepción lanzada durante el análisis semántico indica un bug en el
 * código del compilador. Ningún archivo de entrada debería generar este error
 * bajo ninguna condición. Pero los programadores somos humanos y esta clase
 * existe para manejar mis propios bugs con gracia.
 */
public class InternalError extends SemanticalError {
	public InternalError(String message) {
		super("ERROR INTERNO: " + message);
	}
}
