package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LexicAnalyzer {
	private int line = 0;
	private int column = 0;
	private Scanner file;

	public LexicAnalyzer(String filePath) {
		// Inicializar linea, columna y filereader.
		// Error si no puede leer el archivo.

		try {

			File doc = new File(filePath);
			Scanner obj = new Scanner(doc);
			file = obj;
			while (obj.hasNextLine()) {

				System.out.println(obj.nextLine());
			}

		} catch (FileNotFoundException error) {
			System.out.println("En términos de archivo, no hay archivo.");

		}

	}

	public Token nextToken() {
		// Primero validar que el caracter esté en el alfabeto.
		// Acá hacemos un switch gigante.
		return new Token();
	}

	private char readConsumeChar() {
		return 'a';
	}

	private char readWithoutConsumeChar() {
		return 'a';
	}

}
