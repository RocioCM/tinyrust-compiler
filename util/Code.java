package util;

public class Code {
	private String code;

	public Code() {
		this.code = "";
	}

	public Code(String code) {
		this.code = code;
	}

	/** Agrega un bloque de múltiples lineas de código ASM. */
	public Code add(String lines) {
		this.code += lines;
		return this;
	}

	/** Agrega una nueva linea de código ASM. */
	public Code addLine(String... strings) {
		String line = String.join("", strings);
		this.code = code + line + "\n";
		return this;
	}

	/** Genera un label único para un método o variable. */
	static public String generateLabel(String prefix, String className, String method, String postfix) {
		return prefix + "_" + className.replaceAll("_", "__") + "_" + method.replaceAll("_", "__") + "_" + postfix;
	}

	/**
	 * Agrega el código asm necesario para guardar en la pila el dato almacenado en
	 * un registro dado.
	 * 
	 * @param reg - Nombre del registro donde está el dato.
	 */
	public Code pushToStackFrom(String reg) {
		addLine("sw " + reg + " 0($sp)   # add data to stack top.");
		addLine("addiu $sp $sp -4   # move stack pointer down.");
		return this;

	}

	/**
	 * Agrega el código asm necesario para mover el dato en el tope de la pila a un
	 * registro dado.
	 * 
	 * @param reg - Nombre del registro donde guardar el dato.
	 */
	public Code popFromStackTo(String reg) {
		addLine("lw " + reg + " 4($sp)   # retrieve data from stack top.");
		addLine("addiu $sp $sp 4   # move stack pointer up.");
		return this;

	}

	public Code writeOutput(int syscallId, String label) {
		addLine("li $v0, ", String.valueOf(syscallId), "    # Load output syscall id.");
		addLine("la $a0, ", label, "    # Load out value.");
		addLine("syscall    # Print out value.");
		return this;

	}

	public Code readInput(int syscallId) {
		writeOutput(4, "msg_in_4");

		addLine("li $v0, ", String.valueOf(syscallId), "   # Load input syscall id.");
		addLine("syscall    # Get input value.");
		addLine("sw $v0, $a0   # Save input value to accumulator.");
		return this;

	}

	public String getCode() {
		return code;
	}
}
