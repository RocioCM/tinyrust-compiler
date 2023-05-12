package semantic_analyzer.symbol_table.predefined_classes;

import java.util.HashMap;

import error.semantic.DuplicatedEntityIdError;
import error.semantic.IllegalSelfDeclarationError;
import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.MethodEntry;
import semantic_analyzer.symbol_table.TableList;
import semantic_analyzer.types.Type;

public abstract class PredefinedClass extends ClassEntry {
	public PredefinedClass(String name) {
		super(name, false, null, new Location(-1, -1));
	}

	protected void addMethod(String name, boolean isStatic) throws SemanticalError {
		super.addMethod(name, isStatic, null);
	}

	protected void addMethod(String name, boolean isStatic, Type returnType) throws SemanticalError {
		MethodEntry method = super.addMethod(name, isStatic, new Location(-1, -1));
		method.setReturnType(returnType);
	}

	protected void addArgumentToMethod(String methodName, Type argType, String argName)
			throws IllegalSelfDeclarationError, DuplicatedEntityIdError {
		MethodEntry method = super.methods().get(methodName);
		if (method != null) {
			method.addArgument(argName, argType, new Location(-1, -1));
		}
	}

	@Override
	public void consolidate(TableList<ClassEntry> classes, HashMap<String, ClassEntry> classesTree) {
		// Tope recursivo de consolidación. Todas las clases predefinidas se consideran
		// ya consolidadas, dado que heredan de Object o es la clase Object en si misma.
		super.setConsolidated(true);
		return;
	}
}
