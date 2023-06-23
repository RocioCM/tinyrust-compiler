package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Bool;
import util.Code;
import util.Json;

public class WhileNode extends SentenceNode {
	static private int instancesCounter = 0; // Contador para asignar un id único al nodo.
	private int id;
	private ExpressionNode condition;
	private SentenceNode block;

	public WhileNode(ExpressionNode condition, SentenceNode block, Location loc) {
		super(loc);
		this.condition = condition;
		this.block = block;
		this.id = WhileNode.instancesCounter + 1;
		WhileNode.instancesCounter++;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "While");
		json.addAttr("condicion", condition);
		json.addAttr("sentencia", block);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		condition.setExpectedResolveType(new Bool()); // Validar que el tipo de la condición sea booleano
		condition.validate(ts);

		block.setExpectedReturnType(super.expectedReturnType());
		block.validate(ts); // Validar las sentencias del bloque.
		super.setResolvedReturnType(block.resolvedReturnType()); // Para validar el retorno del método.
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		String whileLabel = "while_" + id;
		String finallyLabel = "after_while_" + id;

		code.addLine(whileLabel, ":");
		code.addLine(condition.generateCode(ts));
		code.addLine("beq $a0, $0, ", finallyLabel, "    # WHILE: stop looping if condition is false.");

		code.add(block.generateCode(ts));
		code.addLine("j ", whileLabel, "    # Jump back to loop start.");

		code.addLine(finallyLabel, ":");
		return code.getCode();
	}
}
