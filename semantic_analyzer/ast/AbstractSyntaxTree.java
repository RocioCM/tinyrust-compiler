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
		asm.addLine(".data"); // Mensajes amigables para input.
		asm.addLine("msg_in_str: .asciiz \"Introduzca texto y presione Enter:... \\n\"");
		asm.addLine("msg_in_char: .asciiz \"Introduzca un caracter y presione Enter:... \\n\"");
		asm.addLine("msg_in_int: .asciiz \"Introduzca un entero y presione Enter:... \\n\"");
		asm.addLine("msg_in_bool: .asciiz \"Introduzca true o false y presione Enter:... \\n\"");

		// Generate Virtual Method Tables. The VTs associate methods from classes with
		// its corresponding ASM label.
		generateVirtualTables(ts, asm);

		asm.addLine(".text");
		asm.addLine(".globl main");

		// Create mocked Stack Frame for main method
		asm.pushToStackFrom("$0"); // Push initial frame pointer to stack.
		asm.pushToStackFrom("$0"); // Push initial return address to stack.

		asm.addLine("jal main # start ejecution in main function.");

		// This will be executed when main method execution is completed.
		asm.addLine("li $v0, 10 # 10 is the exit syscall.");
		asm.addLine("syscall # execute the syscall.");

		asm.add(classes.generateCode(ts));

		return asm.getCode();
	}

	private void generateVirtualTables(SymbolTable ts, Code asm) {
		// Iterate over all classes to generate each VT.
		Iterator<ClassNode> classesIter = classes.iterator();
		while (classesIter.hasNext()) {
			String className = classesIter.next().name();
			ClassEntry classTSEntry = ts.getClass(className);

			// Retrieve methods from TS instead of AST, because AST doesn't include
			// inherited methods.
			classTSEntry.methods().values().forEach((method) -> {
				String vtLabel = Code.generateLabel("labelmethod", className, method.name(), "");

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
					String methodLabel = Code.generateLabel("labelmethod", superClassTSEntry.name(), method.name(), "");
					asm.addLine(vtLabel, ": .word ", methodLabel);

				} else {
					// Method isn't inherited, so its label is resolved directly.
					String methodLabel = className.equals("main") && method.name().equals("main") ? "main"
							: Code.generateLabel("method", className, method.name(), "");
					asm.addLine(vtLabel, ": .word ", methodLabel);
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
					/// TODO complete this ASM code from
					/// some constants or utils.
					new MethodNode("out_str", new Code().writeOutput(4, "4($sp)").getCode()),
					new MethodNode("out_i32", new Code().writeOutput(1, "4($sp)").getCode()),
					new MethodNode("out_bool", new Code().writeOutput(1, "4($sp)").getCode()),
					new MethodNode("out_char", new Code().writeOutput(4, "4($sp)").getCode()),
					new MethodNode("out_array", new Code().writeOutput(4, "4($sp)").getCode()),
					new MethodNode("in_str", ""),
					new MethodNode("in_i32", ""),
					new MethodNode("in_bool", ""),
					new MethodNode("in_Char", ""))));
			classes.add(new ClassNode("I32", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Str", new TreeList<MethodNode>(
					new MethodNode("length", ""),
					new MethodNode("concat", ""),
					new MethodNode("substr", ""))));
			classes.add(new ClassNode("Char", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Bool", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Array", new TreeList<MethodNode>(new MethodNode("length", ""))));
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
