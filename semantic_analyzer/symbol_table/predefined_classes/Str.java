package semantic_analyzer.symbol_table.predefined_classes;

import error.semantic.sentences.InternalError;
import semantic_analyzer.types.I32;

public class Str extends PredefinedClass {
	public Str(int position) throws InternalError {
		super("Str", position);
		try {
			super.addMethod("length", false, new I32());
			super.addMethod("concat", false, new semantic_analyzer.types.Str());
			super.addArgumentToMethod("concat", new semantic_analyzer.types.Str(), "s");
			super.addMethod("substr", false, new semantic_analyzer.types.Str());
			super.addArgumentToMethod("substr", new I32(), "i");
			super.addArgumentToMethod("substr", new I32(), "l");
		} catch (Exception e) {
			throw new InternalError("ERROR DECLARANDO LOS METODOS DE LA CLASE Str.");
		}
	}
}
