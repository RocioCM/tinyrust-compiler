package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public abstract class ChainedAccessNode implements Node {
	private String accessedEntity;
	private ChainedAccessNode chainedAccess;
	protected Location loc; // Declaration location.

	public ChainedAccessNode(String accessedEntity, ChainedAccessNode chainedAccess, Location loc) {
		this.accessedEntity = accessedEntity;
		this.chainedAccess = chainedAccess;
		this.loc = loc;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso encadenado");
		json.addAttr("entidad-accedida", accessedEntity);
		json.addAttr("encadenado", chainedAccess);
		return json.toString();
	}

	public String accessedEntity() {
		return accessedEntity;
	}

	public ChainedAccessNode chainedAccess() {
		return chainedAccess;
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		throw new InternalError(loc,
				"SE INVOCO AL METODO validate() DE LA CLASE ChainedAccessNode. SE DEBERIA LLAMAR AL METODO validateAndResolveType PARA ESTA CLASE.");
	};

	abstract protected Type validateAndResolveType(SymbolTable ts, ClassEntry accessedClass) throws ASTError;

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
	}

	public void setChainedAccess(ChainedAccessNode chainedAccess) {
		this.chainedAccess = chainedAccess;
	}
}
