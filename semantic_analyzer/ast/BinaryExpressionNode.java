package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.UnmatchedTypeError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class BinaryExpressionNode extends ExpressionNode {
	private ExpressionNode leftOperand;
	private ExpressionNode rightOperand;
	private String operator;
	private Type expectedOperandType; // Si es null, indica que ambos operandos deben ser del mismo tipo,
										// sin importar qué tipo en específico.

	public BinaryExpressionNode(ExpressionNode rightOperand, String operator,
			Type expectedOperandType, Type resultType, Location loc) {
		super(resultType, loc);
		this.leftOperand = null;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	public BinaryExpressionNode(ExpressionNode leftOperand, ExpressionNode rightOperand, String operator,
			Type expectedOperandType, Type resultType, Location loc) {
		super(resultType, loc);
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "expresion binaria");
		json.addAttr("operador", operator);
		json.addAttr("tipo-de-retorno", super.resolveType());
		if (expectedOperandType != null) {
			json.addAttr("tipo-de-operandos", expectedOperandType);
		} else {
			json.addAttr("tipo-de-operandos", "Mismo tipo");
		}
		json.addAttr("operando-derecha", leftOperand);
		json.addAttr("operando-izquierda", rightOperand);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (leftOperand == null) {
			throw new InternalError(loc,
					"EL LADO IZQUIERDO DE LA EXPRESION BINARIA NO ESTA DEFINIDO. SE ESPERABA QUE ESTUVIERA DEFINIDO PARA ESTE PUNTO.");
		}
		// Validar que el tipo de los operandos sea el esperado para este operador.
		leftOperand.setExpectedResolveType(expectedOperandType);
		rightOperand.setExpectedResolveType(expectedOperandType);
		leftOperand.validate(ts);
		rightOperand.validate(ts);

		// En caso de que el tipo esperado de los operandos sea null,
		// se valida que ambos operandos sean del mismo tipo.
		if (!leftOperand.resolveType().equals(rightOperand.resolveType())) {
			throw new UnmatchedTypeError(loc, leftOperand.resolveType(), rightOperand.resolveType());
		}
		super.validate(ts); // Validar que esta expresión sea del tipo esperado para su contexto.
	}

	public void setLeftOperand(ExpressionNode leftOperand) {
		this.leftOperand = leftOperand;
	}
}
