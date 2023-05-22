package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class AccessStaticMethodNode extends MethodCallNode {

	public AccessStaticMethodNode(String className, MethodCallNode methodCall, Location loc) {
		super(className, methodCall.methodName(), methodCall.arguments(), methodCall.chainedAccess(), loc);
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "llamada método estático");
		json.addAttr("clase", super.className());
		json.addAttr("metodo", super.methodName());
		json.addAttr("argumentos", super.arguments());
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		super.validate(ts); // Validar que la llamada al método sea válida.

		// TODO: validar que el método SÍ sea estático.
	}
}
