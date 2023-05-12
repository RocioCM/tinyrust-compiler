package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class MethodBlockNode implements TableElement {
	private TreeList<SentenceNode> sentences;

	public MethodBlockNode() {
		this.sentences = new TreeList<SentenceNode>();
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("sentencias", sentences);
		return json.toString();
	}

	public void addSentence(SentenceNode sent) {
		sentences.add(sent);
	}

}
