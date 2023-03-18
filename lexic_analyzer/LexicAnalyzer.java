package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import error.*;

public class LexicAnalyzer {
	private Scanner file;
	private String currentLine = "";
	private int lineNumber = 0;
	private int columnNumber = 0;
	private boolean reachedEOF = false;

	public LexicAnalyzer(String filePath) throws FileNotFoundException {
		// Abrir el archivo de entrada.
		File document = new File(filePath);
		Scanner scanner = new Scanner(document);
		this.file = scanner;

		// Inicializar la lectura de la primer linea del archivo.
		if (file.hasNextLine()) {
			currentLine = file.nextLine();
			lineNumber++;
		}
	}

	public boolean hasNextToken() {
		return !reachedEOF;
	}

	public Token nextToken() throws LexicalError {
		consumeSpaces(); // Consumir sin guardar los espacios, tabs y enter antes de un caracter valioso.
		char currentChar = readConsumeChar();
		Token token = new Token("", "" + currentChar, lineNumber, columnNumber);

		// ASCIIs: https://www.ascii-code.com/
		// Acá hacemos un switch gigante.
		// Léase Autómata Finito Determinista.

		// Leer el primer caracter del token y continuar según qué caracter es.
		switch (currentChar) {
			case '+': // Operador suma.
				token.setToken("op_add");
				break;

			case '*': // Operador multiplicación.
				token.setToken("op_prod");
				break;

			case '%': // Operador módulo.
				token.setToken("op_mod");
				break;

			case ';': // Delimitador de final de linea.
				token.setToken("semicolon");
				break;

			case ':': // Delimitador de tipo de variable.
				token.setToken("colon");
				break;

			case '{': // Llave de apertura de bloque.
				token.setToken("open_curly");
				break;

			case '}': // Llave de cierre de bloque.
				token.setToken("close_curly");
				break;

			case '(': // Paréntesis de apertura para parámetros o expresiones.
				token.setToken("open_par");
				break;

			case ')': // Paréntesis de cierre para parámetros o expresiones.
				token.setToken("close_par");
				break;

			case '[': // Corchete de apertura de arreglos.
				token.setToken("open_bracket");
				break;

			case ']': // Corchete de cierre de arreglos.
				token.setToken("open_bracket");
				break;

			case ',': // Separador de parámetros en funciones o arreglos.
				token.setToken("comma");
				break;

			case '.': // Punto para los métodos.
				token.setToken("dot");
				break;

			case '|': // Operador lógico OR
				if (!isOrToken(currentChar, token)) {
					throw new UnmatchedTokenError(token.getLine(), token.getCol(), token.getLexema());
				}
				break;

			case '&': // Operador lógico AND
				if (!isAndToken(currentChar, token)) {
					throw new UnmatchedTokenError(token.getLine(), token.getCol(), token.getLexema());
				}
				break;

			case '-': // Operador resta o tipo de retorno de función.
				// Op resta o ->
				break;

			case '/': // Operador división o comentario.
				// Op div o // o /*
				break;

			case '!': // Negación o comparación desigualdad.
				// ! o !=
				break;

			case '=': // Asignación o comparación igualdad.
				// = o ==
				break;

			case '<': // Operador menor / menor o igual.
				// < o <=
				break;

			case '>': // Operador mayor / mayor o igual.
				// > o >=
				break;

			default:
				if (isIdentifier(currentChar, token)) {
					ReservedWords.isReservedWord(token);
				}
				if (isIntLiteral(currentChar, token)) {
				}
				if (isStringLiteral(currentChar, token)) {
				}
		}

		return token;

	}

	private boolean isValidChar(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar >= 32 && asciiChar <= 126 || asciiChar == 209 || asciiChar == 241 || asciiChar == 161
				|| asciiChar == 191 || asciiChar == 9 || asciiChar == 10 || asciiChar == 11 || asciiChar == 13 || asciiChar == 3
				|| asciiChar == -1) {
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

	private boolean isOperator(char currentChar) {
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
			case ')': // Paréntesis para los parámetros.
			case '[': // Corchetes para los arreglos.
			case ']': // Corchetes para los arreglos.
			case '{': // Llaves para bloques.
			case '}': // Llaves para bloques.
			case ',':
			case '"':
			case '\'':
			case ';':
			case '.': // Punto para los métodos.
				return true;
			default:
				return false;
		}
	}

	private boolean isBlankSpace(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar == 32 // space
				|| asciiChar == 9 // tab
				|| asciiChar == 10 // new line
				|| asciiChar == 11 // vertical tab
				|| asciiChar == 13 // enter
				|| asciiChar == 3 // end of file
		) {
			return true;
		}
		return false;
	}

	private boolean isLineBreak(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar == 10 // new line
				|| asciiChar == 11 // vertical tab
				|| asciiChar == 13 // enter
				|| asciiChar == 3 // end of file
		) {
			return true;
		}
		return false;
	}

	private boolean isIntLiteral(char initialChar, Token token) throws InvalidCharacterError {
		if (!isDigit(initialChar)) {
			return false;
		}

		char nextChar = readWithoutConsumeChar();
		while (isDigit(nextChar)) {
			readConsumeChar();
			token.appendLexema(nextChar);
			nextChar = readWithoutConsumeChar();
		}
		token.setToken("lit_int");
		return true;
	}

	private boolean isStringLiteral(char initialChar, Token token) throws InvalidCharacterError, InvalidLiteralError {
		if (initialChar != '"') {
			return false;
		}

		char nextChar = readWithoutConsumeChar();
		while (!isLineBreak(nextChar) && nextChar != '"') {
			readConsumeChar();
			token.appendLexema(nextChar);
			nextChar = readWithoutConsumeChar();
		}
		if (nextChar == '"') {
			readConsumeChar();
			token.appendLexema(nextChar);
			token.setToken("lit_string");
		} else {
			throw new InvalidLiteralError(token.getLine(), token.getCol(), "LITERAL CADENA SIN CERRAR.");
		}

		return true;
	}

	private boolean isCharLiteral(char initialChar, Token token) {
		return true;
	}

	private boolean isOrToken(char initialChar, Token token) throws InvalidCharacterError {
		char nextChar = readWithoutConsumeChar();
		if (nextChar == '|') {
			readConsumeChar();
			token.setToken("op_or");
			token.appendLexema(nextChar);
			return true;
		}
		return false;
	}

	private boolean isAndToken(char initialChar, Token token) throws InvalidCharacterError {
		char nextChar = readWithoutConsumeChar();
		if (nextChar == '&') {
			readConsumeChar();
			token.setToken("op_and");
			token.appendLexema(nextChar);
			return true;
		}
		return false;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * identificador.
	 * Este método es básicamente un autómata con 4 estados
	 * 0: siendo el inicial
	 * 1: al que nos movemos si encontramos _ a letra minúscula - Aceptador
	 * 2: al que nos movemos si encontramos letra, dígito o _ - Aceptador
	 * 3: el rechazador mediante el cual arrojaremos el error de que no es un
	 * identificador.
	 * Vamos a frenar en un estado aceptador si venimos desde el estado 1 o 2 y nos
	 * topamos con un espacio o algún operador.
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    identificador.
	 * @param token       el token que vamos a llenar con el lexema y el token si es
	 *                    que es un identificador.
	 * @return true si es un identificador, false si no lo es.
	 */

	private boolean isIdentifier(char initialChar, Token token) throws InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 1, 2 };
		char currentChar = initialChar;
		String lexema = "";

		while (currentState != 3 && currentChar != ' ') {
			if (currentState == 0 && (isLowercaseChar(currentChar) || currentChar == '_')) {
				currentState = 1;
				lexema += currentChar;

				// Miramos un poco hacia adelante a ver si es operador o no el siguiente char.
				currentChar = readWithoutConsumeChar();
				if (!isOperator(currentChar)) {
					currentChar = readConsumeChar();
				}
			} else {
				if (currentState == 0 && isDigit(currentChar)) {
					currentState = 3;
					break;
				} else {
					if (currentState == 1 && (isAlphabet(currentChar) || isDigit(currentChar) || currentChar == '_')) {
						currentState = 1;
						lexema += currentChar;
						currentChar = readWithoutConsumeChar();
						if (!isOperator(currentChar)) {
							currentChar = readConsumeChar();
						}
					} else {
						if (currentState == 1 && isOperator(currentChar)) {
							currentState = 2;
							break;
						} else {
							currentState = 3;
							break;
						}
					}
				}
			}
		}

		if (Arrays.asList(successStates).contains(currentState)) {
			token.setLexema(lexema);
			token.setToken("id");
			return true;
		}
		return false; // Throw error identificador mal formado
	}

	private void consumeSpaces() throws InvalidCharacterError {
		char currentChar = readWithoutConsumeChar();
		while (isBlankSpace(currentChar) && !reachedEOF) {
			readConsumeChar();
			currentChar = readWithoutConsumeChar();
		}
	}

	private char readConsumeChar() throws InvalidCharacterError {
		char currentChar;
		if (reachedEOF) {
			currentChar = (char) 3; // End of File - ETX
		} else {
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
					reachedEOF = true;
					currentChar = (char) 3; // End of File - ETX
					file.close();
				}
			}
		}

		if (!isValidChar(currentChar)) {
			throw new InvalidCharacterError(lineNumber, columnNumber, currentChar);
		}
		return currentChar;
	}

	private char readWithoutConsumeChar() {
		char currentChar;
		if (reachedEOF) {
			currentChar = (char) 3; // End of File - ETX
		} else {
			if (currentLine.length() > 0) {
				currentChar = currentLine.charAt(0);
			} else {
				if (file.hasNextLine()) {
					currentChar = (char) 13; // Carriage Return - ENTER
				} else {
					currentChar = (char) 3; // End of File - ETX
				}
			}
		}
		// TODO - Evaluar si es necesario verificar isValidChar acá. Contra: line number
		return currentChar;
	}

}
