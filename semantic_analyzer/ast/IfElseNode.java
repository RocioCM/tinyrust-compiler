package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Bool;
import util.Json;

public class IfElseNode extends SentenceNode {
	private ExpressionNode condition;
	private SentenceNode block;
	private SentenceNode elseBlock;

	public IfElseNode(ExpressionNode condition, SentenceNode block, SentenceNode elseBlock, Location loc) {
		super(loc);
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "If-Else");
		json.addAttr("condicion", condition);
		json.addAttr("sentencia-if", block);
		json.addAttr("sentencia-else", elseBlock);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		// Validar que la condición sea una expresión booleana:
		condition.setExpectedResolveType(new Bool());
		condition.validate(ts);

		// Validar las sentencias de cada bloque.
		block.setExpectedReturnType(super.expectedReturnType());
		block.validate(ts);
		if (elseBlock != null) {
			elseBlock.setExpectedReturnType(super.expectedReturnType());
			elseBlock.validate(ts);

			// Validar el retorno de ambos bloques.
			if (block.resolvedReturnType() != null // El bloque if tiene un retorno.
					&& block.resolvedReturnType().equals(elseBlock.resolvedReturnType())) {
				super.setResolvedReturnType(block.resolvedReturnType());
			}
		}
	}
}
