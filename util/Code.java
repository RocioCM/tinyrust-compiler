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
	public void add(String lines) {
		this.code += lines;
	}

	/** Agrega una nueva linea de código ASM. */
	public void addLine(String... strings) {
		String line = String.join("", strings);
		this.code = code + line + "\n";
	}

	/**
	 * Agrega el código asm necesario para guardar en la pila el dato almacenado en
	 * un registro dado.
	 * 
	 * @param reg - Nombre del registro donde está el dato.
	 */
	public void pushToStackFrom(String reg) {
		addLine("sw " + reg + " 0($sp)   # add data to stack top.");
		addLine("addiu $sp $sp -4)   # move stack pointer down.");
	}

	/**
	 * Agrega el código asm necesario para mover el dato en el tope de la pila a un
	 * registro dado.
	 * 
	 * @param reg - Nombre del registro donde guardar el dato.
	 */
	public void popFromStackTo(String reg) {
		addLine("lw " + reg + " 4($sp)   # retrieve data from stack top.");
		addLine("addiu $sp $sp 4)   # move stack pointer up.");
	}

	public String getCode() {
		return code;
	}
}
