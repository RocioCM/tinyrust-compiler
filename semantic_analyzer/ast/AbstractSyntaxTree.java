package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class AbstractSyntaxTree implements Node {
	private String name;
	private TreeList<ClassNode> classes;

	public AbstractSyntaxTree(String name) {
		this.name = name;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("clases", classes);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (!ts.isConsolidated()) {
			throw new InternalError(new Location(-1, -1),
					"SOLO SE PUEDE VALIDAR EL AST LUEGO DE CONSOLIDAR LA TABLA DE SIMBOLOS.");
		}
		classes.validate(ts);
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code asm = new Code();

		asm.addConstants(); // Init some constants in data segment.

		asm.addLine("");
		asm.addLine(".text");
		asm.addLine(".globl main");

		// Main method execution:
		asm.pushAndUpdateFramePointer();
		asm.addLine("jal main # start ejecution in main function.");
		asm.popFromStackTo("$fp"); // Clear stack.
		asm.exitProgram(); // This will be executed when main method execution is completed.

		asm.add(classes.generateCode(ts));

		// Subroutine cleanup and jump back.
		// This is generic for every method, then it's just written once,
		asm.addLine("");
		asm.addLine("cleanup_method:");
		asm.addLine("lw $fp, 0($fp)    # Jump tothe stack frame's first frame pointer.");
		asm.addLine("addiu $sp, $fp, -4    # Remove vars and arguments from stack.");
		asm.addLine("lw $fp, 0($fp)    # Restore caller frame pointer.");
		asm.addLine("jr $ra    # Jump back to caller's next instruction address after method execution.");

		// Error handling subroutines.
		asm.addLine("");
		asm.addLine("error_zero_division:");
		/// TODO print error with line and column.
		asm.exitProgram();

		return asm.getCode();
	}

	public void addMain(BlockNode block, Location loc) throws InternalError {
		if (classes != null) {
			// Lista de métodos de la clase fantasma main.
			TreeList<MethodNode> methods = new TreeList<MethodNode>(loc);
			methods.add(new MethodNode("main", block, loc)); // Método main.
			classes.addLast(new ClassNode("main", methods, loc)); // Clase fantasma main.
		} else {
			throw new InternalError(loc,
					"SE INTENTO REGISTRAR LA FUNCION MAIN EN EL AST PERO LA LISTA DE CLASES AUN NO SE HA INICIALIZADO.");
		}
	}

	private void addPredefinedClasses() throws InternalError {
		if (classes != null) {
			classes.add(new ClassNode("Object", new TreeList<MethodNode>()));
			classes.add(new ClassNode("IO", new TreeList<MethodNode>(
					/// TODO complete this ASM code from some constants or utils.
					new MethodNode("out_str", new Code().writeOutput(4).getCode()),
					new MethodNode("out_i32", new Code().writeOutput(1).getCode()),
					new MethodNode("out_bool", new Code().writeOutputBool(4).getCode()),
					new MethodNode("out_char", new Code().writeOutput(4).getCode()), ///
					new MethodNode("out_array", new Code().writeOutput(4).getCode()), ///
					new MethodNode("in_str", ""), ///
					new MethodNode("in_i32", new Code().readInputLiteral(5, "msg_in_i32").getCode()),
					new MethodNode("in_bool", ""), ///
					new MethodNode("in_Char", "")))); ///
			classes.add(new ClassNode("I32", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Str", new TreeList<MethodNode>(
					new MethodNode("length", ""), ///
					new MethodNode("concat", ""), ///
					new MethodNode("substr", "")))); ///
			classes.add(new ClassNode("Char", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Bool", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Array", new TreeList<MethodNode>(new MethodNode("length", "")))); ///
		} else {
			throw new InternalError(new Location(-1, -1),
					"SE INTENTO REGISTRAR LAS CLASES PREDEFINIDAS EN EL AST PERO LA LISTA DE CLASES ES NULA.");
		}
	}

	public void setClasses(TreeList<ClassNode> classes) throws InternalError {
		this.classes = classes;
		addPredefinedClasses();
	}
}
