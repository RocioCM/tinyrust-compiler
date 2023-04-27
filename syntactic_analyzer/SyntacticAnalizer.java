package syntactic_analyzer;

import java.io.FileNotFoundException;
import java.util.Arrays;

import error.lexic.LexicalError;
import error.semantic.SemanticalError;
import error.syntactic.SyntacticalError;
import error.syntactic.UnexpectedToken;
import lexic_analyzer.LexicAnalyzer;
import lexic_analyzer.Token;
import semantic_analyzer.symbol_table.SymbolTable;

/**
 * Analizador Sintáctico descendente predictivo recursivo de TinyRust+.
 * Consume un archivo de entrada y determina si la estructura sintáctica del
 * archivo se corresponde con un programa válido de TinyRust+.
 * Hace uso de la clase LexicAnalyzer para identificar los tokens del archivo.
 */
public class SyntacticAnalizer {
	private LexicAnalyzer lexic;
	private Token token;
	private Token nextToken;
	private SymbolTable ts;

	public SyntacticAnalizer(String inputPath) throws FileNotFoundException {
		// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
		lexic = new LexicAnalyzer(inputPath);
		ts = new SymbolTable(inputPath);
	}

	/**
	 * Inicia el análisis sintáctico del archivo provisto al analizador sintáctico.
	 * 
	 * @return true si el análisis fue exitoso. Caso contrario lanzará una excepción
	 * @throws LexicalError     - Si algún token del archivo no es válido.
	 * @throws SyntacticalError - Si la estructura sintáctica del archivo no se
	 *                          corresponde con un programa válido de TinyRust+.
	 */
	public boolean run() throws LexicalError, SyntacticalError, SemanticalError {
		// Se obtienen los dos primeros tokens del archivo.
		token = lexic.nextToken();
		nextToken = lexic.nextToken();

		// Si la entrada no es sintácticamente correcta, Program lanza una excepción,
		// por lo se asume que si termina de ejecutarse, la entrada es correcta.
		Program();
		return true;
	}

	public String getTsJson() {
		return ts.toJson();
	}

	/**
	 * Compara el tipo del token actual contra la lista de tipos de token recibida.
	 * Si coincide con algún elemento de la lista, consume el token.
	 * 
	 * @param types Tipos posibles esperados del token.
	 * @throws LexicalError     - Si el siguiente token no es válido.
	 * @throws SyntacticalError - Si el token no coincide con ningún tipo esperado.
	 */
	private void matchToken(String... types) throws LexicalError, SyntacticalError {
		if (Arrays.asList(types).contains(token.getToken())) {
			token = nextToken;
			nextToken = lexic.nextToken();
		} else {
			String typesAsString = String.join(", ", types);
			throw new UnexpectedToken(token, "TOKEN DE TIPO: " + typesAsString);
		}
	}

	/**
	 * Compara el lexema del token actual contra la lista de lexemas recibida.
	 * Si coincide con algún elemento de la lista, consume el token.
	 * 
	 * @param lexema Lexemas posibles esperados del token.
	 * @throws LexicalError     - Si el siguiente token no es válido.
	 * @throws SyntacticalError - Si el token no coincide con ningún lexema
	 *                          esperado.
	 */
	private void matchLexema(String... types) throws LexicalError, SyntacticalError {
		if (Arrays.asList(types).contains(token.getLexema())) {
			token = nextToken;
			nextToken = lexic.nextToken();
		} else {
			String typesAsString = String.join(", ", types);
			throw new UnexpectedToken(token, typesAsString);
		}
	}

	/**
	 * Compara el lexema del token actual contra la lista de lexemas recibida.
	 * Retorna true si coincide con algún elemento de la lista.
	 * 
	 * @param lexema Lexemas posibles esperados del token.
	 */
	private boolean isFirstL(String... firsts) {
		return Arrays.asList(firsts).contains(token.getLexema());
	}

	/**
	 * Compara el tipo del token actual contra la lista de tipos de token recibida.
	 * Retorna true si coincide con algún elemento de la lista.
	 * 
	 * @param types Tipos posibles esperados del token.
	 */
	private boolean isFirstT(String... firsts) {
		return Arrays.asList(firsts).contains(token.getToken());
	}

	private void Program() throws LexicalError, SyntacticalError {
		Start();
		matchToken("EOF");
	}

	private void Start() throws LexicalError, SyntacticalError {
		if (isFirstL("class", "fn")) {
			Clases();
			Main();
		} else {
			throw new UnexpectedToken(token, "DECLARACION DE CLASES O METODO MAIN");
		}
	}

	private void Clases() throws LexicalError, SyntacticalError {
		if (isFirstL("class")) {
			Clase();
			Clases();
		}
		// Como Clases deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void Main() throws LexicalError, SyntacticalError {
		matchLexema("fn"); // Si no matchea, este método arrojará la excepción.
		matchLexema("main");
		matchLexema("(");
		matchLexema(")");
		BloqueMetodo();
	}

	private void Clase() throws LexicalError, SyntacticalError {
		matchLexema("class"); // Si no matchea, este método arrojará la excepción.
		matchToken("id_type");
		ClaseHerenciaOp();
	}

	private void ClaseHerenciaOp() throws LexicalError, SyntacticalError {
		if (isFirstL(":")) {
			Herencia();
		}
		matchLexema("{");
		Miembros();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "UN CONSTRUCTOR, METODO, ATRIBUTO O \"}\"");
		}

	}

	private void Herencia() throws LexicalError, SyntacticalError {
		if (isFirstL(":")) {
			matchLexema(":");
			matchToken("id_type");
		} else {
			throw new UnexpectedToken(token, "\":\" (HERENCIA)");
		}
	}

	private void Miembros() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array", "pub", "fn", "static", "create") || isFirstT("id_type")) {
			Miembro();
			Miembros();
		}
		// Como Miembros deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void Miembro() throws LexicalError, SyntacticalError {
		if (isFirstL("create")) {
			Constructor();
		} else {
			if (isFirstL("fn", "static")) {
				Metodo();
			} else {
				if (isFirstL("Bool", "I32", "Str", "Char", "Array", "pub") || isFirstT("id_type")) {
					Atributo();
				} else {
					throw new UnexpectedToken(token, "CONSTRUCTOR, METODO O ATRIBUTO");
				}
			}
		}
	}

	private void Atributo() throws LexicalError, SyntacticalError {
		if (isFirstL("pub")) {
			Visibilidad();
		}
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Tipo();
			matchLexema(":");
			ListaDeclaracionVariables();
			matchLexema(";");
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private void Constructor() throws LexicalError, SyntacticalError {
		matchLexema("create"); // Si no matchea, este método arrojará la excepción.
		ArgumentosFormales();
		BloqueMetodo();

	}

	private void Metodo() throws LexicalError, SyntacticalError {
		if (isFirstL("static")) {
			FormaMetodo();
		}
		if (isFirstL("fn")) {
			matchLexema("fn");
			matchToken("id");
			ArgumentosFormales();
			matchLexema("->");
			TipoMetodo();
			BloqueMetodo();
		} else {
			throw new UnexpectedToken(token, "\"fn\" (METODO)");
		}
	}

	private void ArgumentosFormales() throws LexicalError, SyntacticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(")")) {
			matchLexema(")");
		} else {
			ListaArgumentosFormales();
			matchLexema(")");
		}
	}

	private void ListaArgumentosFormales() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			ArgumentoFormal();
			if (isFirstL(",")) {
				matchLexema(",");
				ListaArgumentosFormales();
				// No se lanza un error si no matchea "," ya que
				// sería el caso del último argumento de la lista.
			}
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE, TIPO PRIMITIVO O \")\"");
		}
	}

	private void ArgumentoFormal() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Tipo();
			matchLexema(":");
			matchToken("id");
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE, TIPO PRIMITIVO O \")\"");
		}
	}

	private void ArgumentosActuales() throws LexicalError, SyntacticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(")")) {
			matchLexema(")");
		} else {
			ListaExpresiones();
			matchLexema(")");
		}
	}

	private void FormaMetodo() throws LexicalError, SyntacticalError {
		matchLexema("static"); // Si no matchea, este método arrojará la excepción.
	}

	private void Visibilidad() throws LexicalError, SyntacticalError {
		matchLexema("pub"); // Si no matchea, este método arrojará la excepción.
	}

	private void TipoMetodo() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array", "void") || isFirstT("id_type")) {
			if (isFirstL("void")) {
				matchLexema("void");
			} else {
				Tipo();
			}
		} else {
			throw new UnexpectedToken(token, "UN TIPO DE RETORNO (CLASE O VOID)");
		}
	}

	private void Tipo() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			if (isFirstL("Array")) {
				TipoArray();
			} else if (isFirstT("id_type")) {
				TipoReferencia();
			} else if (isFirstL("Bool", "I32", "Str", "Char")) {
				TipoPrimitivo();
			}
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private void TipoPrimitivo() throws LexicalError, SyntacticalError {
		matchLexema("Bool", "I32", "Str", "Char");
	}

	private void TipoReferencia() throws LexicalError, SyntacticalError {
		matchToken("id_type");
	}

	private void TipoArray() throws LexicalError, SyntacticalError {
		matchLexema("Array"); // Si no matchea, este método arrojará la excepción.
		TipoPrimitivo();
	}

	private void ListaDeclaracionVariables() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(",")) {
			matchLexema(",");
			ListaDeclaracionVariables();
			// No se lanza un error si no matchea "," ya que
			// sería el caso de la última variable de la lista.
		}
	}

	private void DeclVarLocalesN() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			DeclVarLocales();
			DeclVarLocalesN();
		}
		// Como DeclVarLocalesN deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void DeclVarLocales() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Tipo();
			matchLexema(":");
			ListaDeclaracionVariables();
			matchLexema(";");
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private void Sentencia() throws LexicalError, SyntacticalError {
		if (isFirstL(";", "self", "(", "if", "while", "{", "return") || isFirstT("id")) {
			if (isFirstL(";")) {
				matchLexema(";");
			} else if (isFirstL("self") || isFirstT("id")) {
				Asignacion();
				matchLexema(";");
			} else if (isFirstL("(")) {
				SentenciaSimple();
				matchLexema(";");
			} else if (isFirstL("if")) {
				matchLexema("if");
				matchLexema("(");
				Expresion();
				matchLexema(")");
				Sentencia();
				ElseOp();
			} else if (isFirstL("while")) {
				matchLexema("while");
				matchLexema("(");
				Expresion();
				matchLexema(")");
				Sentencia();
			} else if (isFirstL("{")) {
				Bloque();
			} else if (isFirstL("return")) {
				matchLexema("return");
				if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
						|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
					Expresion();
				}
				matchLexema(";");
			}
		} else {
			throw new UnexpectedToken(token, "UNA SENTENCIA");
		}
	}

	private void Sentencias() throws LexicalError, SyntacticalError {
		if (isFirstL(";", "self", "(", "if", "while", "{", "return") || isFirstT("id")) {
			Sentencia();
			Sentencias();
		}
		// Como Sentencias deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void SentenciaSimple() throws LexicalError, SyntacticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		Expresion();
		matchLexema(")");
	}

	private void ElseOp() throws LexicalError, SyntacticalError {
		if (isFirstL("else")) {
			matchLexema("else");
			Sentencia();
		}
		// Como ElseOp deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void Bloque() throws LexicalError, SyntacticalError {
		matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "UNA SENTENCIA O \"}\"");
		}
	}

	private void BloqueMetodo() throws LexicalError, SyntacticalError {
		matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		DeclVarLocalesN();
		Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "EN ORDEN O UNA DECLARACION DE VARIABLE O UNA SENTENCIA O \"}\"");
		}
	}

	private void Asignacion() throws LexicalError, SyntacticalError {
		if (isFirstL("self") || isFirstT("id")) {
			if (isFirstL("self")) {
				AsignacionSelfSimple();
			} else {
				AsignacionVariableSimple();
			}
			matchLexema("=");
			Expresion();
		} else {
			throw new UnexpectedToken(token, "\"self\" O UN IDENTIFICADOR DE VARIABLE O METODO");
		}
	}

	private void AsignacionVariableSimple() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			Expresion();
			matchLexema("]");
		} else {
			EncadenadoSimpleN();
			// Como EncadenadoSimpleN deriva Lambda, no tirará excepción
			// si el token no matchea con sus primeros.
		}
	}

	private void AsignacionSelfSimple() throws LexicalError, SyntacticalError {
		matchLexema("self"); // Si no matchea, este método arrojará la excepción.
		EncadenadoSimpleN();
	}

	private void ListaExpresiones() throws LexicalError, SyntacticalError {
		Expresion(); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(",")) {
			matchLexema(",");
			ListaExpresiones();
			// No se lanza un error si no matchea "," ya que
			// sería el caso de la última expresión de la lista.
		}
	}

	private void Expresion() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpOr();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpOr() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpAnd();
			ExpOrP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpOrP() throws LexicalError, SyntacticalError {
		if (isFirstL("||")) {
			matchLexema("||");
			ExpAnd();
			ExpOrP();
		}
		// Como ExpOrP deriva Lambda, se continúa la ejecución si no matchea
	}

	private void ExpAnd() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpIgual();
			ExpAndP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpAndP() throws LexicalError, SyntacticalError {
		if (isFirstL("&&")) {
			matchLexema("&&");
			ExpIgual();
			ExpAndP();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void ExpIgual() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpCompuesta();
			ExpIgualP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpIgualP() throws LexicalError, SyntacticalError {
		if (isFirstL("==", "!=")) {
			OpIgual();
			ExpCompuesta();
			ExpIgualP();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void ExpCompuesta() throws LexicalError, SyntacticalError {
		ExpAdd(); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("<", "<=", ">", ">=")) {
			OpCompuesto();
			ExpAdd();
		}
	}

	private void ExpAdd() throws LexicalError, SyntacticalError {
		if (isFirstL(
				"+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpMul();
			ExpAddP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpAddP() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-")) {
			OpAdd();
			ExpMul();
			ExpAddP();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void ExpMul() throws LexicalError, SyntacticalError {
		if (isFirstL(
				"+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpUn();
			ExpMulP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
	}

	private void ExpMulP() throws LexicalError, SyntacticalError {
		if (isFirstL("*", "/", "%")) {
			OpMul();
			ExpUn();
			ExpMulP();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void ExpUn() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!")) {
			OpUnario();
			ExpUn();
		} else {
			if (isFirstL("nil", "true", "false", "(", "self", "new")
					|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
				Operando();
			} else {
				throw new UnexpectedToken(token, "UNA EXPRESION");
			}
		}

	}

	private void OpIgual() throws LexicalError, SyntacticalError {
		matchLexema("==", "!=");
	}

	private void OpCompuesto() throws LexicalError, SyntacticalError {
		matchLexema("<", ">", "<=", ">=");
	}

	private void OpAdd() throws LexicalError, SyntacticalError {
		matchLexema("+", "-");
	}

	private void OpUnario() throws LexicalError, SyntacticalError {
		matchLexema("+", "-", "!");
	}

	private void OpMul() throws LexicalError, SyntacticalError {
		matchLexema("*", "/", "%");
	}

	private void Operando() throws LexicalError, SyntacticalError {
		if (isFirstL("(", "self", "new") || isFirstT("id", "id_type")) {
			Primario();
			EncadenadoOp();
		} else {
			if (isFirstL("nil", "true", "false") || isFirstT("lit_int", "lit_string", "lit_char")) {
				Literal();
			} else {
				throw new UnexpectedToken(token, "UN OPERANDO");
			}
		}
	}

	private void Literal() throws LexicalError, SyntacticalError {
		if (isFirstL("nil", "true", "false")) {
			matchLexema("nil", "true", "false");
		} else {
			if (isFirstT("lit_int", "lit_string", "lit_char")) {
				matchToken("lit_int", "lit_string", "lit_char");
			} else {
				throw new UnexpectedToken(token, "UN LITERAL");
			}
		}
	}

	private void Primario() throws LexicalError, SyntacticalError {
		if (isFirstL("(")) {
			ExpresionParentizada();
		} else {
			if (isFirstL("self")) {
				AccesoSelf();
			} else {
				if (isFirstT("id")) {
					// Miramos qué hay después del identificador sin consumirlo
					if (nextToken.getLexema().equals("(")) {
						LlamadaMetodo();
					} else {
						AccesoVar();
					}
				} else {
					if (isFirstT("id_type")) {
						LlamadaMetodoEstatico();
					} else {
						if (isFirstL("new")) {
							LlamadaConstructor();
						} else {
							throw new UnexpectedToken(token,
									"\"(\", \"self\", \"new\" O UN IDENTIFICADOR DE CLASE O DE METODO/VARIABLE");
						}
					}
				}
			}
		}
	}

	private void ExpresionParentizada() throws LexicalError, SyntacticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		Expresion();
		matchLexema(")");
		EncadenadoOp();
	}

	private void AccesoSelf() throws LexicalError, SyntacticalError {
		matchLexema("self"); // Si no matchea, este método arrojará la excepción.
		EncadenadoOp();
	}

	private void AccesoVar() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			Expresion();
			matchLexema("]");
		} else {
			EncadenadoOp();
		}
	}

	private void LlamadaMetodo() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		ArgumentosActuales();
		EncadenadoOp();
	}

	private void LlamadaMetodoEstatico() throws LexicalError, SyntacticalError {
		matchToken("id_type"); // Si no matchea, este método arrojará la excepción.
		matchLexema(".");
		LlamadaMetodo();
		EncadenadoOp();
	}

	private void LlamadaConstructor() throws LexicalError, SyntacticalError {
		matchLexema("new"); // Si no matchea, este método arrojará la excepción.
		if (isFirstT("id_type")) {
			matchToken("id_type");
			ArgumentosActuales();
			EncadenadoOp();
		} else {
			if (isFirstL("Bool", "I32", "Str", "Char")) {
				TipoPrimitivo();
				matchLexema("[");
				Expresion();
				matchLexema("]");
			} else {
				throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
			}
		}
	}

	private void EncadenadoSimpleN() throws LexicalError, SyntacticalError {
		if (isFirstL(".")) {
			EncadenadoSimple();
			EncadenadoSimpleN();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void EncadenadoSimple() throws LexicalError, SyntacticalError {
		matchLexema(".");
		matchToken("id");
	}

	private void Encadenado() throws LexicalError, SyntacticalError {
		matchLexema(".");
		if (isFirstT("id")) {
			// Miramos qué hay después del identificador sin consumirlo
			if (nextToken.getLexema().equals("(")) {
				LlamadaMetodoEncadenado();
			} else {
				AccesoVariableEncadenado();
			}
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE METODO O VARIABLE");
		}
	}

	private void EncadenadoOp() throws LexicalError, SyntacticalError {
		if (isFirstL(".")) {
			Encadenado();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
	}

	private void AccesoVariableEncadenado() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			Expresion();
			matchLexema("]");
		} else {
			EncadenadoOp();
		}
	}

	private void LlamadaMetodoEncadenado() throws LexicalError, SyntacticalError {
		matchToken("id"); // Si no matchea, este método arrojará la excepción.
		ArgumentosActuales();
		EncadenadoOp();
	}

}
