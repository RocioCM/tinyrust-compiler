package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;

import java.util.Iterator;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.MethodEntry;
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

		generateVirtualTables(ts, asm); // Save static VTs in data segment.
		asm.addConstants(); // Init some constants in data segment.

		asm.addLine("");
		asm.addLine(".text");
		asm.addLine(".globl main");
		asm.addLine("jal main # start ejecution in main function.");
		asm.exitProgram(); // This will be executed when main method execution is completed.

		asm.add(classes.generateCode(ts));

		// Subroutine cleanup and jump back.
		// This is generic for every method, then it's just written once,
		asm.addLine("");
		asm.addLine("cleanup_method:");
		asm.addLine("la $sp, 0($fp)    # Remove arguments and variables from stack.");
		asm.addLine("jr $ra    # Jump back to caller's next instruction address after method execution.");

		// Error handling subroutines.
		asm.addLine("");
		asm.addLine("error_zero_division:");
		/// TODO print error with line and column.
		asm.exitProgram();

		return asm.getCode();
	}

	private void generateVirtualTables(SymbolTable ts, Code asm) {
		asm.addLine(".data");

		// Iterate over all classes to generate each VT.
		Iterator<ClassNode> classesIter = classes.iterator();
		while (classesIter.hasNext()) {
			String className = classesIter.next().name();
			ClassEntry classTSEntry = ts.getClass(className);

			asm.addLine("vtable_" + className + ":"); // Declare class VTable.
			// Retrieve methods from TS instead of AST, because AST doesn't include
			// inherited methods.
			classTSEntry.methods().values().forEach((method) -> {
				if (method.isInherited()) {
					// Method is inherited, so get the super-method label "recursively".
					ClassEntry superClassTSEntry = ts.getClass(classTSEntry.extendsFrom());
					MethodEntry superMethod = superClassTSEntry.methods().get(method.name());
					while (superMethod.isInherited()) {
						// Go up the classes tree until the method implementation.
						superClassTSEntry = ts.getClass(superClassTSEntry.extendsFrom());
						superMethod = superClassTSEntry.methods().get(method.name());
					}

					// Found method implementation, then generate label on that super-class.
					String methodLabel = Code.generateLabel("method", superClassTSEntry.name(), method.name());
					asm.addLine(".word ", methodLabel);

				} else {
					// Method isn't inherited, so its label is resolved directly.
					String methodLabel = className.equals("main") && method.name().equals("main") ? "main"
							: Code.generateLabel("method", className, method.name());
					asm.addLine(".word ", methodLabel);
				}
			});
		}
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
