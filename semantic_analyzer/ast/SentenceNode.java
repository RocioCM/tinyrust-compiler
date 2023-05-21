package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.Location;

public abstract class SentenceNode implements Node {
    protected Location loc; // Declaration location.

    protected SentenceNode(Location loc) {
        this.loc = loc;
    }
}
