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
		if (isFirstL("{")) {
			matchLexema("{");
			Miembros();
			matchLexema("}");
		} else {
			if (isFirstL(":")) {
				Herencia();
				matchLexema("{");
				Miembros();
				matchLexema("}");
			} else {
				throw new UnexpectedToken(token, "\"{\" O \":\"");
			}
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
		// TODO
	}

	private void Miembro() throws LexicalError, SyntacticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array", "pub", "fn", "static", "create") || isFirstT("id_type")) {
			if (isFirstT("id_type")) {
				matchToken("id_type");
			} else {
				matchLexema("Bool", "I32", "Str", "Char", "Array", "pub", "fn", "static", "create");
			}
		}
		// TODO
	}

	private void Atributo() throws LexicalError, SyntacticalError {
	}

	private void Constructor() throws LexicalError, SyntacticalError {
	}

	private void Metodo() throws LexicalError, SyntacticalError {
	}

	private void ArgumentosFormales() throws LexicalError, SyntacticalError {
	}

	private void ListaArgumentosFormales() throws LexicalError, SyntacticalError {
	}

	private void ArgumentoFormal() throws LexicalError, SyntacticalError {
	}

	private void ArgumentosActuales() throws LexicalError, SyntacticalError {
	}

	private void FormaMetodo() throws LexicalError, SyntacticalError {
	}

	private void Visibilidad() throws LexicalError, SyntacticalError {
	}

	private void TipoMetodo() throws LexicalError, SyntacticalError {
	}

	private void Tipo() throws LexicalError, SyntacticalError {
	}

	private void TipoPrimitivo() throws LexicalError, SyntacticalError {
	}

	private void TipoReferencia() throws LexicalError, SyntacticalError {
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
