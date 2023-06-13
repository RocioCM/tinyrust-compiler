package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class AccessMethodNode extends MethodCallNode {

    public AccessMethodNode(String methodName, TreeList<ExpressionNode> arguments,
            ChainedAccessNode chainedAccess, Location loc) {
        super(null, methodName, arguments, chainedAccess, loc);
    }

    @Override
    public String toJson() {
        Json json = new Json();
        json.addAttr("tipo", "llamada metodo");
        json.addAttr("clase", "self");
        json.addAttr("metodo", super.methodName());
        json.addAttr("argumentos", super.arguments());
        json.addAttr("tipo-resuelto", super.resolveType());
        json.addAttr("encadenado", super.chainedAccess());
        return json.toString();
    }

    @Override
    public void validate(SymbolTable ts) throws ASTError {
        if (super.methodName().equals("create")) {
            throw new ASTError(loc,
                    "SE INTENTO ACCEDER EXPLICITAMENTE AL METODO create DE LA CLASE ACTUAL, ESTE METODO ES ACCESIBLE UNICAMENTE A TRAVES DEL CONSTRUCTOR DE LA CLASE.");
        }

        // Validar que un método NO estático no sea invocado dentro de un método
        // estático.
        if (ts.currentMethod().isStatic() && !super.getMethod(ts).isStatic()) {
            throw new ASTError(loc,
                    "SE INTENTO ACCEDER AL METODO NO ESTATICO " + super.methodName()
                            + " DENTRO DEL METODO ESTATICO " + ts.currentMethod().name()
                            + ". NO SE PERMITE ACCEDER A METODOS DINAMICOS DENTRO DE UN CONTEXTO ESTATICO.");
        }

        super.validate(ts); // Validar que la llamada al método sea válida.
    }

    @Override
    public String generateCode(SymbolTable ts) throws ASTError {
        Code code = new Code();

        // Get self reference.
        // Tip: the self reference for the method is the same as the caller's self.
        code.addLine("lw $t1, 0($fp)    # Save in $t1 the upper frame pointer address.");
        code.addLine("lw $a0, 8($t1)    # Save in $t2 the current class CIR address"); // 8 = 4*fp + 4*ra
        code.pushToStackFrom("$a0");

        code.add(super.generateCode(ts));

        code.popFromStackTo("$t2"); // Self value is not used anymore, just pop it to random register.
        // Tip: by this point, the result is already stored at $a0.

        return code.getCode();
    }
}
