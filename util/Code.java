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
	static public String generateLabel(String prefix, String className, String method) {
		return prefix + "_" + className.replaceAll("_", "__") + "_" + method;
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

	public Code pushAndUpdateFramePointer() {
		pushToStackFrom("$fp"); // Save frame pointer to stack.
		addLine("addiu $fp, $sp, 4    # Set the new frame pointer.");
		return this;
	}

	public Code writeOutput(int syscallId) {
		addLine("lw $a0 4($fp)"); // Retrieve output value from method first argument.
		addLine("li $v0, ", String.valueOf(syscallId), "    # Load output syscall id.");
		addLine("syscall    # Print out value.");
		/// TODO: add breakline after each print. Remove the hardcoded new lines from
		/// constants.
		return this;
	}

	public Code writeOutputBool(int syscallId) {
		popFromStackTo("$a0"); // Retrieve output value from stack.
		addLine("beq $a0, $0, set_bool_false     # Save true string address in a0.");
		addLine("la $a0, bool_true    # Save true string address in a0.");
		addLine("j output_bool    # Jump to print instruction.");

		addLine("set_bool_false: ");
		addLine("la $a0, bool_false    # Save false string address in a0.");

		addLine("output_bool: ");
		addLine("li $v0, ", String.valueOf(syscallId), "    # Load output syscall id.");
		addLine("syscall    # Print out value.");
		return this;
	}

	public Code readInputLiteral(int syscallId, String msgLabel) {
		// Show user friendly input message.
		addLine("la $a0 ", msgLabel, "   # Save string addr in output register.");
		addLine("li $v0, 4    # Load output string syscall id.");
		addLine("syscall    # Print out value.");

		// Expect input value.
		addLine("li $v0, ", String.valueOf(syscallId), "   # Load input syscall id.");
		addLine("syscall    # Get input value.");
		addLine("la $t1, temp_i32    # Reserve aligned space for input value.");
		addLine("sw $v0, 0($t1)    # Save input value to data segment.");
		addLine("lw $a0, 0($t1)    # Save input value to accumulator.");

		return this;
	}

	public Code readInputString(int syscallId, String msgLabel) {
		// Show user friendly input message.
		addLine("la $a0 ", msgLabel, "   # Save string addr in output register.");
		addLine("li $v0, 4    # Load output string syscall id.");
		addLine("syscall    # Print out value.");

		// Expect input value.
		addLine("li $v0, ", String.valueOf(syscallId), "   # Load input syscall id.");
		addLine("syscall    # Get input value.");
		addLine("la $t1, temp_i32    # Reserve aligned space for input value.");
		addLine("sw $v0, 0($t1)    # Save input value to data segment.");
		addLine("lw $a0, 0($t1)    # Save input value to accumulator.");

		return this;
	}

	/**
	 * Agrega al segmento de datos un grupo de constantes que serán utilizadas
	 * durante la ejecución.
	 */
	public Code addConstants() {
		addLine(".data");
		// User friendly messages for input
		addLine("msg_in_str: .asciiz \"Introduzca texto y presione Enter:... \\n\"");
		addLine("msg_in_char: .asciiz \"Introduzca un caracter y presione Enter:... \\n\"");
		addLine("msg_in_i32: .asciiz \"Introduzca un entero y presione Enter:... \\n\"");
		addLine("msg_in_bool: .asciiz \"Introduzca true o false y presione Enter:... \\n\"");
		// Error messages
		addLine("msg_div_error: .asciiz \"Error de ejecucion: division por cero. \\n\"");
		// Boolean values labels
		addLine("bool_true: .asciiz \"true\\n\"");
		addLine("bool_false: .asciiz \"false\\n\"");
		// Default values
		addLine("empty_str: .asciiz \"\"");
		// Reserved spaces
		addLine("temp_i32: .word 0");
		addLine(".text    # End data segment.");

		return this;
	}

	public Code exitProgram() {
		addLine("li $v0, 10 # 10 is the exit syscall.");
		addLine("syscall # execute the syscall.");
		return this;
	}

	public String getCode() {
		return code;
	}
}
