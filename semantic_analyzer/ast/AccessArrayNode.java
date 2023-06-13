package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.I32;
import util.Code;
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

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code(indexAccess.generateCode(ts)); // Generate index expression code.
		// Tip: at this point access index is at $a0.
		code.pushToStackFrom("$a0"); // Save access index to stack.
		code.add(variable.generateCode(ts)); // Get the array variable.
		// Tip: at this point array address is at $a0.
		code.popFromStackTo("$t1"); // Save access index to temporal register.
		code.addLine("lw $t2, $a0    # Save array address to temporal register.");
		code.addLine("lw $t3, 0($a0)    # Save array length to temporal register.");

		// TODO validate access isn't out of index.
		code.addLine("li $a1, ", String.valueOf(super.loc.getLine()), "    # Save line in case of division error.");
		code.addLine("li $a2, ", String.valueOf(super.loc.getCol()), "    # Save column in case of division error.");
		code.addLine("slt $a0 $t1 $t3");
		code.addLine("beq $a0, $0, error_array_out_of_bounds # Validate access is in bounds.");

		/// TODO: return value and address.
		// Access class attribute.
		// code.addLine("lw $t2, 8($t1) # Save in $t2 array address"); // 8 = 4*fp +
		// 4*ra
		// code.addLine("lw $a0, " + variable.position() * 4, "($t2) # Save in
		// accumulator the attribute value.");
		// code.addLine("addiu $v0, $t2, " + variable.position() * 4, " # Save in v0 the
		// attribute address.");

		return code.getCode();
	}
}
