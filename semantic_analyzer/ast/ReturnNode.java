package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Void;
import util.Json;

public class ReturnNode extends SentenceNode {
	private ExpressionNode returnValue;

	public ReturnNode() {
		this.returnValue = new Void();
	}

	public ReturnNode(ExpressionNode expression) {
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
		returnValue.setExpectedResolveType(null); // TODO: De alguna forma hay que hacerle llegar el nombre del método a
																							// esta función. O setearlo en la TS como currentMethod.
		returnValue.validate(ts);

	}
}
