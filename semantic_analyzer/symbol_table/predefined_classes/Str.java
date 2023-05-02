package semantic_analyzer.symbol_table.predefined_classes;

import semantic_analyzer.symbol_table.types.I32;

public class Str extends PredefinedClass {
	public Str() throws InternalError {
		super("Str");
		try {
			super.addMethod("length", true, new I32());
			super.addMethod("concat", true, new semantic_analyzer.symbol_table.types.Str());
			super.addArgumentToMethod("concat", new semantic_analyzer.symbol_table.types.Str(), "s");
			super.addMethod("substr", true, new semantic_analyzer.symbol_table.types.Str());
			super.addArgumentToMethod("substr", new I32(), "i");
			super.addArgumentToMethod("substr", new I32(), "l");
		} catch (Exception e) {
			throw new InternalError("ERROR DECLARANDO LOS METODOS DE LA CLASE Str.");
		}
	}
}
