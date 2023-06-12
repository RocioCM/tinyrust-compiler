package semantic_analyzer.ast;

import java.util.HashMap;
import java.util.Iterator;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.NotFoundError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.MethodEntry;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import semantic_analyzer.types.Void;
import util.Code;

public abstract class MethodCallNode extends ExpressionNode {
    private String className; // Si es null indica que debe accederse a la clase actual.
    private String methodName;
    private TreeList<ExpressionNode> arguments;
    private ChainedAccessNode chainedAccess;

    public MethodCallNode(String className, String methodName, TreeList<ExpressionNode> arguments,
            ChainedAccessNode chainedAccess, Location loc) {
        super(loc);
        this.className = className;
        this.methodName = methodName;
        this.arguments = arguments;
        this.chainedAccess = chainedAccess;
    }

    abstract public String toJson();

    public MethodEntry getMethod(SymbolTable ts) throws ASTError {
        ClassEntry classEntry;
        MethodEntry methodEntry;

        // Obtener la entrada de la TS de la clase accedida.
        if (className == null) {
            classEntry = ts.currentClass();
            className = classEntry.name();
        } else {
            classEntry = ts.getClass(className);
            if (classEntry == null) {
                throw new NotFoundError(loc, "SE INTENTO ACCEDER A LA CLASE NO DECLARADA " + className);
            }
        }

        // Obtener la entrada de la TS del método accedido.
        if (methodName.equals("create")) {
            methodEntry = classEntry.constructor();
        } else {
            methodEntry = classEntry.methods().get(methodName);
        }

        // Validar que el método existe en la clase.
        if (methodEntry == null) {
            throw new NotFoundError(loc, "SE INTENTO ACCEDER AL METODO " + methodName + " DE LA CLASE "
                    + className + ", PERO LA CLASE NO IMPLEMENTA TAL METODO.");
        }

        return methodEntry;
    }

    /**
     * Validar que el método existe en la clase actual, que los argumentos son
     * válidos para el método y que el tipo de retorno de esta expresión el esperado
     * para su contexto.
     */
    public void validate(SymbolTable ts) throws ASTError {
        Type resolveType;

        MethodEntry methodEntry = getMethod(ts);

        // Validar argumentos del método.
        validateArgs(ts, methodEntry);

        // Validar el encadenado si existe.
        if (chainedAccess != null) {
            // Recursivo: resolver y validar el encadenado.

            // Si el método retorna void, entonces no se puede encadenar nada.
            if (methodEntry.returnType().equals(new Void())) {
                throw new ASTError(loc, "EL METODO " + methodName
                        + " NO RETORNA NINGUN VALOR, NO SE PUEDE ACCEDER A ENCADENADOS DE void.");
            }

            // Buscar la clase en la TS para validar el encadenado.
            ClassEntry returnTypeClass = ts.getClass(methodEntry.returnType().type());
            if (returnTypeClass == null) {
                // La clase debería existir ya que la TS ya está validada y consolidada.
                throw new InternalError(loc,
                        "LA CLASE DE RETORNO DEL METODO " + methodName + " DE LA CLASE "
                                + className + " NO EXISTE EN LA TS.");
            }

            // Llamado recursivo.
            resolveType = chainedAccess.validateAndResolveType(ts, returnTypeClass);

        } else {
            // Si no hay encadenado, devolver el tipo de retorno del método.
            resolveType = methodEntry.returnType();
        }

        super.setResolveType(resolveType);
        super.validateType(ts);
    }

    /**
     * Valida que los argumentos del método coincidan con la firma del método.
     * Es decir, que la cantidad, tipo y orden de los argumentos sea correcta.
     */
    private void validateArgs(SymbolTable ts, MethodEntry methodEntry) throws ASTError {
        // 1. Armar una estructura práctica para validar los argumentos.
        HashMap<Number, Type> formalArgsMap = new HashMap<Number, Type>(); // Argumentos formales del método.
        methodEntry.arguments().values().forEach((arg) -> {
            // Armar map de posiciones a tipos de argumento.
            formalArgsMap.put(arg.position(), arg.type());
        });

        // 2. Validar que la cantidad de argumentos coincida.
        if (arguments.size() != formalArgsMap.size()) {
            throw new ASTError(loc, "LA CANTIDAD DE ARGUMENTOS PARA EL METODO " + methodName
                    + " NO ES CORRECTA. SE ESPERABAN " + formalArgsMap.size() + " ARGUMENTOS.");
        }

        // 3. Validar que el tipo de cada argumento coincida.
        Iterator<ExpressionNode> argsIterator = arguments.iterator();
        for (int i = 0; i < formalArgsMap.size(); i++) {
            Type argFormalType = formalArgsMap.get(i + 1);
            argsIterator.next().setExpectedResolveType(argFormalType);
        }
        arguments.validate(ts); // Validará que cada expresión tenga el tipo esperado.
    }

    @Override
    public String generateCode(SymbolTable ts) throws ASTError {
        Code code = new Code();

        /// TODO: the self reference must be pushed to stack before this so we can
        /// access its attrs in the method.

        // Save caller method state to stack.
        code.pushToStackFrom("$fp"); // Save the caller frame pointer to stack.
        code.pushToStackFrom("$ra"); // Save the caller return address to stack.
        code.addLine("la $fp, 0($sp)    # Set the new frame pointer.");

        // Push arguments to the stack in inverse order.
        for (int i = arguments.size() - 1; i >= 0; i--) {
            code.add(arguments.get(i).generateCode(ts));
            code.pushToStackFrom("$a0");
        }

        // Call method.
        code.addLine("jal " + Code.generateLabel("method", className, methodName) + "    # Jump to method code.");

        // Restore registers after method call returns.
        code.popFromStackTo("$ra"); // Restore caller return address from stack.
        code.popFromStackTo("$fp"); // Restore caller frame pointer from stack.

        // Tip: at this point, when returning from the method,
        // the return value is at $a0.
        /// TODO: resolve chained access. Generate its code.

        return code.getCode();
    }

    public String className() {
        return className;
    }

    public String methodName() {
        return methodName;
    }

    public TreeList<ExpressionNode> arguments() {
        return arguments;
    }

    public ChainedAccessNode chainedAccess() {
        return chainedAccess;
    }

    public void setChainedAccess(ChainedAccessNode chainedAccess) {
        this.chainedAccess = chainedAccess;
    }
}
