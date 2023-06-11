package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class ReturnNode extends SentenceNode {
	private ExpressionNode returnValue;
	private String cleanUpLabel; /// TODO: calc this during validate.

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
		returnValue.setExpectedResolveType(super.expectedReturnType());
		returnValue.validate(ts);

		// Si la validación de returnValue pasó, significa que el tipo resuelto es
		// válido para el método.
		super.setResolvedReturnType(super.expectedReturnType());

	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code(returnValue.generateCode(ts));

		// Tip: at this point, result value is already on the accumulator.
		code.addLine("jr $ra    # Jump to next instruction address after function call.");

		return code.getCode();
	}
}
