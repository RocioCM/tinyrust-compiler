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
		json.addAttr("tipo", "llamada metodo estatico");
		json.addAttr("clase", super.className());
		json.addAttr("metodo", super.methodName());
		json.addAttr("argumentos", super.arguments());
		json.addAttr("tipo-resuelto", super.resolveType());
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (methodName().equals("create")) {
			throw new ASTError(loc,
					"SE INTENTO ACCEDER EXPLICITAMENTE AL METODO create DE LA CLASE " + super.className()
							+ ", ESTE METODO ES ACCESIBLE UNICAMENTE A TRAVES DEL CONSTRUCTOR DE LA CLASE.");
		}

		// Validar que el método sea estático.
		if (!super.getMethod(ts).isStatic()) {
			throw new ASTError(loc,
					"SE INTENTO INVOCAR DE MANERA ESTATICA AL METODO NO ESTATICO " + super.methodName() + " DE LA CLASE "
							+ super.className());
		}

		super.validate(ts); // Validar que la llamada al método sea válida.

	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		return super.generateCode(ts);
	}
}
