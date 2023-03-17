import java.util.ArrayList;
import java.io.FileNotFoundException;

import lexic_analyzer.*;

class TinyRustCompiler {
	public static void main(String[] args) {
		String inputPath = "./tests/test1.rs"; // TODO - Leer el path de entrada del comando.
		ArrayList<Token> tokensList = new ArrayList<Token>(); // Lista dinámica para guardar los tokens.

		try {
			// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
			LexicAnalyzer lexic = new LexicAnalyzer(inputPath);

			// Identificar todos los tokens del archivo de entrada.
			while (lexic.hasNextToken()) {
				tokensList.add(lexic.nextToken());
			}

			// Imprimir lista de tokens del archivo.
			System.out.println("CORRECTO: LEXICO ");
			tokensList
					.forEach(
							token -> System.out.println(
									"Token: " + token.getToken() +
											" | Lexema: " + token.getLexema() +
											" | Linea: " + token.getLine() +
											" | Columna: " + token.getCol()));
			// TODO - Enviar el Standard Output al archivo de salida (del comando).

		} catch (FileNotFoundException error) {
			System.out.println("\nERROR: no se encontró un archivo en la ruta " + inputPath);
		} catch (LexicalError error) {
			System.out.println("\nERROR LEXICO: " + error.getMessage());
		}
		// TODO - Usar el logger para los errores. Completar la interfaz de los errores.
	}
}