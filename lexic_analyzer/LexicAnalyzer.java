package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class LexicAnalyzer {
	private String currentLine = "";
	private int lineNumber = 1;
	private int columnNumber = 1;
	private Scanner file;

	public LexicAnalyzer(String filePath) {
		// Inicializar linea, columna y filereader.
		// Error si no puede leer el archivo.
		try {
			File doc = new File(filePath);
			Scanner obj = new Scanner(doc);
			file = obj;
			currentLine = obj.nextLine();

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
		Token token = new Token("", "", lineNumber, columnNumber);
		char currentChar = readConsumeChar();

		if (isValidChar(currentChar)) {
			// ASCIIs: https://www.ascii-code.com/
			// Acá hacemos un switch gigante.
			// Léase Autómata Finito Determinista.
			if(isIdentificator(currentChar, token)){
				ReservedWords.isReservedWord(token);
				System.out.println("Token: " + token.getToken() + " | Lexema: " + token.getLexema() + " | Linea: " + token.getLine() + " | Columna: " + token.getCol());
			}
			// System.out.println(currentLine);
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

	private boolean isLowercaseChar(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar >= 97 && asciiChar <= 122) {
			return true;
		}
		return false;
	}

	private boolean isAlphabet(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar >= 65 && asciiChar <= 90 || asciiChar >= 97 && asciiChar <= 122) {
			return true;
		}
		return false;
	}

	private boolean isDigit(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar >= 48 && asciiChar <= 57) {
			return true;
		}
		return false;
	}

	private boolean isOperator(char currentChar){
		switch (currentChar) {
			case '+':
			case '-':
			case '*':
			case '/':
			case '%':
			case '=':
			case '!':
			case '<':
			case '>':
			case '&':
			case '|': 
			case '(': // Paréntesis para los parámetros.
			case '[': // Corchetes para los arreglos.
			case '.': // Punto para los métodos.
				return true;
			default:
				return false;
		}
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un identificador.
	 * Este método es básicamente un autómata con 4 estados
	 * 0: siendo el inicial
	 * 1: al que nos movemos si encontramos _ a letra minúscula - Aceptador
	 * 2: al que nos movemos si encontramos letra, dígito o _ - Aceptador
	 * 3: el rechazador mediante el cual arrojaremos el error de que no es un identificador.
	 * Vamos a frenar en un estado aceptador si venimos desde el estado 1 o 2 y nos topamos con un espacio o algún operador.
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un identificador.
	 * @param token el token que vamos a llenar con el lexema y el token si es que es un identificador.
	 * @return true si es un identificador, false si no lo es.
	 */

	private boolean isIdentificator(char initialChar, Token token) {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = {1,2}; 
		char currentChar = initialChar;
		String lexema = "";

		while(currentState != 3 && currentChar != ' '){
			if(currentState == 0 && (isLowercaseChar(currentChar) || currentChar == '_')){
				currentState = 1;
				lexema += currentChar;

				//Miramos un poco hacia adelante a ver si es operador o no el siguiente char.
				currentChar = readWithoutConsumeChar();
				if(!isOperator(currentChar)){
					currentChar = readConsumeChar();
				}
			}else{
				if(currentState == 0 && isDigit(currentChar)){
					currentState = 3;
					break;
				}else {
					if(currentState == 1 && (isAlphabet(currentChar) || isDigit(currentChar) || currentChar == '_')){
						currentState = 1;
						lexema += currentChar;
						currentChar = readWithoutConsumeChar();
						if(!isOperator(currentChar)){
							currentChar = readConsumeChar();
						}
					}else {
						if(currentState == 1 && isOperator(currentChar)){
							currentState = 2;
							break;
						}else{
							currentState = 3;
							break;
						}
					}
				}
			}
		}

		if(Arrays.asList(successStates).contains(currentState)){	
			token.setLexema(lexema);
			token.setToken("id");
			return true;
		}
		return false; //Throw error identificador mal formado
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
				columnNumber = 1;
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
