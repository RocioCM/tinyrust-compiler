package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class ChainedExpressionNode extends ExpressionNode {
    private String role; // Rol de la expresión encadenada en el código.
    private ExpressionNode expression;
    private ChainedAccessNode chainedAccess;

    public ChainedExpressionNode(String role, ExpressionNode expression, ChainedAccessNode chainedAccess) {
        super(expression.loc);
        this.role = role;
        this.expression = expression;
        this.chainedAccess = chainedAccess;
    }

    @Override
    public String toJson() {
        Json json = new Json();
        json.addAttr("tipo", role);
        json.addAttr("expresion", expression);
        json.addAttr("encadenado", chainedAccess);
        return json.toString();
    }

    /**
     * Valida que la variable con tal nombre exista en el ámbito actual y resuelve
     * su tipo. Si tiene un encadenado, también lo valida.
     */
    @Override
    public void validate(SymbolTable ts) throws ASTError {
        Type resolvedType;

        expression.validate(ts); // Valida la expresión y resuelve su tipo.

        // Resolver el tipo de la expresión parentizada.
        if (chainedAccess != null) {
            // Resolver y validar el encadenado.
            ClassEntry expTypeClass = ts.getClass(expression.resolveType().type());
            if (expTypeClass == null) {
                // La clase debería existir ya que la TS ya está validada y consolidada.
                throw new InternalError(loc,
                        "LA CLASE DEL TIPO" + expression.resolveType().type() + " NO EXISTE EN LA TS.");
            }
            resolvedType = chainedAccess.validateAndResolveType(ts, expTypeClass);
        } else {
            // Si no hay encadenado, devolver el tipo de la expresión.
            resolvedType = expression.resolveType();
        }

        // Validar que el tipo resuelto es el esperado para el parentizado.
        super.setResolveType(resolvedType);
        super.validateType(ts);
    }

    @Override
    public String generateCode(SymbolTable ts) throws ASTError {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
}
