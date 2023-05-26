package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class ConstructorCallNode extends MethodCallNode {

	public ConstructorCallNode(String className, TreeList<ExpressionNode> arguments,
			ChainedAccessNode chainedAccess, Location loc) {
		super(className, "create", arguments, chainedAccess, loc);
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "llamada constructor");
		json.addAttr("clase", super.className());
		json.addAttr("argumentos", super.arguments());
		json.addAttr("tipo-resuelto", super.resolveType());
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		super.validate(ts); // Validar que la llamada al método create sea válida.
	}
}
