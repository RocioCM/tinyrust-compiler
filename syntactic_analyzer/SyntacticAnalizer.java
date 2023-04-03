package syntactic_analyzer;

import java.io.FileNotFoundException;
import java.util.Arrays;

import error.lexic.LexicalError;
import error.syntactic.SyntacticalError;
import error.syntactic.UnexpectedToken;
import lexic_analyzer.LexicAnalyzer;
import lexic_analyzer.Token;

public class SyntacticAnalizer {
	private LexicAnalyzer lexic;
	private Token token;
	private Token nextToken;

	public SyntacticAnalizer(String inputPath) throws FileNotFoundException {
		// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
		lexic = new LexicAnalyzer(inputPath);

	}

	public boolean run() throws LexicalError, SyntacticalError {
		// Se obtienen los dos primeros tokens del archivo.
		token = lexic.nextToken();
		nextToken = lexic.nextToken();

		// Si la entrada no es sintácticamente correcta, Program lanza una excepción,
		// por lo se asume que si termina de ejecutarse, la entrada es correcta.
		Program();
		return true;
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
			throw new UnexpectedToken(token, types[0]); // TODO: show good error message with tokens type.
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
			throw new UnexpectedToken(token, types[0]); // TODO: show good error message with tokens type.
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
			throw new UnexpectedToken(token, "");
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
		if (isFirstL("fn")) {
			matchLexema("fn");
			matchLexema("main");
			matchLexema("(");
			matchLexema(")");
			BloqueMetodo();
		} else {
			throw new UnexpectedToken(token, "fn");
		}
	}

	private void Clase() throws LexicalError, SyntacticalError {
		if (isFirstL("class")) {
			matchLexema("class");
			matchToken("id_type");
			ClaseHerenciaOp();
		} else {
			throw new UnexpectedToken(token, "class");
		}
	}

	private void ClaseHerenciaOp() throws LexicalError, SyntacticalError {
		if (isFirstL(":")) {
			Herencia();
		}
		if (isFirstL("{")) {
			matchLexema("{");
			Miembros();
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "\"{\" O \":\"");
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
			throw new UnexpectedToken(token, "CONSTRUCTOR, METODO O ATRIBUTO");
		}
	}

	private void Constructor() throws LexicalError, SyntacticalError {
		if (isFirstL("create")) {
			matchLexema("create");
			ArgumentosFormales();
			Bloque();
		} else {
			throw new UnexpectedToken(token, "CONSTRUCTOR, METODO O ATRIBUTO");
		}
	}

	private void Metodo() throws LexicalError, SyntacticalError {
		if (isFirstL("static")) {
			FormaMetodo();
		}
		if (isFirstL("fn")) {
			matchLexema("fn");
			matchToken("id");
			matchLexema("->");
			TipoMetodo();
			ArgumentosFormales();
			BloqueMetodo();
		} else {
			throw new UnexpectedToken(token, "\"static\" O \"fn\" (METODO)");
		}
	}

	private void ArgumentosFormales() throws LexicalError, SyntacticalError {
		if (isFirstL("(")) {
			matchLexema("(");
			if (isFirstL(")")) {
				matchLexema(")");
			} else {
				ListaArgumentosFormales();
				matchLexema(")");
			}
		} else {
			throw new UnexpectedToken(token, "\"(\"");
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
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private void ArgumentoFormal() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Tipo();
			matchLexema(":");
			matchToken("id");
		}
	}

	private void ArgumentosActuales() throws LexicalError, SyntacticalError {
		if (isFirstL("(")) {
			matchLexema("(");
			if (isFirstL(")")) {
				matchLexema(")");
			} else {
				ListaExpresiones();
				matchLexema(")");
			}
		} else {
			throw new UnexpectedToken(token, "\"(\"");
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
			}
			if (isFirstT("id_type")) {
				TipoReferencia();
			}
			if (isFirstL("Bool", "I32", "Str", "Char")) {
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
	}

	private void ListaDeclaracionVariables() throws LexicalError, SyntacticalError {
	}

	private void DeclVarLocalesN() throws LexicalError, SyntacticalError {
	}

	private void DeclVarLocales() throws LexicalError, SyntacticalError {
	}

	private void Sentencia() throws LexicalError, SyntacticalError {
	}

	private void Sentencias() throws LexicalError, SyntacticalError {
	}

	private void SentenciasOp() throws LexicalError, SyntacticalError {
	}

	private void SentenciaSimple() throws LexicalError, SyntacticalError {
	}

	private void ElseOp() throws LexicalError, SyntacticalError {
	}

	private void Bloque() throws LexicalError, SyntacticalError {
	}

	private void BloqueMetodo() throws LexicalError, SyntacticalError {
	}

	private void Asignacion() throws LexicalError, SyntacticalError {
	}

	private void AsignacionVariableSimple() throws LexicalError, SyntacticalError {
	}

	private void AsignacionSelfSimple() throws LexicalError, SyntacticalError {
	}

	private void ListaExpresiones() throws LexicalError, SyntacticalError {
	}

	private void Expresion() throws LexicalError, SyntacticalError {
	}

	private void ExpOr() throws LexicalError, SyntacticalError {
	}

	private void ExpOrP() throws LexicalError, SyntacticalError {
	}

	private void ExpAnd() throws LexicalError, SyntacticalError {
	}

	private void ExpAndP() throws LexicalError, SyntacticalError {
	}

	private void ExpIgual() throws LexicalError, SyntacticalError {
	}

	private void ExpIgualP() throws LexicalError, SyntacticalError {
	}

	private void ExpCompuesta() throws LexicalError, SyntacticalError {
	}

	private void ExpAdd() throws LexicalError, SyntacticalError {
	}

	private void ExpAddP() throws LexicalError, SyntacticalError {
	}

	private void ExpMul() throws LexicalError, SyntacticalError {
	}

	private void ExpMulP() throws LexicalError, SyntacticalError {
	}

	private void ExpUn() throws LexicalError, SyntacticalError {
	}

	private void OpIgual() throws LexicalError, SyntacticalError {
	}

	private void OpCompuesto() throws LexicalError, SyntacticalError {
	}

	private void OpAdd() throws LexicalError, SyntacticalError {
	}

	private void OpUnario() throws LexicalError, SyntacticalError {
	}

	private void OpMul() throws LexicalError, SyntacticalError {
	}

	private void Operando() throws LexicalError, SyntacticalError {
	}

	private void Literal() throws LexicalError, SyntacticalError {
	}

	private void Primario() throws LexicalError, SyntacticalError {
	}

	private void ExpresionParentizada() throws LexicalError, SyntacticalError {
	}

	private void AccesoSelf() throws LexicalError, SyntacticalError {
	}

	private void AccesoVar() throws LexicalError, SyntacticalError {
	}

	private void LlamadaMetodo() throws LexicalError, SyntacticalError {
	}

	private void LlamadaMetodoEstatico() throws LexicalError, SyntacticalError {
	}

	private void LlamadaConstructor() throws LexicalError, SyntacticalError {
	}

	private void EncadenadoSimpleN() throws LexicalError, SyntacticalError {
	}

	private void EncadenadoSimple() throws LexicalError, SyntacticalError {
	}

	private void Encadenado() throws LexicalError, SyntacticalError {
	}

	private void EncadenadoOp() throws LexicalError, SyntacticalError {
	}

	private void AccesoVariableEncadenado() throws LexicalError, SyntacticalError {
	}
}
