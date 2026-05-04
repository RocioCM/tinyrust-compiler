package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class AssignNode extends SentenceNode {
	AccessNode leftSide;
	ExpressionNode rightSide;

	public AssignNode(AccessNode leftSide, ExpressionNode rightSide, Location loc) {
		super(loc);
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Asignacion");
		json.addAttr("izquierda", leftSide);
		json.addAttr("derecha", rightSide);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		leftSide.validate(ts); // Validar y resolver el acceso de la izquierda.
		// Validar que el tipo del lado derecho coincida con el izquierdo:
		rightSide.setExpectedResolveType(leftSide.resolveType());
		rightSide.validate(ts);
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		code.add(rightSide.generateCode(ts)); // Evaluate the right expression.
		code.pushToStackFrom("$a0"); // Save the expression result in stack.

		// Tip: AccessNode returns an address at $v0, and a value at $a0.
		// Depending on the case, you may use the value or the address.

		// Assign the right expression to the left address.
		code.add(leftSide.generateCode(ts));
		code.popFromStackTo("$t1"); // Save assignment value in temporary register.
		code.addLine("sw $t1, 0($v0)    # Save expression result in left variable.");
		code.addLine("add $a0, $0, $0 # Clear accumulator value.");
		code.addLine("add $v0, $0, $0 # Clear v0 address value.");

		return code.getCode();
	}
}
