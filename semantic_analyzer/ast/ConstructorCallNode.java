package semantic_analyzer.ast;

import java.util.HashMap;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.AttributeEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class ConstructorCallNode extends MethodCallNode {
	static private int instancesCounter = 0;
	private int id;

	public ConstructorCallNode(String className, TreeList<ExpressionNode> arguments,
			ChainedAccessNode chainedAccess, Location loc) {
		super(className, "create", arguments, chainedAccess, loc);
		this.id = instancesCounter + 1;
		instancesCounter++;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "llamada constructor");
		json.addAttr("clase", super.className());
		json.addAttr("argumentos", super.arguments());
		json.addAttr("tipo-resuelto", super.resolveType());
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		super.validate(ts); // Validar que la llamada al método create sea válida.
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		String cirLabel = "cir_" + id;
		String vtLabel = "vtable_" + super.className();
		HashMap<String, AttributeEntry> attributes = ts.getClass(super.className()).attributes();

		code.addLine(".data");
		code.addLine(cirLabel, ": .space " + (attributes.size() + 1) * 4, "    # Instance of class ", super.className());

		code.addLine(".text");
		code.addLine("la $a0, ", vtLabel, "    # Save VT address to accumulator.");
		code.addLine("sw $a0 ", cirLabel, "($0)    # Save VT address in CIR.");
		code.addLine("la $a0, ", cirLabel, "    # Save CIR address to accumulator.");

		// Initialize value for each field.
		attributes.values().forEach((attr) -> {
			/// TODO Use the type's default value for str and char.
			/// Init each field with its default value.
			/// Defaults: bool 0, int 0, Object nil, Array nil, char ??, str "".

			switch (attr.type().type()) {
				case "Str":
					break;

				case "Char":
					break;

				default:
					break;
			}
		});

		// Invoke class constructor.
		code.pushToStackFrom("$a0"); // Push self for constructor.
		code.add(super.generateCode(ts)); // Invoke class constructor method.
		code.popFromStackTo("$a0"); // Save the instance reference in a0 register for return.

		return code.getCode();
	}
}
