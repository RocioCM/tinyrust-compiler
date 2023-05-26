package error.semantic.sentences;

import semantic_analyzer.symbol_table.Location;

public class InternalError extends ASTError {

    public InternalError(Location location, String message) {
        super(location, "ERROR INTERNO: " + message);
    }

}
