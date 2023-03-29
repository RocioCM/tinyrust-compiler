package syntactic_analyzer;

import java.io.FileNotFoundException;

import error.lexic.LexicalError;
import error.syntactic.PlaceholderError;
import error.syntactic.SyntacticalError;
import util.Logger;

/**
 * Ejecutor provisional que instancia el analizador sintáctico y solicita tokens
 * hasta agotar el archivo fuente.
 */
public class Executor {
	public void run(String inputPath, String outputPath) {
		try {
			// Patrón Singleton: se utiliza una única instancia del analizador.
			SyntacticAnalizer syntactic = new SyntacticAnalizer(inputPath);

			boolean succeed = syntactic.run();

			if (succeed) {
				Logger.syntacticSuccess(outputPath);
			} else {
				// Esto nunca va a ejecutar,
				// porque el método run() lanza una excepción en caso de error.
				Logger.syntacticError(new PlaceholderError(1, 1), outputPath);
			}
		} catch (FileNotFoundException error) {
			System.out.println("\nERROR IO: NO SE ENCONTRO UN ARCHIVO DE ENTRADA EN LA RUTA " + inputPath);
			// System.exit(1); /// TODO: uncomment these exits.
		} catch (LexicalError error) {
			Logger.lexicError(error, outputPath);
			// System.exit(1);
		} catch (SyntacticalError error) {
			Logger.syntacticError(error, outputPath);
			// System.exit(1);
		}
	}
}
