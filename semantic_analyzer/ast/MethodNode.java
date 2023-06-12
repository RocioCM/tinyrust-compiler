package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Code;
import util.Json;

public class MethodNode implements Node {
	private String name;
	private BlockNode block; // Es un bloque simple ya que las decl. de variables se guardan en la TS.
	private Boolean mocked = false; // Indicador de si es una clase predefinida y el método es un placeholder.
	private String mockedCode = ""; // Código ASM pre-armado de funciones mock.
	private String label; // Label del método para generación de código.
	private int argsSize; // Cantidad de argumentos para generación de código.
	protected Location loc; // Declaration location.

	public MethodNode(String name, String mockedCode) {
		// Constructor usado para los métodos predefinidos de clases predefinidas.
		this.name = name;
		this.loc = new Location(-1, -1);
		this.block = new BlockNode(new TreeList<SentenceNode>(), loc); // Bloque vacío si no se especifica.
		this.mocked = true; // El metodo no será validado gracias a este flag.
		this.mockedCode = mockedCode;
	}

	public MethodNode(String name, BlockNode block, Location loc) {
		// Constructor usado por los métodos declarados en clases del usuario.
		this.name = name;
		this.block = block;
		this.loc = loc;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("Bloque", block);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		ts.startMethod(name); // Se actualiza el estado de la TS para poder acceder las variables del método.

		if (!mocked) {
			Type expectedReturnType = ts.currentMethod().returnType();
			block.setExpectedReturnType(expectedReturnType);
			block.validate(ts); // Se validan las sentencias del bloque.

			// Se valida el tipo de retorno del bloque. Excepto si es el constructor.
			if (!name.equals("create") && !expectedReturnType.equals(block.resolvedReturnType())) {
				// El bloque no retorna el tipo correcto en todas sus ramas.
				throw new ASTError(loc, "EL METODO " + name + " DEBE RETORNAR UN VALOR DEL TIPO "
						+ expectedReturnType.toJson() + " EN TODAS LAS RAMAS DEL BLOQUE.");
			}
		}

		// Preprocesar datos para la generación de código.
		String className = ts.currentClass().name();
		this.label = className.equals("main") && name.equals("main") ? "main"
				: Code.generateLabel("method", className, name);
		this.argsSize = ts.currentMethod().arguments().size();

		ts.endMethod();
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();

		code.addLine("");
		code.addLine(label + ":");

		if (mocked) {
			code.add(mockedCode);
		} else {
			/// TODO Load variable declarations from TS, using the type's default value.

			code.add(block.generateCode(ts));
			// Add implicit void return if explicit return not present.
			code.addLine("add $a0, $0, $0    # Clean accumulator to return nil.");
		}

		code.addLine("j cleanup_method    # Jump to method cleanup and return to caller.");

		return code.getCode();
	}
}
