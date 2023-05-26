package semantic_analyzer.ast;

import java.util.Iterator;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Void;
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
		Iterator<SentenceNode> sentencesIter = sentences.iterator();

		// Validar cada sentencia del bloque.
		while (sentencesIter.hasNext()) {
			SentenceNode sentence = sentencesIter.next();

			sentence.setExpectedReturnType(super.expectedReturnType());
			sentence.validate(ts);

			// Si la sentencia tiene un tipo de retorno concreto, determina el retorno de
			// todo el bloque.
			if (sentence.resolvedReturnType() != null) {
				super.setResolvedReturnType(sentence.resolvedReturnType());
			}
		}

		// Si se esperaba un return de tipo Void pero el bloque no tiene ningún retorno
		// explícito, se guarda Void como tipo resuelto para evitar un error al validar.
		if (super.expectedReturnType().equals(new Void()) && super.resolvedReturnType() == null) {
			super.setResolvedReturnType(new Void());
		}
	}
}
