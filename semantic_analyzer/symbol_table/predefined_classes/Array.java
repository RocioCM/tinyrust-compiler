package semantic_analyzer.symbol_table.predefined_classes;

import semantic_analyzer.types.I32;
import error.semantic.InternalError;

public class Array extends PredefinedClass {
	public Array() throws InternalError {
		super("Array");
		try {
			super.addMethod("length", false, new I32());
		} catch (Exception e) {
			throw new InternalError("ERROR DECLARANDO LOS METODOS DE LA CLASE Array.");
		}
	}
}
