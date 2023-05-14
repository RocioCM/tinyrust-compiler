package semantic_analyzer.ast;

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
}
