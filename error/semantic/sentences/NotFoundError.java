package error.semantic.sentences;

import semantic_analyzer.symbol_table.Location;

public class NotFoundError extends ASTError {
    public NotFoundError(Location location, String message) {
        super(location, message);
    }
}
