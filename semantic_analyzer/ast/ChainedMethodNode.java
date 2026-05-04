package semantic_analyzer.ast;

import java.util.HashMap;
import java.util.Iterator;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.NotFoundError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.MethodEntry;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import semantic_analyzer.types.Void;
import util.Code;
import util.Json;

public class ChainedMethodNode extends ChainedAccessNode {
	private TreeList<ExpressionNode> arguments;
	private int position;

	public ChainedMethodNode(String accessedEntity, TreeList<ExpressionNode> arguments,
			ChainedAccessNode chainedAccess, Location loc) {
		super(accessedEntity, chainedAccess, loc);
		this.arguments = arguments;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso metodo encadenado");
		json.addAttr("nombre-metodo", super.accessedEntity());
		json.addAttr("argumentos", arguments);
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}

	@Override
	protected Type validateAndResolveType(SymbolTable ts, ClassEntry accessedClass) throws ASTError {
		Type returnType;
		// Obtener la entrada de la TS del método accedido.
		MethodEntry methodEntry = accessedClass.methods().get(super.accessedEntity());

		if (methodEntry == null) {
			throw new NotFoundError(loc, "SE INTENTO ACCEDER AL METODO " + super.accessedEntity() + " DE LA CLASE "
					+ accessedClass.name() + ", PERO LA CLASE NO IMPLEMENTA TAL METODO.");
		}

		validateArgs(ts, methodEntry); // Validar argumentos del método.

		if (super.chainedAccess() != null) {
			// Recursivo: resolver y validar el encadenado si existe.

			// Si el método retorna void, entonces no se puede encadenar nada.
			if (methodEntry.returnType().equals(new Void())) {
				throw new ASTError(loc, "EL METODO " + super.accessedEntity()
						+ " NO RETORNA NINGUN VALOR, NO SE PUEDE ACCEDER A ENCADENADOS DE void.");
			}

			ClassEntry returnTypeClass = ts.getClass(methodEntry.returnType().type());
			if (returnTypeClass == null) {
				// La clase debería existir ya que la TS ya está validada y consolidada.
				throw new InternalError(loc,
						"LA CLASE DE RETORNO DEL METODO " + super.accessedEntity() + " DE LA CLASE "
								+ accessedClass.name() + " NO EXISTE EN LA TS.");
			}

			// Llamado recursivo.
			returnType = super.chainedAccess().validateAndResolveType(ts, returnTypeClass);

		} else {
			// Tope recursivo: devolver el tipo de retorno del método.
			returnType = methodEntry.returnType();
		}

		// Guardar datos para la generación de código.
		this.position = methodEntry.position();

		return returnType;
	}

	private void validateArgs(SymbolTable ts, MethodEntry methodEntry) throws ASTError {
		// 1. Armar una estructura práctica para validar los argumentos.
		HashMap<Number, Type> formalArgsMap = new HashMap<Number, Type>(); // Argumentos formales del método.
		methodEntry.arguments().values().forEach((arg) -> {
			// Armar map de posiciones a tipos de argumento.
			formalArgsMap.put(arg.position(), arg.type());
		});

		// 2. Validar que la cantidad de argumentos coincida.
		if (arguments.size() != formalArgsMap.size()) {
			throw new ASTError(loc, "LA CANTIDAD DE ARGUMENTOS PARA EL METODO " + methodEntry.name()
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

	public TreeList<ExpressionNode> arguments() {
		return arguments;
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		// 1. Self està en a0, pushearla al stack.
		code.pushToStackFrom("$a0"); // Push self reference to Stack (CIR addr).

		code.pushToStackFrom("$ra"); // Save the caller return address to stack.
		code.pushAndUpdateFramePointer();

		code.addLine("lw $fp 4($sp) # Restore caller frame pointer in register, during arguments evaluation.");
		// Push arguments to the stack in inverse order.
		for (int i = arguments.size() - 1; i >= 0; i--) {
			code.add(arguments.get(i).generateCode(ts));
			code.pushToStackFrom("$a0");
		}
		code.addLine("addiu $fp, $sp, " + 4 * (arguments.size() + 1),
				"    # Restore the latest frame pointer value to register, after arguments evaluation.");

		// Call method.
		// Access class VT using CIR reference.
		code.addLine("lw $t1, 8($fp)    # Save in $t1 the current class CIR address"); // 8 = 4*fp + 4*ra
		code.addLine("lw $t2, 0($t1)    # Save in $t2 the object VT address");
		code.addLine("lw $a0, " + position * 4,
				"($t2)    # Save in accumulator the method label address");
		code.addLine("jalr $a0    # Jump to method code.");

		// Tip: at this point, return value is already stored in the accumulator.

		// Restore registers after method call returns.
		code.popFromStackTo("$fp"); // Restore caller frame pointer from stack.
		code.popFromStackTo("$ra"); // Restore caller return address from stack.
		code.popFromStackTo("$t2"); // This value isn't used, just popped.

		return code.getCode();
	}
}
