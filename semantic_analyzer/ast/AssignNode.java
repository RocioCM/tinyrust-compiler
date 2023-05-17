package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class AssignNode extends SentenceNode {
	AccessNode leftSide;
	ExpressionNode rightSide;

	public AssignNode(AccessNode leftSide, ExpressionNode rightSide) {
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
}
