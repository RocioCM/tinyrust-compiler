package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class ReturnNode extends SentenceNode {
	private ExpressionNode returnValue;

	public ReturnNode(Location loc) {
		super(loc);
		this.returnValue = new LiteralNode("nil", "p_nil", loc);
	}

	public ReturnNode(ExpressionNode expression, Location loc) {
		super(loc);
		this.returnValue = expression;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Return");
		json.addAttr("expresion-retorno", returnValue);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		// Validar que el tipo de la expresión coincida con el de retorno del método.
		try {
			Type expectedType = ts.currentMethod().returnType();
			returnValue.setExpectedResolveType(expectedType);
		} catch (error.semantic.declarations.InternalError e) {
			throw new InternalError(loc, e.getMessage());
		}
		returnValue.validate(ts);

	}
}
