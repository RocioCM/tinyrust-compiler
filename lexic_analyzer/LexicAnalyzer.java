package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LexicAnalyzer {
	private String currentLine = "";
	private int lineNumber = 0;
	private int columnNumber = 0;
	private Scanner file;

	public LexicAnalyzer(String filePath) {
		// Inicializar linea, columna y filereader.
		// Error si no puede leer el archivo.

		try {
			File doc = new File(filePath);
			Scanner obj = new Scanner(doc);
			file = obj;

			// // Hardcoded with testing purposes:
			// while (obj.hasNextLine()) {
			// currentLine = obj.nextLine();

			// for (int i = 0; i < currentLine.length(); i++) {
			// System.out.print((int) currentLine.charAt(i) + " ");
			// }
			// System.out.println(currentLine);
			// // Procesar el enter a mano al final de la linea.

			// }
			// Procesar el EOF a mano al final.
		} catch (FileNotFoundException error) {
			System.out.println("En términos de archivo, no hay archivo.");
		}

	}

	public Token nextToken() {
		Token token = new Token(/* lineNumber, columnNumber */);
		char currentChar = readConsumeChar();

		if (isValidChar(currentChar)) {
			// ASCIIs: https://www.ascii-code.com/
			// Acá hacemos un switch gigante.
			// Léase Autómata Finito Determinista.
			return token;
		}

		return null;
	}

	private boolean isValidChar(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar >= 32 && asciiChar <= 126 || asciiChar == 209 || asciiChar == 241 || asciiChar == 161
				|| asciiChar == 191 || asciiChar == 9) {
			return true;
		}
		return false;
	}

	private char readConsumeChar() {
		char currentChar;
		if (currentLine.length() > 0) {
			currentChar = currentLine.charAt(0);
			currentLine = currentLine.substring(1);
			columnNumber++;
		} else {
			if (file.hasNextLine()) {
				currentChar = (char) 13; // Carriage Return - ENTER
				currentLine = file.nextLine();
				lineNumber++;
				columnNumber = 0;
			} else {
				currentChar = (char) 3; // End of File - ETX
				// TODO - Handelear si llaman a esta función después del EOF con un flag o algo
				// Aprovechar para cerrar el file reader.
			}
		}
		return currentChar;
	}

	private char readWithoutConsumeChar() {
		char currentChar;
		if (currentLine.length() > 0) {
			currentChar = currentLine.charAt(0);
		} else {
			if (file.hasNextLine()) {
				currentChar = (char) 13; // Carriage Return - ENTER
			} else {
				currentChar = (char) 3; // End of File - ETX
			}
		}
		return currentChar;
	}

}
