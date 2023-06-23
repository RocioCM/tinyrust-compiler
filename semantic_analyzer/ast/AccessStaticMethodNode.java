package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class AccessStaticMethodNode extends MethodCallNode {
	static private int instancesCounter = 0;
	private int instanceId;

	public AccessStaticMethodNode(String className, MethodCallNode methodCall, Location loc) {
		super(className, methodCall.methodName(), methodCall.arguments(), methodCall.chainedAccess(), loc);
		this.instanceId = instancesCounter + 1;
		instancesCounter++;
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
					"SE INTENTO INVOCAR DE MANERA ESTATICA AL METODO NO ESTATICO " + super.methodName()
							+ " DE LA CLASE "
							+ super.className());
		}

		super.validate(ts); // Validar que la llamada al método sea válida.

	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		String vtLabel = "vtable_" + super.className();
		String mockedCirLabel = "mCir_" + instanceId;
		code.addLine(".data");
		code.addLine(mockedCirLabel, ": .align 2    # Mocked instance of class ",
				super.className(), " for static methods access.");
		code.addLine(".space 4");

		// Tip: it's just necessary to push the VT reference a "self" value to stack as
		// attributes are not accessed in the body of the static method.
		code.addLine(".text");
		code.addLine("la $a0, ", vtLabel, "    # Save VT address to accumulator.");
		code.addLine("sw $a0 ", mockedCirLabel, "($0)    # Save VT address in CIR.");
		code.addLine("la $a0, ", mockedCirLabel, "    # Save mocked CIR address to accumulator.");

		code.pushToStackFrom("$a0"); // Push mocked CIR as "self" reference.
		code.add(super.generateCode(ts));
		code.popFromStackTo("$t2"); // This value is not used, just removed.

		return code.getCode();
	}
}
