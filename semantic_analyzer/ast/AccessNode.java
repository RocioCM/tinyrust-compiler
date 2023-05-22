package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.Location;

public abstract class AccessNode extends ExpressionNode {
    public AccessNode(Location loc) {
        super(loc);
    }
}
