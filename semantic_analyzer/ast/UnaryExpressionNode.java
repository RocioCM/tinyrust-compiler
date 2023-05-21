package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class UnaryExpressionNode extends ExpressionNode {
	private ExpressionNode operand;
	private String operator;

	public UnaryExpressionNode(ExpressionNode operand, String operator, Type expectedOperandType, Type resolveType,
			Location loc) {
		super(resolveType, loc);
		this.operator = operator;
		this.operand = operand;
		operand.setExpectedResolveType(expectedOperandType);
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "expresion unaria");
		json.addAttr("operador", operator);
		json.addAttr("tipo-de-retorno", super.resolveType());
		json.addAttr("tipo-operando", operand.resolveType());
		json.addAttr("operando", operand);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		operand.validate(ts); // Validar que el tipo del operando es el esperado para este operador.
		super.validateType(ts); // Validar que esta expresión es del tipo esperado para su contexto.
	}
}
