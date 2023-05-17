package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.TableElement;

public interface Node extends TableElement {
	public void validate(SymbolTable ts) throws ASTError;
}
