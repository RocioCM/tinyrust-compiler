package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class AccessMethodNode extends MethodCallNode {

    public AccessMethodNode(String methodName, TreeList<ExpressionNode> arguments,
            ChainedAccessNode chainedAccess, Location loc) {
        super(null, methodName, arguments, chainedAccess, loc);
    }

    @Override
    public String toJson() {
        Json json = new Json();
        json.addAttr("tipo", "llamada método");
        json.addAttr("clase", "self");
        json.addAttr("metodo", super.methodName());
        json.addAttr("argumentos", super.arguments());
        json.addAttr("encadenado", super.chainedAccess());
        return json.toString();
    }

    @Override
    public void validate(SymbolTable ts) throws ASTError {
        if (methodName() == "create") {
            throw new ASTError(loc,
                    "SE INTENTO ACCEDER EXPLICITAMENTE AL METODO create DE LA CLASE ACTUAL, ESTE METODO ES ACCESIBLE UNICAMENTE A TRAVES DEL CONSTRUCTOR DE LA CLASE.");
        }

        super.validate(ts); // Validar que la llamada al método create sea válida.

        // TODO: validar que el método no sea estático.
    }
}
