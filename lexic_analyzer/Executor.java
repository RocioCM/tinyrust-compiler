package lexic_analyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import error.LexicalError;
import util.Logger;

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
			Logger.lexicSuccess(tokensList);

		} catch (FileNotFoundException error) {
			System.out.println("\nERROR: no se encontró un archivo en la ruta " + inputPath);
		} catch (LexicalError error) {
			Logger.lexicError(error);
		}
	}
}
