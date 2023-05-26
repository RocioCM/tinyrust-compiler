package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.PrimitiveType;
import util.Json;

public class ConstructorArrayCallNode extends ExpressionNode {
	private PrimitiveType<?> elementsType;
	private ExpressionNode size;

	public ConstructorArrayCallNode(PrimitiveType<?> elementsType, ExpressionNode size, Location loc) {
		super(new Array(elementsType), loc);
		this.elementsType = elementsType;
		this.size = size;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "llamada constructor arreglo");
		json.addAttr("tipo-dato", elementsType);
		json.addAttr("tamaño", size);
		json.addAttr("tipo-resuelto", super.resolveType());
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		// Validar que el tamaño es de tipo I32.
		size.setExpectedResolveType(new I32());
		size.validate(ts);

		// Validar que esta expresión es del tipo esperado.
		super.validateType(ts);
	}
}
