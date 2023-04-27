package semantic_analyzer.symbol_table;

import java.util.ArrayList;
import java.util.HashMap;

public class TableList<T extends TableElement> extends HashMap<String, T> implements TableElement {
	@Override
	public String toJson() {
		new ArrayList<>();
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}

}
