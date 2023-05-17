package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;

public abstract class AccessNode extends ExpressionNode {

	abstract public void validate(SymbolTable ts) throws ASTError;
}
