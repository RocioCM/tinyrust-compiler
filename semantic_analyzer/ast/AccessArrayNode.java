package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.I32;
import util.Json;

public class AccessArrayNode extends AccessNode {
	private AccessVariableNode variable;
	private ExpressionNode indexAccess; // Indice del elemento que se quiere acceder.

	public AccessArrayNode(AccessVariableNode variable, ExpressionNode indexAccess, Location loc) {
		super(loc);
		this.variable = variable;
		this.indexAccess = indexAccess;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso arreglo simple");
		json.addAttr("nombre-variable", variable);
		json.addAttr("expresion-acceso", indexAccess);
		json.addAttr("tipo-resuelto", super.resolveType());
		return json.toString();
	}

	/**
	 * Validar que la variable existe y es de tipo arreglo, que el índice es de tipo
	 * I32 y que el tipo de dato del Array coincide con el tipo de dato esperado
	 * para esta expresión.
	 */
	@Override
	public void validate(SymbolTable ts) throws ASTError {
		// Validar que el índice es de tipo entero.
		indexAccess.setExpectedResolveType(new I32());
		indexAccess.validate(ts);

		// Validar que la variable existe y es de tipo Array.
		variable.setExpectedResolveType(new Array());
		variable.validate(ts);

		// Resolver el tipo de esta expresión como el tipo de los elementos del array.
		Array arrayType = (Array) (variable.resolveType());
		super.setResolveType(arrayType.itemsType());

		super.validateType(ts); // Validar que esta expresión es del tipo esperado para su contexto.

	}
}
