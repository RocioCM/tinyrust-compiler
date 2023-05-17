package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class BlockNode extends SentenceNode {
	private TreeList<SentenceNode> sentences;

	public BlockNode(TreeList<SentenceNode> sentences) {
		this.sentences = sentences;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Bloque");
		json.addAttr("sentencias", sentences);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		sentences.validate(ts); // Validar cada sentencia del bloque.
	}
}
