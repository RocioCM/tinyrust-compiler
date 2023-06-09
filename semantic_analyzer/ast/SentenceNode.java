package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;

public abstract class SentenceNode implements Node {
    private Type returnType; // Tipo de dato que la sentencia retornará.
    private Type expectedReturnType; // Tipo de dato que la sentencia debería retornar.
    protected Location loc; // Declaration location.

    protected SentenceNode(Location loc) {
        this.loc = loc;
        this.returnType = null;
        this.expectedReturnType = null;
    }

    @Override
    public String generateCode(SymbolTable ts) throws ASTError {
        return "";
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'generateCode'");
    }

    public Type resolvedReturnType() {
        return returnType;
    }

    public void setResolvedReturnType(Type resolvedReturnType) {
        this.returnType = resolvedReturnType;
    }

    public Type expectedReturnType() {
        return expectedReturnType;
    }

    public void setExpectedReturnType(Type expectedReturnType) {
        this.expectedReturnType = expectedReturnType;
    }
}
