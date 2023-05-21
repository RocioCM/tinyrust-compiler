package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class BlockNode extends SentenceNode {
	private TreeList<SentenceNode> sentences;

	public BlockNode(TreeList<SentenceNode> sentences, Location loc) {
		super(loc);
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
