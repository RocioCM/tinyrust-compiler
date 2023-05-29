package syntactic_analyzer;

import java.io.FileNotFoundException;
import java.util.Arrays;

import error.lexic.LexicalError;
import error.semantic.SemanticalError;
import error.semantic.declarations.InternalError;
import error.syntactic.SyntacticalError;
import error.syntactic.UnexpectedToken;
import lexic_analyzer.LexicAnalyzer;
import lexic_analyzer.Token;
import semantic_analyzer.ast.AbstractSyntaxTree;
import semantic_analyzer.ast.AccessArrayNode;
import semantic_analyzer.ast.AccessMethodNode;
import semantic_analyzer.ast.MethodCallNode;
import semantic_analyzer.ast.AccessNode;
import semantic_analyzer.ast.AssignNode;
import semantic_analyzer.ast.BinaryExpressionNode;
import semantic_analyzer.ast.BlockNode;
import semantic_analyzer.ast.ChainedAccessNode;
import semantic_analyzer.ast.ChainedArrayNode;
import semantic_analyzer.ast.ChainedAttributeNode;
import semantic_analyzer.ast.ChainedExpressionNode;
import semantic_analyzer.ast.ConstructorCallNode;
import semantic_analyzer.ast.ConstructorArrayCallNode;
import semantic_analyzer.ast.ChainedMethodNode;
import semantic_analyzer.ast.AccessStaticMethodNode;
import semantic_analyzer.ast.ClassNode;
import semantic_analyzer.ast.ExpressionNode;
import semantic_analyzer.ast.IfElseNode;
import semantic_analyzer.ast.LiteralNode;
import semantic_analyzer.ast.MethodNode;
import semantic_analyzer.ast.ReturnNode;
import semantic_analyzer.ast.SentenceNode;
import semantic_analyzer.ast.SimpleExpressionNode;
import semantic_analyzer.ast.TreeList;
import semantic_analyzer.ast.UnaryExpressionNode;
import semantic_analyzer.ast.AccessVariableNode;
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
		lexic = new LexicAnalyzer(inputPath);
		ts = new SymbolTable(inputPath);
		ast = new AbstractSyntaxTree(inputPath);
	}

	public SyntacticAnalyzer(String inputPath, SymbolTable ts, AbstractSyntaxTree ast)
			throws FileNotFoundException {
		this.lexic = new LexicAnalyzer(inputPath);
		this.ts = ts;
		this.ast = ast;
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
	public void run() throws LexicalError, SyntacticalError, SemanticalError {
		// Se obtienen los dos primeros tokens del archivo.
		token = lexic.nextToken();
		nextToken = lexic.nextToken();

		Program();
	}

	public SymbolTable getTs() {
		return this.ts;
	}

	public AbstractSyntaxTree getAst() {
		return this.ast;
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
			ast.addMain(blockNode, mainToken);
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
		return new ClassNode(classIdToken.getLexema(), methodsNode, classIdToken);
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
		return new MethodNode("create", blockNode, constructorToken);
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
			node = new MethodNode(nameToken.getLexema(), blockNode, nameToken);
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

	private TreeList<ExpressionNode> ArgumentosActuales() throws LexicalError, SyntacticalError {
		TreeList<ExpressionNode> node;
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(")")) {
			node = new TreeList<ExpressionNode>();
			matchLexema(")");
		} else {
			node = ListaExpresiones();
			matchLexema(")");
		}
		return node;
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
				Token ifToken = matchLexema("if");
				matchLexema("(");
				ExpressionNode conditionNode = Expresion();
				matchLexema(")");
				SentenceNode blockNode = Sentencia();
				SentenceNode elseBlockNode = ElseOp();
				node = new IfElseNode(conditionNode, blockNode, elseBlockNode, ifToken);
			} else if (isFirstL("while")) {
				Token whileToken = matchLexema("while");
				matchLexema("(");
				ExpressionNode conditionNode = Expresion();
				matchLexema(")");
				SentenceNode blockNode = Sentencia();
				node = new WhileNode(conditionNode, blockNode, whileToken);
			} else if (isFirstL("{")) {
				node = Bloque();
			} else if (isFirstL("return")) {
				Token returnToken = matchLexema("return");
				if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
						|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
					ExpressionNode expressionNode = Expresion();
					node = new ReturnNode(expressionNode, returnToken);
				} else {
					node = new ReturnNode(returnToken);
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
		Token locationToken = matchLexema("("); // Si no matchea, este método arrojará la excepción.
		ExpressionNode expressionNode = Expresion();
		matchLexema(")");
		return new SimpleExpressionNode(expressionNode, locationToken);
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
		Token locationToken = matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		TreeList<SentenceNode> sentencesNode = Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "UNA SENTENCIA O \"}\"");
		}
		return new BlockNode(sentencesNode, locationToken);
	}

	private BlockNode BloqueMetodo() throws LexicalError, SyntacticalError, SemanticalError {
		Token locationToken = matchLexema("{"); // Si no matchea, este método arrojará la excepción.
		DeclVarLocalesN();
		TreeList<SentenceNode> sentencesNode = Sentencias();
		if (isFirstL("}")) {
			matchLexema("}");
		} else {
			throw new UnexpectedToken(token, "EN ORDEN O UNA DECLARACION DE VARIABLE O UNA SENTENCIA O \"}\"");
		}
		return new BlockNode(sentencesNode, locationToken);
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
			Token locationToken = matchLexema("=");
			ExpressionNode expressionNode = Expresion();
			node = new AssignNode(leftEntity, expressionNode, locationToken);
		} else {
			throw new UnexpectedToken(token, "\"self\" O UN IDENTIFICADOR DE VARIABLE O METODO");
		}
		return node;
	}

	private AccessNode AsignacionVariableSimple() throws LexicalError, SyntacticalError {
		AccessNode node;
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			ExpressionNode indexNode = Expresion();
			matchLexema("]");
			AccessVariableNode varNode = new AccessVariableNode(nameToken.getLexema(), nameToken);
			node = new AccessArrayNode(varNode, indexNode, nameToken);
		} else {
			ChainedAttributeNode chainNode = EncadenadoSimpleN();
			node = new AccessVariableNode(nameToken.getLexema(), chainNode, nameToken);

			// Como EncadenadoSimpleN deriva Lambda, no tirará excepción
			// si el token no matchea con sus primeros.
		}
		return node;
	}

	private AccessVariableNode AsignacionSelfSimple() throws LexicalError, SyntacticalError {
		Token nameToken = matchLexema("self"); // Si no matchea, este método arrojará la excepción.
		ChainedAttributeNode chainNode = EncadenadoSimpleN();
		return new AccessVariableNode(nameToken.getLexema(), chainNode, true, nameToken);
	}

	private TreeList<ExpressionNode> ListaExpresiones() throws LexicalError, SyntacticalError {
		TreeList<ExpressionNode> list;
		ExpressionNode expressionNode = Expresion(); // Si no matchea, este método arrojará la excepción.
		if (isFirstL(",")) {
			matchLexema(",");
			list = ListaExpresiones();
			list.add(expressionNode);
			// No se lanza un error si no matchea "," ya que
			// sería el caso de la última expresión de la lista.
		} else {
			list = new TreeList<ExpressionNode>();
			list.add(expressionNode);
		}
		return list;
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
		ExpressionNode node = null;
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpressionNode leftExpNode = ExpAnd();
			BinaryExpressionNode rightExpNode = ExpOrP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = rightExpNode;
			} else {
				node = leftExpNode;
			}
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private BinaryExpressionNode ExpOrP() throws LexicalError, SyntacticalError {
		BinaryExpressionNode node = null;
		if (isFirstL("||")) {
			Token opToken = matchLexema("||");
			ExpressionNode leftExpNode = ExpAnd();
			BinaryExpressionNode rightExpNode = ExpOrP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = new BinaryExpressionNode(rightExpNode, opToken.getLexema(), new Bool(), new Bool(), opToken);

			} else {
				node = new BinaryExpressionNode(leftExpNode, opToken.getLexema(), new Bool(), new Bool(), opToken);
			}
		}
		// Como ExpOrP deriva Lambda, se continúa la ejecución si no matchea
		return node;
	}

	private ExpressionNode ExpAnd() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpressionNode leftExpNode = ExpIgual();
			BinaryExpressionNode rightExpNode = ExpAndP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = rightExpNode;
			} else {
				node = leftExpNode;
			}
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private BinaryExpressionNode ExpAndP() throws LexicalError, SyntacticalError {
		BinaryExpressionNode node = null;
		if (isFirstL("&&")) {
			Token opToken = matchLexema("&&");
			ExpressionNode leftExpNode = ExpIgual();
			BinaryExpressionNode rightExpNode = ExpAndP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = new BinaryExpressionNode(rightExpNode, opToken.getLexema(), new Bool(), new Bool(), opToken);
			} else {
				node = new BinaryExpressionNode(leftExpNode, opToken.getLexema(), new Bool(), new Bool(), opToken);
			}
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ExpressionNode ExpIgual() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpressionNode leftExpNode = ExpCompuesta();
			BinaryExpressionNode rightExpNode = ExpIgualP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = rightExpNode;
			} else {
				node = leftExpNode;
			}
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private BinaryExpressionNode ExpIgualP() throws LexicalError, SyntacticalError {
		BinaryExpressionNode node = null;
		if (isFirstL("==", "!=")) {
			Token opToken = OpIgual();
			ExpressionNode leftExpNode = ExpCompuesta();
			BinaryExpressionNode rightExpNode = ExpIgualP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = new BinaryExpressionNode(rightExpNode, opToken.getLexema(), null, new Bool(), opToken);
			} else {
				node = new BinaryExpressionNode(leftExpNode, opToken.getLexema(), null, new Bool(), opToken);
			}
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ExpressionNode ExpCompuesta() throws LexicalError, SyntacticalError {
		ExpressionNode node;
		ExpressionNode leftExpNode = ExpAdd(); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("<", "<=", ">", ">=")) {
			Token opToken = OpCompuesto();
			ExpressionNode rightExpNode = ExpAdd();
			node = new BinaryExpressionNode(leftExpNode, rightExpNode, opToken.getLexema(), new I32(), new Bool(),
					opToken);
		} else {
			node = leftExpNode;
		}
		return node;
	}

	private ExpressionNode ExpAdd() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL(
				"+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpressionNode leftExpNode = ExpMul();
			BinaryExpressionNode rightExpNode = ExpAddP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = rightExpNode;
			} else {
				node = leftExpNode;
			}
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private BinaryExpressionNode ExpAddP() throws LexicalError, SyntacticalError {
		BinaryExpressionNode node = null;
		if (isFirstL("+", "-")) {
			Token opToken = OpAdd();
			ExpressionNode leftExpNode = ExpMul();
			BinaryExpressionNode rightExpNode = ExpAddP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = new BinaryExpressionNode(rightExpNode, opToken.getLexema(), new I32(), new I32(), opToken);
			} else {
				node = new BinaryExpressionNode(leftExpNode, opToken.getLexema(), new I32(), new I32(), opToken);
			}
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ExpressionNode ExpMul() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL(
				"+", "-", "!", "nil", "true", "false", "(", "self", "new")
				|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
			ExpressionNode leftExpNode = ExpUn();
			BinaryExpressionNode rightExpNode = ExpMulP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = rightExpNode;
			} else {
				node = leftExpNode;
			}
		} else {
			throw new UnexpectedToken(token, "UNA EXPRESION");
		}
		return node;
	}

	private BinaryExpressionNode ExpMulP() throws LexicalError, SyntacticalError {
		BinaryExpressionNode node = null;
		if (isFirstL("*", "/", "%")) {
			Token opToken = OpMul();
			ExpressionNode leftExpNode = ExpUn();
			BinaryExpressionNode rightExpNode = ExpMulP();
			if (rightExpNode != null) {
				rightExpNode.setLeftOperand(leftExpNode);
				node = new BinaryExpressionNode(rightExpNode, opToken.getLexema(), new I32(), new I32(), opToken);
			} else {
				node = new BinaryExpressionNode(leftExpNode, opToken.getLexema(), new I32(), new I32(), opToken);
			}
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ExpressionNode ExpUn() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("+", "-", "!")) {
			Token opToken = OpUnario();
			ExpressionNode operandNode = ExpUn();
			Type type = opToken.getLexema().equals("!") ? new Bool() : new I32();
			node = new UnaryExpressionNode(operandNode, opToken.getLexema(), type, type, opToken);
		} else {
			if (isFirstL("nil", "true", "false", "(", "self", "new")
					|| isFirstT("id", "lit_int", "lit_string", "lit_char", "id_type")) {
				node = Operando();
			} else {
				throw new UnexpectedToken(token, "UNA EXPRESION");
			}
		}
		return node;
	}

	private Token OpIgual() throws LexicalError, SyntacticalError {
		return matchLexema("==", "!=");
	}

	private Token OpCompuesto() throws LexicalError, SyntacticalError {
		return matchLexema("<", ">", "<=", ">=");
	}

	private Token OpAdd() throws LexicalError, SyntacticalError {
		return matchLexema("+", "-");
	}

	private Token OpUnario() throws LexicalError, SyntacticalError {
		return matchLexema("+", "-", "!");
	}

	private Token OpMul() throws LexicalError, SyntacticalError {
		return matchLexema("*", "/", "%");
	}

	private ExpressionNode Operando() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("(", "self", "new") || isFirstT("id", "id_type")) {
			ExpressionNode primaryNode = Primario();
			ChainedAccessNode chainedAccess = EncadenadoOp();
			if (chainedAccess != null) {
				node = new ChainedExpressionNode("primario encadenado", primaryNode, chainedAccess);
			} else {
				node = primaryNode;
			}
		} else {
			if (isFirstL("nil", "true", "false") || isFirstT("lit_int", "lit_string", "lit_char")) {
				node = Literal();
			} else {
				throw new UnexpectedToken(token, "UN OPERANDO");
			}
		}
		return node;
	}

	private LiteralNode Literal() throws LexicalError, SyntacticalError {
		LiteralNode node = null;
		if (isFirstL("nil", "true", "false")) {
			Token token = matchLexema("nil", "true", "false");
			node = new LiteralNode(token.getLexema(), token.getToken(), token);
		} else {
			if (isFirstT("lit_int", "lit_string", "lit_char")) {
				Token token = matchToken("lit_int", "lit_string", "lit_char");
				node = new LiteralNode(token.getLexema(), token.getToken(), token);
			} else {
				throw new UnexpectedToken(token, "UN LITERAL");
			}
		}
		return node;
	}

	private ExpressionNode Primario() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		if (isFirstL("(")) {
			node = ExpresionParentizada();
		} else {
			if (isFirstL("self")) {
				node = AccesoSelf();
			} else {
				if (isFirstT("id")) {
					// Miramos qué hay después del identificador sin consumirlo
					if (nextToken.getLexema().equals("(")) {
						node = LlamadaMetodo();
					} else {
						node = AccesoVar();
					}
				} else {
					if (isFirstT("id_type")) {
						node = LlamadaMetodoEstatico();
					} else {
						if (isFirstL("new")) {
							node = LlamadaConstructor();
						} else {
							throw new UnexpectedToken(token,
									"\"(\", \"self\", \"new\" O UN IDENTIFICADOR DE CLASE O DE METODO/VARIABLE");
						}
					}
				}
			}
		}
		return node;
	}

	private ExpressionNode ExpresionParentizada() throws LexicalError, SyntacticalError {
		ExpressionNode node;
		matchLexema("("); // Si no matchea, este método arrojará la excepción.
		ExpressionNode expressionNode = Expresion();
		matchLexema(")");
		ChainedAccessNode chainNode = EncadenadoOp();
		if (chainNode != null) {
			node = new ChainedExpressionNode("expresion parentizada", expressionNode, chainNode);
		} else {
			node = expressionNode;
		}
		return node;
	}

	private AccessVariableNode AccesoSelf() throws LexicalError, SyntacticalError {
		Token nameToken = matchLexema("self"); // Si no matchea, este método arrojará la excepción.
		ChainedAccessNode chainNode = EncadenadoOp();
		return new AccessVariableNode(nameToken.getLexema(), chainNode, nameToken);
	}

	private AccessNode AccesoVar() throws LexicalError, SyntacticalError {
		AccessNode node;
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			ExpressionNode indexNode = Expresion();
			matchLexema("]");
			AccessVariableNode varNode = new AccessVariableNode(nameToken.getLexema(), nameToken);
			node = new AccessArrayNode(varNode, indexNode, nameToken);
		} else {
			ChainedAccessNode chainNode = EncadenadoOp();
			node = new AccessVariableNode(nameToken.getLexema(), chainNode, nameToken);
		}
		return node;
	}

	private AccessMethodNode LlamadaMetodo() throws LexicalError, SyntacticalError {
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		TreeList<ExpressionNode> argsNode = ArgumentosActuales();
		ChainedAccessNode chainNode = EncadenadoOp();
		return new AccessMethodNode(nameToken.getLexema(), argsNode, chainNode, nameToken);
	}

	private ExpressionNode LlamadaMetodoEstatico() throws LexicalError, SyntacticalError {
		ExpressionNode node;
		Token typeToken = matchToken("id_type"); // Si no matchea, este método arrojará la excepción.
		matchLexema(".");
		MethodCallNode methodaccessNode = LlamadaMetodo();
		node = new AccessStaticMethodNode(typeToken.getLexema(), methodaccessNode, typeToken);
		ChainedAccessNode chainNode = EncadenadoOp();
		if (chainNode != null) {
			// Dado que la llamada al método ya incluye su encadenado,
			// si llegase a haber otro encadenado luego, se deben anidar.
			node = new ChainedExpressionNode("llamado a metodo estatico encadenado", node, chainNode);
		}
		return node;
	}

	private ExpressionNode LlamadaConstructor() throws LexicalError, SyntacticalError {
		ExpressionNode node = null;
		matchLexema("new"); // Si no matchea, este método arrojará la excepción.
		if (isFirstT("id_type")) {
			Token typeToken = matchToken("id_type");
			TreeList<ExpressionNode> argsNode = ArgumentosActuales();
			ChainedAccessNode chainNode = EncadenadoOp();
			node = new ConstructorCallNode(typeToken.getLexema(), argsNode, chainNode, typeToken);
		} else {
			if (isFirstL("Bool", "I32", "Str", "Char")) {
				PrimitiveType<?> type = TipoPrimitivo();
				Token locationToken = matchLexema("[");
				ExpressionNode expressionNode = Expresion();
				matchLexema("]");
				node = new ConstructorArrayCallNode(type, expressionNode, locationToken);
			} else {
				throw new UnexpectedToken(token, "UN IDENTIFICADOR DE CLASE O TIPO PRIMITIVO");
			}
		}
		return node;
	}

	private ChainedAttributeNode EncadenadoSimpleN() throws LexicalError, SyntacticalError {
		ChainedAttributeNode node = null;
		if (isFirstL(".")) {
			node = EncadenadoSimple();
			ChainedAttributeNode chainNode = EncadenadoSimpleN();
			node.setChainedAccess(chainNode);
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ChainedAttributeNode EncadenadoSimple() throws LexicalError, SyntacticalError {
		matchLexema(".");
		Token nameToken = matchToken("id");
		return new ChainedAttributeNode(nameToken.getLexema(), nameToken);
	}

	private ChainedAccessNode Encadenado() throws LexicalError, SyntacticalError {
		ChainedAccessNode node = null;
		matchLexema(".");
		if (isFirstT("id")) {
			// Miramos qué hay después del identificador sin consumirlo
			if (nextToken.getLexema().equals("(")) {
				node = LlamadaMetodoEncadenado();
			} else {
				node = AccesoVariableEncadenado();
			}
		} else {
			throw new UnexpectedToken(token, "UN IDENTIFICADOR DE METODO O VARIABLE");
		}
		return node;
	}

	private ChainedAccessNode EncadenadoOp() throws LexicalError, SyntacticalError {
		ChainedAccessNode node = null;
		if (isFirstL(".")) {
			node = Encadenado();
		}
		// Como deriva Lambda, no se lanza excepción si no matchea
		return node;
	}

	private ChainedAccessNode AccesoVariableEncadenado() throws LexicalError, SyntacticalError {
		ChainedAccessNode node;
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		if (isFirstL("[")) {
			matchLexema("[");
			ExpressionNode indexNode = Expresion();
			matchLexema("]");
			node = new ChainedArrayNode(nameToken.getLexema(), indexNode, nameToken);
		} else {
			ChainedAccessNode chainNode = EncadenadoOp();
			node = new ChainedAttributeNode(nameToken.getLexema(), chainNode, nameToken);
		}
		return node;
	}

	private ChainedMethodNode LlamadaMetodoEncadenado() throws LexicalError, SyntacticalError {
		Token nameToken = matchToken("id"); // Si no matchea, este método arrojará la excepción.
		TreeList<ExpressionNode> argsNode = ArgumentosActuales();
		ChainedAccessNode chainNode = EncadenadoOp();
		return new ChainedMethodNode(nameToken.getLexema(), argsNode, chainNode, nameToken);
	}
}
