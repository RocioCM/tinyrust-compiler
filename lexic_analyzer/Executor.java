package lexic_analyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import error.lexic.LexicalError;
import util.Logger;

/**
 * Ejecutor provisional que instancia el analizador léxico y solicita tokens
 * hasta agotar el archivo fuente.
 */
public class Executor {
	public void run(String inputPath, String outputPath) {
		ArrayList<Token> tokensList = new ArrayList<Token>(); // Lista dinámica para guardar los tokens.

		try {
			// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
			LexicAnalyzer lexic = new LexicAnalyzer(inputPath);

			// Identificar todos los tokens del archivo de entrada.
			while (lexic.hasNextToken()) {
				tokensList.add(lexic.nextToken());
			}

			// Imprimir lista de tokens del archivo.
			Logger.lexicSuccess(tokensList, outputPath);

		} catch (FileNotFoundException error) {
			System.out.println("\nERROR IO: NO SE ENCONTRO UN ARCHIVO DE ENTRADA EN LA RUTA " + inputPath);
			System.exit(1);
		} catch (LexicalError error) {
			Logger.lexicError(error, outputPath);
			System.exit(1);
		}
	}
}
