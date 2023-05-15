package syntactic_analyzer;

import java.io.FileNotFoundException;
import java.util.Arrays;

import error.lexic.LexicalError;
import error.semantic.InternalError;
import error.semantic.SemanticalError;
import error.syntactic.SyntacticalError;
import error.syntactic.UnexpectedToken;
import lexic_analyzer.LexicAnalyzer;
import lexic_analyzer.Token;
import semantic_analyzer.ast.AbstractSyntaxTree;
import semantic_analyzer.ast.AccessArrayNode;
import semantic_analyzer.ast.AccessNode;
import semantic_analyzer.ast.AccessSimpleChain;
import semantic_analyzer.ast.AssignNode;
import semantic_analyzer.ast.BlockNode;
import semantic_analyzer.ast.ClassNode;
import semantic_analyzer.ast.ExpressionNode;
import semantic_analyzer.ast.IfElseNode;
import semantic_analyzer.ast.LiteralNode;
import semantic_analyzer.ast.MethodNode;
import semantic_analyzer.ast.ReturnNode;
import semantic_analyzer.ast.SentenceNode;
import semantic_analyzer.ast.SimpleExpressionNode;
import semantic_analyzer.ast.TreeList;
import semantic_analyzer.ast.VariableNode;
import semantic_analyzer.ast.WhileNode;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Char;
import semantic_analyzer.types.ClassType;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.PrimitiveType;
import semantic_analyzer.types.Str;
import semantic_analyzer.types.Type;
import semantic_analyzer.types.Void;

/**
 * Analizador Sintáctico descendente predictivo recursivo de TinyRust+.
 * Consume un archivo de entrada y determina si la estructura sintáctica del
 * archivo se corresponde con un programa válido de TinyRust+.
 * Hace uso de la clase LexicAnalyzer para identificar los tokens del archivo.
 */
public class SyntacticAnalyzer {
	private LexicAnalyzer lexic;
	private Token token;
	private Token nextToken;
	private SymbolTable ts;
	private AbstractSyntaxTree ast;

	public SyntacticAnalyzer(String inputPath) throws FileNotFoundException, InternalError {
		// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
		lexic = new LexicAnalyzer(inputPath);
		ts = new SymbolTable(inputPath);
		ast = new AbstractSyntaxTree(inputPath);
	}

	/**
	 * Inicia el análisis sintáctico del archivo provisto al analizador sintáctico.
	 * 
	 * @return la tabla de símbolos si el análisis fue exitoso.
	 *         Caso contrario lanzará una excepción.
	 * @throws LexicalError     - Si algún token del archivo no es válido.
	 * @throws SyntacticalError - Si la estructura sintáctica del archivo no se
	 *                          corresponde con un programa válido de TinyRust+.
	 * @throws SemanticalError  - Si alguna sentencia del archivo no respeta la
	 *                          semántica de TinyRust+.
	 */
	public String run() throws LexicalError, SyntacticalError, SemanticalError {
		// Se obtienen los dos primeros tokens del archivo.
		token = lexic.nextToken();
		nextToken = lexic.nextToken();

		try {
			Program();
		} catch (SemanticalError e) {
			throw new SemanticalError(token.getLine(), token.getCol(), e.getMessage());
		} // TODO: remove this try-catch, just leave: Program();

		// Si la entrada no es sintácticamente correcta, Program lanza una excepción,
		// por lo que si termina de ejecutarse, implica que la entrada es correcta y
		// la tabla de símbolos se construyó por completo.
		ts.consolidate();
		ast.consolidate(ts);
		return (ts.toJson());
	}

	public String getAstJson() {
		return ast.toJson();
	}

	/**
	 * Compara el tipo del token actual contra la lista de tipos de token recibida.
	 * Si coincide con algún elemento de la lista, consume el token.
	 * 
	 * @param types Tipos posibles esperados del token.
	 * @return el token consumido.
	 * @throws LexicalError     - Si el siguiente token no es válido.
	 * @throws SyntacticalError - Si el token no coincide con ningún tipo esperado.
	 */
	private Token matchToken(String... types) throws LexicalError, SyntacticalError {
		Token consumedToken = token;
		if (Arrays.asList(types).contains(token.getToken())) {
			token = nextToken;
			nextToken = lexic.nextToken();
		} else {
			String typesAsString = String.join(", ", types);
			throw new UnexpectedToken(token, "TOKEN DE TIPO: " + typesAsString);
		}
		return consumedToken;
	}

	/**
	 * Compara el lexema del token actual contra la lista de lexemas recibida.
	 * Si coincide con algún elemento de la lista, consume el token.
	 * 
	 * @param lexema Lexemas posibles esperados del token.
	 * @return el token consumido.
	 * @throws LexicalError     - Si el siguiente token no es válido.
	 * @throws SyntacticalError - Si el token no coincide con ningún lexema
	 *                          esperado.
	 */
	private Token matchLexema(String... types) throws LexicalError, SyntacticalError {
		Token consumedToken = token;
		if (Arrays.asList(types).contains(token.getLexema())) {
			token = nextToken;
			nextToken = lexic.nextToken();
		} else {
			String typesAsString = String.join(", ", types);
			throw new UnexpectedToken(token, typesAsString);
		}
		return consumedToken;
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

	private void Program() throws LexicalError, SyntacticalError, SemanticalError {
		Start();
		matchToken("EOF");
	}

	private void Start() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL("class", "fn")) {
			TreeList<ClassNode> classesNode = Clases();
			ast.setClasses(classesNode);
			Main();
		} else {
			throw new UnexpectedToken(token, "DECLARACION DE CLASES O METODO MAIN");
		}
	}

	private TreeList<ClassNode> Clases() throws LexicalError, SyntacticalError, SemanticalError {
		TreeList<ClassNode> classesNode;
		if (isFirstL("class")) {
			ClassNode classNode = Clase();
			classesNode = Clases();
			classesNode.add(classNode);
		} else {
			classesNode = new TreeList<ClassNode>();
			// Como Clases deriva Lambda, se continúa la ejecución
			// si el token no matchea con los primeros.
		}
		return classesNode;

	}

	private void Main() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL("fn")) {
			matchLexema("fn");
			Token mainToken = matchLexema("main");
			matchLexema("(");
			matchLexema(")");
			ts.addMain(mainToken);
			BlockNode blockNode = BloqueMetodo();
			ast.addMain(blockNode);
		} else {
			throw new UnexpectedToken(token, "EL METODO MAIN");
		}
	}

	private ClassNode Clase() throws LexicalError, SyntacticalError, SemanticalError {
		matchLexema("class"); // Si no matchea, este método arrojará la excepción.
		Token classIdToken = matchToken("id_type");
		ts.addClass(classIdToken.getLexema(), classIdToken);
		TreeList<MethodNode> methodsNode = ClaseHerenciaOp();
		ts.endClass();
		return new ClassNode(classIdToken.getLexema(), methodsNode);
	}

	private TreeList<MethodNode> ClaseHerenciaOp() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL(":")) {
			Herencia();
		}
		matchLexema("{");
		TreeList<MethodNode> methods = Miembros();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "UN CONSTRUCTOR, METODO, ATRIBUTO O \"}\"");
		}
		return methods;
	}

	private void Herencia() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL(":")) {
			matchLexema(":");
			Token typeToken = matchToken("id_type");
			ts.currentClass().setExtendsFrom(typeToken.getLexema(), typeToken);
		} else {
			throw new UnexpectedToken(token, "\":\" (HERENCIA)");
		}
	}

	private TreeList<MethodNode> Miembros() throws LexicalError, SyntacticalError, SemanticalError {
		TreeList<MethodNode> methodsNodes = new TreeList<MethodNode>();
		if (isFirstL("Bool", "I32", "Str", "Char", "Array", "pub", "fn", "static", "create") || isFirstT("id_type")) {
			MethodNode memberNode = Miembro();
			methodsNodes = Miembros();
			if (memberNode != null) {
				// member es null cuando el miembro es un atributo en vez de un método o
				// constructor.
				methodsNodes.add(memberNode); // La lista de nodos únicamente contendrá métodos.
			}
		} else {
			methodsNodes = new TreeList<MethodNode>();
			// Como Miembros deriva Lambda, se continúa la ejecución
			// si el token no matchea con los primeros.
		}
		return methodsNodes;
	}

	private MethodNode Miembro() throws LexicalError, SyntacticalError, SemanticalError {
		MethodNode nodo;
		if (isFirstL("create")) {
			nodo = Constructor();
		} else {
			if (isFirstL("fn", "static")) {
				nodo = Metodo();
			} else {
				if (isFirstL("Bool", "I32", "Str", "Char", "Array", "pub") || isFirstT("id_type")) {
					Atributo();
					nodo = null;
				} else {
					throw new UnexpectedToken(token, "CONSTRUCTOR, METODO O ATRIBUTO");
				}
			}
		}
		return nodo;
	}

	private void Atributo() throws LexicalError, SyntacticalError, SemanticalError {
		boolean isPublic = false;
		if (isFirstL("pub")) {
			isPublic = Visibilidad();
		}
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Type type = Tipo();
			matchLexema(":");
			ListaDeclaracionVariables(type, isPublic);
			matchLexema(";");
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private MethodNode Constructor() throws LexicalError, SyntacticalError, SemanticalError {
		Token constructorToken = matchLexema("create"); // Si no matchea, este método arrojará la excepción.
		ts.addConstructor(constructorToken);
		ArgumentosFormales();
		BlockNode blockNode = BloqueMetodo();
		ts.endMethod();
		return new MethodNode("create", blockNode);
	}

	private MethodNode Metodo() throws LexicalError, SyntacticalError, SemanticalError {
		boolean isStatic = false;
		MethodNode node = null;
		if (isFirstL("static")) {
			isStatic = FormaMetodo();
		}
		if (isFirstL("fn")) {
			matchLexema("fn");
			Token nameToken = matchToken("id");
			ts.addMethod(nameToken.getLexema(), isStatic, nameToken);
			ArgumentosFormales();
			matchLexema("->");
			Type returnType = TipoMetodo();
			ts.currentMethod().setReturnType(returnType);
			BlockNode blockNode = BloqueMetodo();
			node = new MethodNode(nameToken.getLexema(), blockNode);
			ts.endMethod();
		} else {
			throw new UnexpectedToken(token, "\"fn\" (METODO)");
		}
		return node;
	}

	private void ArgumentosFormales() throws LexicalError, SyntacticalError, SemanticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(")")) {
			matchLexema(")");
		} else {
			ListaArgumentosFormales();
			matchLexema(")");
		}
	}

	private void ListaArgumentosFormales() throws LexicalError, SyntacticalError, SemanticalError {
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

	private void ArgumentoFormal() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Type type = Tipo();
			matchLexema(":");
			Token nameToken = matchToken("id");
			ts.currentMethod().addArgument(nameToken.getLexema(), type, nameToken);
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

	private boolean FormaMetodo() throws LexicalError, SyntacticalError {
		matchLexema("static"); // Si no matchea, este método arrojará la excepción.
		return true;
	}

	private boolean Visibilidad() throws LexicalError, SyntacticalError {
		matchLexema("pub"); // Si no matchea, este método arrojará la excepción.
		return true;
	}

	private Type TipoMetodo() throws LexicalError, SyntacticalError {
		Type tsType;
		if (isFirstL("Bool", "I32", "Str", "Char", "Array", "void") || isFirstT("id_type")) {
			if (isFirstL("void")) {
				matchLexema("void");
				tsType = new Void();
			} else {
				tsType = Tipo();
			}
		} else {
			throw new UnexpectedToken(token, "UN TIPO DE RETORNO (CLASE O VOID)");
		}
		return tsType; // Para este punto tsType siempre tendrá un valor asignado,
		// en cualquier otro caso, se lanza una excepción antes.
	}

	private Type Tipo() throws LexicalError, SyntacticalError {
		Type tsType = null;
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			if (isFirstL("Array")) {
				tsType = TipoArray();
			} else if (isFirstT("id_type")) {
				tsType = TipoReferencia();
			} else if (isFirstL("Bool", "I32", "Str", "Char")) {
				tsType = TipoPrimitivo();
			}
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
		return tsType;
	}

	private PrimitiveType<?> TipoPrimitivo() throws LexicalError, SyntacticalError {
		PrimitiveType<?> tsType;
		Token typeToken = matchLexema("Bool", "I32", "Str", "Char");

		switch (typeToken.getLexema()) {
			case "Bool":
				tsType = new Bool();
				break;
			case "I32":
				tsType = new I32();
				break;
			case "Str":
				tsType = new Str();
				break;
			case "Char":
				tsType = new Char();
				break;
			default:
				tsType = null; // Nunca se llega a este caso, porque matchLexema arrojaría una excepción si el
												// tipo no fuera uno de los 4 casos del switch, pero se deja por prolijidad.
		}
		return tsType;
	}

	private Type TipoReferencia() throws LexicalError, SyntacticalError {
		Token classToken = matchToken("id_type");
		return new ClassType(classToken.getLexema());
	}

	private Array TipoArray() throws LexicalError, SyntacticalError {
		matchLexema("Array"); // Si no matchea, este método arrojará la excepción.
		PrimitiveType<?> type = TipoPrimitivo();
		return new Array(type);
	}

	private void ListaDeclaracionVariables(Type varType, boolean isPublic)
			throws LexicalError, SyntacticalError, SemanticalError {
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		ts.addVar(nameToken.getLexema(), varType, isPublic, nameToken);
		if (isFirstL(",")) {
			matchLexema(",");
			ListaDeclaracionVariables(varType, isPublic);
			// No se lanza un error si no matchea "," ya que
			// sería el caso de la última variable de la lista.
		}
	}

	private void DeclVarLocalesN() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			DeclVarLocales();
			DeclVarLocalesN();
		}
		// Como DeclVarLocalesN deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
	}

	private void DeclVarLocales() throws LexicalError, SyntacticalError, SemanticalError {
		if (isFirstL("Bool", "I32", "Str", "Char", "Array") || isFirstT("id_type")) {
			Type type = Tipo();
			matchLexema(":");
			ListaDeclaracionVariables(type, false);
			matchLexema(";");
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
		}
	}

	private SentenceNode Sentencia() throws LexicalError, SyntacticalError {
		SentenceNode node = null;
		if (isFirstL(";", "self", "(", "if", "while", "{", "return") || isFirstT("id")) {
			if (isFirstL(";")) {
				matchLexema(";");
			} else if (isFirstL("self") || isFirstT("id")) {
				node = Asignacion();
				matchLexema(";");
			} else if (isFirstL("(")) {
				node = SentenciaSimple();
				matchLexema(";");
			} else if (isFirstL("if")) {
				matchLexema("if");
				matchLexema("(");
				ExpressionNode conditionNode = Expresion();
				matchLexema(")");
				SentenceNode blockNode = Sentencia();
				SentenceNode elseBlockNode = ElseOp();
				node = new IfElseNode(conditionNode, blockNode, elseBlockNode);
			} else if (isFirstL("while")) {
				matchLexema("while");
				matchLexema("(");
				ExpressionNode conditionNode = Expresion();
				matchLexema(")");
				SentenceNode blockNode = Sentencia();
				node = new WhileNode(conditionNode, blockNode);
			} else if (isFirstL("{")) {
				node = Bloque();
			} else if (isFirstL("return")) {
				matchLexema("return");
				if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
						|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
					ExpressionNode expressionNode = Expresion();
					node = new ReturnNode(expressionNode);
				} else {
					node = new ReturnNode();
				}
				matchLexema(";");
			}
		} else {
			throw new UnexpectedToken(token, "UNA SENTENCIA");
		}
		return node;
	}

	private TreeList<SentenceNode> Sentencias() throws LexicalError, SyntacticalError {
		TreeList<SentenceNode> sentencesNode;
		if (isFirstL(";", "self", "(", "if", "while", "{", "return") || isFirstT("id")) {
			SentenceNode sentence = Sentencia();
			sentencesNode = Sentencias();
			if (sentence != null) {
				// La sentencia es nula si es únicamente un punto y coma.
				// En ese caso se ignora agregar la sentencia al el AST.
				sentencesNode.add(sentence);
			}
		} else {
			sentencesNode = new TreeList<SentenceNode>();
			// Como Sentencias deriva Lambda, se continúa la ejecución
			// si el token no matchea con los primeros.
		}
		return sentencesNode;
	}

	private SimpleExpressionNode SentenciaSimple() throws LexicalError, SyntacticalError {
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		ExpressionNode expressionNode = Expresion();
		matchLexema(")");
		return new SimpleExpressionNode(expressionNode);
	}

	private SentenceNode ElseOp() throws LexicalError, SyntacticalError {
		SentenceNode node = null;
		if (isFirstL("else")) {
			matchLexema("else");
			node = Sentencia();
		}
		// Como ElseOp deriva Lambda, se continúa la ejecución
		// si el token no matchea con los primeros.
		return node;
	}

	private BlockNode Bloque() throws LexicalError, SyntacticalError {
		matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		TreeList<SentenceNode> sentencesNode = Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "UNA SENTENCIA O \"}\"");
		}
		return new BlockNode(sentencesNode);
	}

	private BlockNode BloqueMetodo() throws LexicalError, SyntacticalError, SemanticalError {
		matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		DeclVarLocalesN();
		TreeList<SentenceNode> sentencesNode = Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "EN ORDEN O UNA DECLARACION DE VARIABLE O UNA SENTENCIA O \"}\"");
		}
		return new BlockNode(sentencesNode);
	}

	private AssignNode Asignacion() throws LexicalError, SyntacticalError {
		AssignNode node = null;
		if (isFirstL("self") || isFirstT("id")) {
			AccessNode leftEntity;
			if (isFirstL("self")) {
				leftEntity = AsignacionSelfSimple();
			} else {
				leftEntity = AsignacionVariableSimple();
			}
			matchLexema("=");
			ExpressionNode expressionNode = Expresion();
			node = new AssignNode(leftEntity, expressionNode);
		} else {
			throw new UnexpectedToken(token, "\"self\" O UN IDENTIFICADOR DE VARIABLE O METODO");
		}
		return node;
	}

	private AccessNode AsignacionVariableSimple() throws LexicalError, SyntacticalError {
		AccessNode node;
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		VariableNode idNode = new VariableNode(nameToken.getLexema());
		if (isFirstL("[")) {
			matchLexema("[");
			ExpressionNode indexNode = Expresion();
			matchLexema("]");
			node = new AccessArrayNode(idNode, indexNode);
		} else {
			AccessNode chainNode = EncadenadoSimpleN();
			node = new AccessSimpleChain(idNode, chainNode);
			// Como EncadenadoSimpleN deriva Lambda, no tirará excepción
			// si el token no matchea con sus primeros.
		}
		return node;
	}

	private AccessNode AsignacionSelfSimple() throws LexicalError, SyntacticalError {
		Token nameToken = matchLexema("self"); // Si no matchea, este método arrojará la excepción.
		AccessNode chainNode = EncadenadoSimpleN();
		VariableNode idNode = new VariableNode(nameToken.getLexema());
		return new AccessSimpleChain(idNode, chainNode);
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

	private ExpressionNode Expresion() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			node = ExpOr();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private ExpressionNode ExpOr() throws LexicalError, SyntacticalError {
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpAnd();
			ExpOrP();
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return null; /// TODO
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

	private LiteralNode Literal() throws LexicalError, SyntacticalError {
		LiteralNode node = null;
		if (isFirstL("nil", "true", "false")) {
			Token token = matchLexema("nil", "true", "false");
			node = new LiteralNode(token.getLexema(), token.getToken());
		} else {
			if (isFirstT("lit_int", "lit_string", "lit_char")) {
				Token token = matchToken("lit_int", "lit_string", "lit_char");
				node = new LiteralNode(token.getLexema(), token.getToken());
			} else {
				throw new UnexpectedToken(token, "UN LITERAL");
			}
		}
		return node;
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

	private AccessSimpleChain EncadenadoSimpleN() throws LexicalError, SyntacticalError {
		AccessSimpleChain node = null;
		if (isFirstL(".")) {
			VariableNode variableNode = EncadenadoSimple();
			AccessSimpleChain chainNode = EncadenadoSimpleN();
			node = new AccessSimpleChain(variableNode, chainNode);
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private VariableNode EncadenadoSimple() throws LexicalError, SyntacticalError {
		matchLexema(".");
		Token nameToken = matchToken("id");
		return new VariableNode(nameToken.getLexema());
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
