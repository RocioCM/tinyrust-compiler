package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import error.BadIdentifierError;
import error.InvalidCharacterError;
import error.LexicalError;
import error.UnclosedMultiLineCommentError;

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
			default:
				if(currentChar == '/'){
					if(!isMultilineComment(currentChar, token)){
						isComment(currentChar, token);
					}
				}
				if(isLowercaseChar(currentChar) || isAlphabet(currentChar)) {
					if(isIdentifier(currentChar, token)){
						ReservedWords.isReservedWord(token);
					}
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
				|| asciiChar == 191 || asciiChar == 9 || asciiChar == 13 || asciiChar == 3 || asciiChar == -1) {
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
			case ';':
			case '.': // Punto para los métodos.
				return true;
			default:
				return false;
		}
	}

	private boolean isSpaceOrFormat(char currentChar) {
		int asciiChar = (int) currentChar;
		if (asciiChar == 32 // space
				|| asciiChar == 9 // tab
				|| asciiChar == 13 // enter
		) {
			return true;
		}
		return false;
	}

	private boolean isIntLiteral(char initialChar, Token token) {
		return true;
	}

	private boolean isStringLiteral(char initialChar, Token token) {
		return true;
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

	private boolean isIdentifier(char initialChar, Token token) throws BadIdentifierError, InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 1, 2 };
		char currentChar = initialChar;
		String lexema = "";
		
		while (currentState != 3 && !isSpaceOrFormat(currentChar)) {
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
		}else{
			throw new BadIdentifierError(lineNumber, columnNumber, lexema);
		}
	}

	private boolean isMultilineComment(char initialChar, Token token) throws UnclosedMultiLineCommentError, InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 4 };
		char currentChar = initialChar;
		String lexema = "";

		//Verificamos que el siguiente char sea un asterisco para comenzar el comentario multilinea
		if(readWithoutConsumeChar() == '*'){
			while(currentState != 5){
				if(reachedEOF){
					currentState =5;
					break;
				}
				if(currentState == 0 && currentChar == '/'){
					currentState = 1;
					lexema += currentChar;
					currentChar = readConsumeChar();
				} else {
					if(currentState == 1 && currentChar == '*'){ //Comienzo del comentario multilinea
						currentState = 2;
						lexema += currentChar;
						currentChar = readConsumeChar();
					} else {
						if(currentState == 2 && currentChar != '*'){ //Cualquier caracter que no sea *
							currentState = 2;
							int asciiChar = (int) currentChar;
							if(asciiChar != 13){ // Verificamos que no sea un ENTER para poder mostrar bien el lexema
								lexema += currentChar;
							}
							currentChar = readConsumeChar();
						} else {
							if(currentState == 2 && currentChar == '*'){
								currentState = 3;
								lexema += currentChar;
								currentChar = readConsumeChar();
							} else {
								/* si encontamos ya un * y no le sigue un / entonces no era el final del comentario
								por lo que volvemos al estado anterior
								*/
								if(currentState == 3 && currentChar != '/'){ 
									currentState = 2;
									lexema += currentChar;
									currentChar = readConsumeChar();
								} else {
									/* Si encontramos un * y le sigue un / entonces es el final del comentario multilinea
									por lo que frenamos el automata en estado aceptador
									*/
									if(currentState == 3 && currentChar == '/'){
										currentState = 4;
										lexema += currentChar;
										break;
									} else {
										currentState = 5;
										break;
									}
								}
							}
						}
					}
				}
			}
			
		if (Arrays.asList(successStates).contains(currentState)) {
			token.setLexema(lexema);
			token.setToken("multiline_comment");
			return true;
		}else{
			throw new UnclosedMultiLineCommentError(lineNumber, columnNumber); // Throw error comentario multilinea
		}}else{
			return false;
		}
	}

	private boolean isComment(char initialChar, Token token) throws InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 2 };
		char currentChar = initialChar;
		String lexema = "";

		while (currentState != 3 && (int) currentChar != 13) {
			if (currentState == 0 && currentChar == '/') {
				currentState = 1;
				lexema += currentChar;
				currentChar = readConsumeChar();
			} else {
				if (currentState == 1 && currentChar == '/') {
					currentState = 2;
					lexema += currentChar;
					currentChar = readConsumeChar();
				}else {
					if(currentState == 2 && (int) currentChar == 13){
						currentState = 2;
						lexema += currentChar;
						break;
					}else {
						if(currentState == 2 && (int) currentChar != 13){
							currentState = 2;
							lexema += currentChar;
							currentChar = readConsumeChar();
						}else{
							currentState = 3;
							break;
						}
					}
				}
			}
		}

		if (Arrays.asList(successStates).contains(currentState)) {
			token.setLexema(lexema);
			token.setToken("comment");
			return true;
		} else {
			return false;
		}
	}

	private void consumeSpaces() throws InvalidCharacterError {
		char currentChar = readWithoutConsumeChar();
		while (isSpaceOrFormat(currentChar)) {
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
		if (currentLine.length() > 0) {
			currentChar = currentLine.charAt(0);
		} else {
			if (file.hasNextLine()) {
				currentChar = (char) 13; // Carriage Return - ENTER
			} else {
				currentChar = (char) 3; // End of File - ETX
			}
		}
		// TODO - Evaluar si es necesario verificar isValidChar acá. Contra: ñine number
		return currentChar;
	}

}
