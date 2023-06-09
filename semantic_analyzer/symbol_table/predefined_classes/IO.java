package semantic_analyzer.symbol_table.predefined_classes;

import error.semantic.sentences.InternalError;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Char;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.Str;

public class IO extends PredefinedClass {

	public IO() throws InternalError {
		super("IO");
		try {
			super.addMethod("out_str", true);
			super.addArgumentToMethod("out_str", new Str(), "s");
			super.addMethod("out_i32", true);
			super.addArgumentToMethod("out_i32", new I32(), "i");
			super.addMethod("out_bool", true);
			super.addArgumentToMethod("out_bool", new Bool(), "b");
			super.addMethod("out_char", true);
			super.addArgumentToMethod("out_char", new Char(), "c");
			super.addMethod("out_array", true);
			super.addArgumentToMethod("out_array", new Array(), "a");
			super.addMethod("in_str", true, new Str());
			super.addMethod("in_i32", true, new I32());
			super.addMethod("in_bool", true, new Bool());
			super.addMethod("in_Char", true, new Char());
		} catch (Exception e) {
			throw new InternalError("ERROR DECLARANDO LOS METODOS DE LA CLASE IO.");
		}
	}

}
