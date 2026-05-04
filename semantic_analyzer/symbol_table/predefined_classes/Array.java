package semantic_analyzer.symbol_table.predefined_classes;

import error.semantic.sentences.InternalError;
import semantic_analyzer.types.I32;

public class Array extends PredefinedClass {
	public Array(int position) throws InternalError {
		super("Array", position);
		try {
			super.addMethod("length", false, new I32());
		} catch (Exception e) {
			throw new InternalError("ERROR DECLARANDO LOS METODOS DE LA CLASE Array.");
		}
	}
}
