package lexic_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import error.*;

/**
 * Analizador Léxico de TinyRust+.
 * Consume un archivo de entrada y a demanda retorna tokens generados a partir
 * de dicho archivo.
 */
public class LexicAnalyzer {
	private Scanner file; // Archivo de entrada.
	private String currentLine = ""; // Contenido de la linea actual leída del archivo.
	private int lineNumber = 0; // Linea leída actual del archivo de entrada.
	private int columnNumber = 0; // Columna leída actual del archivo de entrada.
	private boolean reachedEOF = false; // Flag que indica si ya se consumió todo el archivo de entrada.

	public LexicAnalyzer(String filePath) throws FileNotFoundException {
		// Abrir el archivo de entrada.
		File document = new File(filePath);
		Scanner scanner = new Scanner(document);
		this.file = scanner;

		// Inicializar la lectura de la primer linea del archivo.
		if (file.hasNextLine()) {
			currentLine = file.nextLine();
			lineNumber++;
		} else {
			reachedEOF = true;
		}
	}

	/**
	 * Consume parte del archivo de entrada para reconocer y
	 * retornar el siguiente token.
	 * 
	 * Si ya se consumió todo el archivo de entrada, devuelve un Token especial
	 * representando el fin del archivo (token EOF).
	 * 
	 * @return Siguiente token del archivo de entrada o EOF si el archivo ya fue
	 *         consumido.
	 * @throws LexicalError si ocurrió algún error al intentar decodificar el
	 *                      siguiente token del archivo de entrada.
	 */
	public Token nextToken() throws LexicalError {
		consumeSpaces(); // Consumir sin guardar los espacios, tabs y enter antes de un caracter valioso.
		char currentChar = readConsumeChar();
		Token token = new Token("", String.valueOf(currentChar), lineNumber, columnNumber); // Inicializar el token.

		if (reachedEOF) {
			// Si ya se consumió todo el archivo,
			// se retorna un Token vacío representando el fin del archivo.
			token.setToken("EOF");
			token.setLexema("");

		} else {
			// Leer el primer caracter del token y continuar el match según qué caracter es.
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
					token.setToken("close_bracket");
					break;

				case ',': // Separador de parámetros en funciones o arreglos.
					token.setToken("comma");
					break;

				case '.': // Punto para los métodos.
					token.setToken("dot");
					break;

				case '|': // Operador lógico OR
					if (readWithoutConsumeChar() == '|') {
						token.setToken("op_or");
						token.appendLexema(readConsumeChar());
					} else {
						throw new UnmatchedTokenError(token.getLine(), token.getCol(), token.getLexema());
					}
					break;

				case '&': // Operador lógico AND
					if (readWithoutConsumeChar() == '&') {
						token.setToken("op_and");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else {
						throw new UnmatchedTokenError(token.getLine(), token.getCol(), token.getLexema());
					}
					break;

				case '=': // Asignación o comparación igualdad.
					if (readWithoutConsumeChar() == '=') { // Operador de comparación de igualdad.
						token.setToken("op_eq");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else { // Asignación.
						token.setToken("assignment");
					}
					break;

				case '!': // Negación o comparación desigualdad.
					if (readWithoutConsumeChar() == '=') { // Operador de comparación de desigualdad.
						token.setToken("op_not_eq");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else { // Operador de negación.
						token.setToken("op_not");
					}
					break;

				case '<': // Operador menor / menor o igual.
					if (readWithoutConsumeChar() == '=') { // Operador de comparación de menor o igual.
						token.setToken("op_less_eq");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else { // Operador de comparación de menor.
						token.setToken("op_less");
					}
					break;

				case '>': // Operador menor / menor o igual.
					if (readWithoutConsumeChar() == '=') { // Operador de comparación de mayor o igual.
						token.setToken("op_great_eq");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else { // Operador de comparación de mayor.
						token.setToken("op_great");
					}
					break;

				case '-': // Operador resta o tipo de retorno de función.
					if (readWithoutConsumeChar() == '>') { // Operador de acceso a atributos.
						token.setToken("return_type");
						token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
					} else { // Operador de resta.
						token.setToken("op_sub");
					}
					break;

				case '/': // Operador división o comentario.
					if (!matchMultilineComment(currentChar, token)) { // Comentario multilinea.
						if (!matchComment(currentChar, token)) { // Comentario de una linea.
							// Operador división.
							token.setToken("op_div");
						}
					}
					break;

				case '"': // Literal cadena.
					matchStringLiteral(currentChar, token);
					break;

				case '\'': // Literal caracter.
					matchCharLiteral(currentChar, token);
					break;

				default:
					if (isDigit(currentChar)) { // Dígito numérico.
						matchIntLiteral(currentChar, token);
						break;
					}

					if (isAlphabet(currentChar) || currentChar == '_') { // Letra minúscula o mayúscula o _.
						matchIdentifier(currentChar, token); // Se consume todo el identificador.
						if (!matchTypeIdentifier(token)) { // Se verifica si es un identificador de tipo.
							ReservedWords.matchReservedWord(token); // Se verifica si es una palabra reservada.
						}
						break;
					}

					// Si el primer caracter no coincidió con ningún token posible, lanza un error.
					throw new UnmatchedTokenError(token.getLine(), token.getCol(), token.getLexema());
			}
		}

		return token;
	}

	/**
	 * Verifica si ya se ha consumido por completo el archivo de entrada, por tanto,
	 * no hay más tokens para interpretar.
	 * 
	 * @return Devuelve true si ya se llegó al final del archivo de entrada.
	 */
	public boolean hasNextToken() {
		return !reachedEOF;
	}

	/**
	 * Recibe un caracter y determina si pertenece al alfabeto de entrada de
	 * TinyRust+.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter pertenece al alfabeto de TinyRust+
	 */
	private boolean isValidChar(char currentChar) {
		// Referencia ASCII: https://www.ascii-code.com/
		int asciiChar = (int) currentChar;
		return (asciiChar >= 32
				&& asciiChar <= 126
				|| asciiChar == 9
				|| asciiChar == 10
				|| asciiChar == 11
				|| asciiChar == 13
				|| asciiChar == 3);
	}

	/**
	 * Recibe un caracter y determina si es una letra minúscula del abecedario.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es una letra minúscula.
	 */
	private boolean isLowercaseChar(char currentChar) {
		int asciiChar = (int) currentChar;
		return (asciiChar >= 97 && asciiChar <= 122);
	}

	/**
	 * Recibe un caracter y determina si es una letra mayúscula del abecedario.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es una letra mayúscula.
	 */
	private boolean isUppercaseChar(char currentChar) {
		int asciiChar = (int) currentChar;
		return (asciiChar >= 65 && asciiChar <= 90);
	}

	/**
	 * Recibe un caracter y determina si es una letra del abecedario,
	 * aceptando tanto minúsculas como mayúsculas.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter forma parte del abecedario.
	 */
	private boolean isAlphabet(char currentChar) {
		return (isLowercaseChar(currentChar) || isUppercaseChar(currentChar));
	}

	/**
	 * Recibe un caracter y determina si es un dígito numérico.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es un número.
	 */
	private boolean isDigit(char currentChar) {
		int asciiChar = (int) currentChar;
		return (asciiChar >= 48 && asciiChar <= 57);
	}

	/**
	 * Recibe un caracter y determina si es un operador o algún tipo de delimitador.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es un operador o delimitador.
	 */
	private boolean isOperator(char currentChar) {
		boolean validOperator;
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
			case '&': // Comienzo de operador AND.
			case '|': // Comienzo de operador OR.
			case '(': // Paréntesis para los parámetros.
			case ')': // Paréntesis para los parámetros.
			case '[': // Corchetes para los arreglos.
			case ']': // Corchetes para los arreglos.
			case '{': // Llaves para bloques.
			case '}': // Llaves para bloques.
			case ',': // Separador de parámetros.
			case '"': // Comillas para los literales cadena.
			case '\'': // Comillas para los literales caracter.
			case ';': // Delimitador de final de linea.
			case '.': // Punto para los métodos.
			case ':': // Dos puntos para los tipos de variables.
				validOperator = true;
				break;
			default:
				validOperator = false;
		}

		return validOperator;
	}

	/**
	 * Recibe un caracter y determina si es un espacio en blanco.
	 * Se considera espacio en blanco a tabulaciones, saltos de linea, espacios
	 * y el caracter de fin de archivo.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es un espacio en blanco.
	 */
	private boolean isBlankSpace(char currentChar) {
		int asciiChar = (int) currentChar;
		return (asciiChar == 32 // space
				|| asciiChar == 9 // tab
				|| asciiChar == 10 // new line
				|| asciiChar == 11 // vertical tab
				|| asciiChar == 13 // enter
				|| asciiChar == 3 // end of file
		);
	}

	/**
	 * Recibe un caracter y determina si es un salto de linea.
	 * 
	 * @param currentChar caracter a validar.
	 * @return Devuelve true si el caracter es un salto de linea.
	 */
	private boolean isLineBreak(char currentChar) {
		int asciiChar = (int) currentChar;
		return (asciiChar == 10 // new line
				|| asciiChar == 11 // vertical tab
				|| asciiChar == 13 // enter
				|| asciiChar == 3 // end of file
		);
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * literal numérico entero.
	 * Se considera literal numérico entero a toda sucesión de digitos numéricos.
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    literal entero.
	 * @param token       el token que vamos a modificar si es un literal entero.
	 * @return true si es un literal entero.
	 * @throws InvalidCharacterError si algún caracter leído no pertenece al
	 *                               alfabeto de entrada.
	 * @throws BadIdentifierError    si el literal entero está unido a un
	 *                               identificador.
	 */
	private boolean matchIntLiteral(char initialChar, Token token) throws InvalidCharacterError, BadIdentifierError {
		char nextChar = readWithoutConsumeChar();
		// Consume todos los dígitos contiguos que encuentra.
		while (isDigit(nextChar)) {
			readConsumeChar();
			token.appendLexema(nextChar);
			nextChar = readWithoutConsumeChar();
		}

		if (isAlphabet(nextChar)) {
			// Si luego del entero hay una letra,
			// se interpreta todo el conjunto como un identificador mal formado.
			while (isAlphabet(nextChar) || isDigit(nextChar) || nextChar == '_') {
				// Se lee el resto del identificador para mostrarlo en el mensaje de error.
				token.appendLexema(readConsumeChar());
				nextChar = readWithoutConsumeChar();
			}
			throw new BadIdentifierError(token.getLine(), token.getCol(), token.getLexema());
		} else {
			// Se identifica el token como literal entero exitosamente.
			token.setToken("lit_int");
		}

		return true;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * literal cadena.
	 * Se considera literal cadena a cualquier cantidad de caracteres encerrados
	 * entre comillas dobles en una sola linea.
	 * El literal cadena permite caracteres de escape dentro del literal.
	 * No es multilinea, por lo que tirará un error si se encuentra un salto de
	 * linea antes que las comillas de cierre.
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    literal cadena.
	 * @param token       el token que vamos a modificar si es un literal cadena.
	 * @return true si es un literal cadena.
	 * @throws InvalidCharacterError si algún caracter leído no pertenece al
	 *                               alfabeto de entrada.
	 * @throws InvalidLiteralError   si se encuentra un nil o un salto de linea en
	 *                               antes de cerrar la cadena.
	 */
	private boolean matchStringLiteral(char initialChar, Token token) throws InvalidCharacterError, InvalidLiteralError {
		char nextChar = readWithoutConsumeChar();

		// Se consumen todos los caracteres hasta encontrar las comillas de cierre.
		// Si se encuentra un salto de linea también se deja de leer.
		while (!isLineBreak(nextChar) && nextChar != '"') {
			token.appendLexema(readConsumeChar()); // consumimos finalmente el caracter.
			if (nextChar == '\\') { // Se encontró un caracter de escape en la cadena.
				char escapedChar = readWithoutConsumeChar();
				if (escapedChar == '0') {
					// Se encontró un caracter NIL en la cadena.
					throw new InvalidLiteralError(lineNumber, columnNumber, "SE ENCONTRO UN CARACTER INVALIDO NIL EN UNA CADENA");
				}
				if (escapedChar == '"' || escapedChar == '\\') {
					// Se encontró un escape del caracter " o \. Se sigue leyendo la cadena.
					token.appendLexema(readConsumeChar()); // consumimos el caracter posterior al escape.
				}
			}
			nextChar = readWithoutConsumeChar();
		}

		if (nextChar == '"') { // Se cierra el literal al hallar las comillas de cierre.
			token.appendLexema(readConsumeChar()); // consumimos finalmente las comillas de cierre.
			token.setToken("lit_string");
		} else {
			// Se encontró un salto de linea antes del cierre de la cadena.
			throw new InvalidLiteralError(token.getLine(), token.getCol(),
					"SE ESPERABA UN CIERRE DE CADENA, PERO SE ENCONTRO UN SALTO DE LINEA.");
		}
		return true;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * literal caracter.
	 * Se considera literal caracter a un único caracter encerrado
	 * entre comillas simples.
	 * El literal caracter permite también un único caracter de escape dentro del
	 * literal.
	 * No es multilinea, por lo que tirará un error si se encuentra un salto de
	 * linea antes que la comilla de cierre.
	 * 
	 * Los caracteres se representarán internamente en TinyRust+ como un número
	 * entero igual al código ASCII asociado al caracter.
	 * Referencia ASCII: https://www.ascii-code.com/
	 * 
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    literal caracter.
	 * @param token       el token que vamos a modificar si es un literal caracter.
	 * @return true si es un literal caracter.
	 * @throws InvalidCharacterError si algún caracter leído no pertenece al
	 *                               alfabeto de entrada.
	 * @throws InvalidLiteralError   si se encuentra un nil, un salto de linea, cero
	 *                               caracteres o más de un caracter en el literal.
	 */
	private boolean matchCharLiteral(char initialChar, Token token) throws InvalidCharacterError, InvalidLiteralError {
		char currentChar = readConsumeChar();

		if (isLineBreak(currentChar) || currentChar == '\'') {
			// El literal tiene cero caracteres o no está cerrado.
			throw new InvalidLiteralError(token.getLine(), token.getCol(),
					"EL LITERAL DEBE CONTENER EXACTAMENTE UN CARACTER");
		}

		// Se analiza el caracter dentro del literal.
		if (currentChar == '\\') { // Se encontró un caracter de escape.
			char escapedChar = readConsumeChar(); // consumimos el caracter posterior al escape.
			if (escapedChar == '0') { // Se encontró un caracter NIL en la cadena.
				throw new InvalidLiteralError(lineNumber, columnNumber, "SE ENCONTRO UN CARACTER NIL EN UN LITERAL");
			}
			// Se verifica cuál es el caracter escapado para guardar el número de caracter
			// correspondiente en el token.
			switch (escapedChar) {
				case 't':
					token.setLexema(String.valueOf(9)); // Caracter TAB.
					break;
				case 'n':
					token.setLexema(String.valueOf(10)); // Caracter SALTO DE LINEA.
					break;
				default:
					token.setLexema(String.valueOf((int) escapedChar));
			}
		} else {
			// Si el caracter no fue de escape, se guarda su número de caracter en el token.
			token.setLexema(String.valueOf((int) currentChar));
		}

		// Se verifica encontrar a continuación el caracter de cierre del literal.
		if (readConsumeChar() == '\'') {
			token.setToken("lit_char");
		} else {
			// Si el literal no está cerrado o tiene más de un caracter, se lanza un error.
			throw new InvalidLiteralError(token.getLine(), token.getCol(),
					"EL LITERAL DEBE CONTENER EXACTAMENTE UN CARACTER");
		}
		return true;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * identificador.
	 * Este método es básicamente un autómata con 4 estados
	 * 0: siendo el inicial
	 * 1: al que nos movemos si encontramos _ o letra - Aceptador
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
	private boolean matchIdentifier(char initialChar, Token token) throws BadIdentifierError, InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 1, 2 };
		char currentChar = initialChar;
		String lexema = "";

		while (currentState != 2 && currentState != 3 && !isBlankSpace(currentChar) && !reachedEOF) {
			if (currentState == 0 && (isAlphabet(currentChar) || currentChar == '_')) {
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
						} else {
							lexema += currentChar;
							currentState = 3;
						}
					}
				}
			}
		}

		if (Arrays.asList(successStates).contains(currentState)) {
			token.setLexema(lexema);
			token.setToken("id");
			return true;
		} else {
			throw new BadIdentifierError(lineNumber, columnNumber, lexema);
		}
	}

	/**
	 * A partir de un token de tipo identificador, valida si es un identificador de
	 * tipo.
	 * 
	 * Se considera identificador de tipo a todo identificador que comienza con una
	 * letra mayúscula.
	 * 
	 * @param token el token que vamos a verificar y modificar si es un
	 *              identificador de tipo.
	 * @return true si es un identificador de tipo, false si no lo es.
	 */
	private boolean matchTypeIdentifier(Token token) {
		boolean matched = false;
		if (token.getToken() == "id" && isUppercaseChar(token.getLexema().charAt(0))) {
			token.setToken("id_type");
			matched = true;
		}

		return matched;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * comentario de múltiples líneas.
	 * Este método representa a un autómata con 5 estados con una verificación
	 * adicional:
	 * Hacemos un checkeo de que el siguiente caracter sea un asterisco para
	 * comenzar el comentario multilinea.
	 * Estados:
	 * 0: el inicial
	 * 1: al que nos movemos si encontramos un /
	 * 
	 * 2: al que nos movemos si encontramos un * habiendo encontrado un / antes.
	 * Sobre este estado vamos a ir consumiendo todo lo que venga sin rechazar y
	 * si encontramos un EOF, arrojamos error porque no se cerró el comentario.
	 * 
	 * 3: nos movemos a este estado si encontramos un * y estamos en el estado 2.
	 * Si recibimos cualquier cosa que no sea un /, volvemos al 2. Y si encontramos
	 * un EOF, arrojamos error porque no se cerró el comentario.
	 * 
	 * 4: el estado de aceptación, en el cual consumimos el último caracter y
	 * retornamos que efectivamente se trata de un comentario multilinea.
	 * 
	 * Si en cualquier estado, encontramos un EOF, nos movemos al estado 5,
	 * rechazador.
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    comentario de una línea.
	 * @param token       el token que vamos a llenar si es un comentario de una
	 *                    línea.
	 * @return true si es un comentario de una línea, false si no lo es.
	 */
	private boolean matchMultilineComment(char initialChar, Token token)
			throws UnclosedMultiLineCommentError, InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 4 };
		char currentChar = initialChar;
		String lexema = "";
		boolean matched = false;

		// Verificamos que el siguiente char sea un asterisco para comenzar el
		// comentario multilinea
		if (readWithoutConsumeChar() == '*') {
			while (currentState != 4 && currentState != 5) {
				if (reachedEOF) {
					currentState = 5;
				} else {
					if (currentState == 0 && currentChar == '/') {
						currentState = 1;
						lexema += currentChar;
						currentChar = readConsumeChar();
					} else {
						if (currentState == 1 && currentChar == '*') { // Comienzo del comentario multilinea
							currentState = 2;
							lexema += currentChar;
							currentChar = readConsumeChar();
						} else {
							if (currentState == 2 && currentChar != '*') { // Cualquier caracter que no sea *
								currentState = 2;
								if (!isLineBreak(currentChar)) { // Verificamos que no sea un ENTER para poder mostrar bien el lexema
									lexema += currentChar;
								}
								currentChar = readConsumeChar();
							} else {
								if (currentState == 2 && currentChar == '*') {
									currentState = 3;
									lexema += currentChar;
									currentChar = readConsumeChar();
								} else {
									/*
									 * si encontamos ya un * y no le sigue un / entonces no era el final del
									 * comentario
									 * por lo que volvemos al estado anterior
									 */
									if (currentState == 3 && currentChar != '/') {
										currentState = 2;
										lexema += currentChar;
										currentChar = readConsumeChar();
									} else {
										/*
										 * Si encontramos un * y le sigue un / entonces es el final del comentario
										 * multilinea
										 * por lo que frenamos el automata en estado aceptador
										 */
										if (currentState == 3 && currentChar == '/') {
											currentState = 4;
											lexema += currentChar;
										} else {
											currentState = 5;
										}
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
				matched = true;
			} else {
				throw new UnclosedMultiLineCommentError(lineNumber, columnNumber); // Throw error comentario multilinea
			}
		}

		return matched;
	}

	/**
	 * A partir de un caracter inicial, verifica si lo que sigue es un
	 * comentario de una sola linea.
	 * Este método representa a un autómata con 4 estados con una verificación
	 * adicional:
	 * Hacemos un checkeo de que el siguiente caracter sea un slash para
	 * comenzar el comentario.
	 * Estados:
	 * 0: el inicial
	 * 1: al que nos movemos si encontramos un /
	 * 
	 * 2: al que nos movemos si encontramos un / habiendo encontrado un / antes.
	 * Sobre este estado vamos a ir consumiendo todo lo que venga sin rechazar hasta
	 * encontrar un salto de linea. Este estado es aceptador.
	 * 
	 * 3: nos movemos a este estado si encontramos un salto de linea y estamos en el
	 * estado 2.
	 * 
	 * 
	 * @param initialChar el caracter a partir del cual vamos a analizar si es un
	 *                    comentario de una línea.
	 * @param token       el token que vamos a llenar con el lexema y el token si es
	 *                    que es un comentario de una línea.
	 * @return true si es un comentario de una línea, false si no lo es.
	 */
	private boolean matchComment(char initialChar, Token token) throws InvalidCharacterError {
		int initialState = 0;
		int currentState = initialState;
		Integer successStates[] = { 2 };
		char currentChar = initialChar;
		String lexema = "";
		boolean matched = false;

		// Verificamos que el siguiente char sea un slash para comenzar el comentario.
		if (readWithoutConsumeChar() == '/') {
			while (currentState != 3 && !isLineBreak(currentChar)) {
				if (currentState == 0 && currentChar == '/') {
					currentState = 1;
					lexema += currentChar;
					currentChar = readConsumeChar();
				} else {
					if (currentState == 1 && currentChar == '/') {
						currentState = 2;
						lexema += currentChar;
						currentChar = readConsumeChar();
					} else {
						if (currentState == 2 && isLineBreak(currentChar)) {
							currentState = 2;
							lexema += currentChar;
						} else {
							if (currentState == 2 && !isLineBreak(currentChar)) {
								currentState = 2;
								lexema += currentChar;
								currentChar = readConsumeChar();
							} else {
								currentState = 3;
							}
						}
					}
				}
			}

			if (Arrays.asList(successStates).contains(currentState)) {
				token.setLexema(lexema);
				token.setToken("comment");
				matched = true;
			}
		}

		return matched;
	}

	/**
	 * Consume y descarta espacios en blanco del archivo de entrada.
	 * Se considera espacios en blanco a tabulaciones, saltos de linea y espacios.
	 * Se detiene al encontrar un caracter no blanco o al alcanzar el final del
	 * archivo.
	 * 
	 * @throws InvalidCharacterError si algún caracter leído no pertenece al
	 *                               alfabeto de entrada.
	 */
	private void consumeSpaces() throws InvalidCharacterError {
		char currentChar = readWithoutConsumeChar();
		// Validamos que el caracter sea espacio en blanco y lo consumimos.
		// Se detiene al llegar al final del archivo o leer un caracter significativo.
		while (isBlankSpace(currentChar) && !reachedEOF) {
			readConsumeChar(); // consumimos finalmente el caracter.
			currentChar = readWithoutConsumeChar();
		}
	}

	/**
	 * Lee el próximo caracter del archivo consumiendolo.
	 * Si el archivo se terminó de leer por completo,
	 * devuelve el caracter 3 (ETX - End of Text).
	 * 
	 * Al leer un caracter actualiza el registro de número de linea y columna
	 * actuales.
	 * 
	 * @return Devuelve el próximo caracter del archivo.
	 * @throws InvalidCharacterError si el caracter leído no pertenece al alfabeto
	 *                               de entrada de TinyRust+
	 */
	private char readConsumeChar() throws InvalidCharacterError {
		char currentChar;
		if (reachedEOF) {
			currentChar = (char) 3; // End of File - ETX
		} else {
			if (currentLine.length() > 0) {
				// Lee el próximo caracter de la linea actual.
				currentChar = currentLine.charAt(0);
				currentLine = currentLine.substring(1);
				columnNumber++;
			} else {
				if (file.hasNextLine()) {
					// Si ya se consumió la linea actual, devuelve un salto de linea.
					currentChar = (char) 13; // Carriage Return - ENTER
					currentLine = file.nextLine(); // Inicializa la siguiente linea.
					lineNumber++;
					columnNumber = 0;
				} else {
					// Se llegó al final del archivo.
					currentChar = (char) 3; // End of File - ETX
					reachedEOF = true;
					file.close();
				}
			}
		}

		// Validar que el caracter pertenece al alfabeto de entrada.
		if (!isValidChar(currentChar)) {
			throw new InvalidCharacterError(lineNumber, columnNumber, currentChar);
		}
		return currentChar;
	}

	/**
	 * Lee el próximo caracter del archivo sin consumirlo.
	 * Si el archivo se terminó de leer por completo,
	 * devuelve el caracter 3 (ETX - End of Text).
	 * 
	 * @return Devuelve el próximo caracter del archivo.
	 */
	private char readWithoutConsumeChar() {
		char currentChar;
		if (reachedEOF) {
			currentChar = (char) 3; // End of File - ETX
		} else {
			if (currentLine.length() > 0) {
				// Lee el próximo caracter de la linea actual.
				currentChar = currentLine.charAt(0);
			} else {
				if (file.hasNextLine()) {
					// Si ya se consumió la linea actual, devuelve un salto de linea.
					currentChar = (char) 13; // Carriage Return - ENTER
				} else {
					// Se llegó al final del archivo.
					currentChar = (char) 3; // End of File - ETX
					reachedEOF = true;
					file.close(); // Cierra el archivo.
				}
			}
		}
		return currentChar;
	}
}
