package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.UnmatchedTypeError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Code;
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
		super.validateType(ts); // Validar que esta expresión sea del tipo esperado para su contexto.
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		code.add(leftOperand.generateCode(ts));
		code.pushToStackFrom("$a0"); // Save left operand value to Stack.

		code.add(rightOperand.generateCode(ts));
		code.popFromStackTo("$t1"); // Move left operand from stack to temporal register.

		// Implement ASM operation according to operator.
		// $t1 = left operand (1); $a0 = right operand (2).
		switch (operator) {
			case "+":
				code.addLine("addu $a0 $t1 $a0    # Sum up the two expressions results.");
				break;

			case "-":
				code.addLine("subu $a0 $t1 $a0    # Substract the two expressions results.");
				break;

			case "&&":
				code.addLine("and $a0 $t1 $a0    # Execute AND on the two expressions results.");
				break;

			case "||":
				code.addLine("or $a0 $t1 $a0    # Execute OR on the two expressions results.");
				break;

			case "==":
				code.addLine("seq $a0 $t1 $a0    # Compare the two expressions are equal.");
				break;

			case "!=":
				code.addLine("sne $a0 $t1 $a0    # Check the two expressions are different.");
				break;

			case "<":
				code.addLine("slt $a0 $t1 $a0    # Check left expression is lesser than right one.");
				break;

			case "<=":
				code.addLine("sle $a0 $t1 $a0    # Check left expression is lesser than or equal to right one.");
				break;

			case ">":
				code.addLine("sgt $a0 $t1 $a0    # Check left expression is greater than right one.");
				break;

			case ">=":
				code.addLine("sge $a0 $t1 $a0    # Check left expression is greater than or equal to right one.");
				break;

			case "*":
				code.addLine("mul $a0 $a0 $t1    # Multiply the two expressions values.");
				break;

			case "/":
				code.addLine("li $a1, ", String.valueOf(super.loc.getLine()), "    # Save line in case of division error.");
				code.addLine("li $a2, ", String.valueOf(super.loc.getCol()), "    # Save column in case of division error.");
				code.addLine("beq $t1, $0, error_zero_division    # Validate zero-division..");
				code.addLine("div $t1 $a0   # Divide the two expressions vañues (a0/t1).");
				code.addLine("mflo $a0     # Save division result in accumulator.");
				break;

			case "%":
				code.addLine("li $a1, ", String.valueOf(super.loc.getLine()), "    # Save line in case of division error.");
				code.addLine("li $a2, ", String.valueOf(super.loc.getCol()), "    # Save column in case of division error.");
				code.addLine("beq $t1, $0, error_zero_division    # Validate zero-division..");
				code.addLine("div $t1 $a0   # Divide the two expressions values (a0/t1).");
				code.addLine("mfhi $a0     # Save division remainder in accumulator.");
				break;

			default:
				break;
		}

		return code.getCode();
	}

	public void setLeftOperand(ExpressionNode leftOperand) {
		this.leftOperand = leftOperand;
	}
}
