package error.semantic.sentences;

import semantic_analyzer.symbol_table.Location;

public class InternalError extends ASTError {

    public InternalError(String message) {
        // TS errors.
        super(new Location(-1, -1), "ERROR INTERNO: " + message);
    }

    public InternalError(Location location, String message) {
        // AST errors.
        super(location, "ERROR INTERNO: " + message);
    }

}
