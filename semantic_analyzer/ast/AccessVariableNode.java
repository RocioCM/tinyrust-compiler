package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class AccessVariableNode extends AccessNode {
	private String identifier;
	private ChainedAccessNode chainedAccess;
	private Boolean mandatoryChain = false;

	public AccessVariableNode(String identifier, Location loc) {
		super(loc);
		this.identifier = identifier;
		this.chainedAccess = null;
	}

	public AccessVariableNode(String identifier, ChainedAccessNode chainedAccess, Location loc) {
		super(loc);
		this.identifier = identifier;
		this.chainedAccess = chainedAccess;
	}

	public AccessVariableNode(String identifier, ChainedAccessNode chainedAccess, Boolean mandatoryChain, Location loc) {
		super(loc);
		this.identifier = identifier;
		this.chainedAccess = chainedAccess;
		this.mandatoryChain = mandatoryChain;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "variable");
		json.addAttr("nombre-variable", identifier);
		json.addAttr("tipo-resuelto", super.resolveType());
		json.addAttr("encadenado", chainedAccess);
		return json.toString();
	}

	public String identifier() {
		return identifier;
	}

	/**
	 * Valida que la variable con tal nombre exista en el ámbito actual y resuelve
	 * su tipo. Si tiene un encadenado, también lo valida.
	 */
	@Override
	public void validate(SymbolTable ts) throws ASTError {
		Type varType, resolvedType;

		// Obtener la variable del contexto actual de la TS.
		try {
			varType = ts.getVariableType(identifier);
		} catch (error.semantic.declarations.InternalError e) {
			throw new InternalError(loc, e.getMessage());
		}

		// Validar que la variable exista.
		if (varType == null) {
			throw new ASTError(loc,
					"SE INTENTO ACCEDER A LA VARIABLE " + identifier + " PERO NO ESTA DEFINIDA EN EL AMBITO ACTUAL.");
		}

		if (chainedAccess == null && mandatoryChain) {
			// En caso de que sea obligatorio tener al menos un encadenado, pero no haya
			// ninguno. Ejemplo: acceso self simple.
			throw new ASTError(loc,
					"NO SE PERMITE REASIGNAR EL IDENTIFICADOR \"self\", ESTE ES UNA REFERENCIA A LA CLASE ACTUAL.");
		}

		// El acceso a self se trata de forma especial, ya que es una referencia.
		if (identifier.equals("self")) {
			try {
				if (ts.currentMethod().isStatic()) {
					throw new ASTError(loc,
							"NO SE PERMITE ACCEDER A LA REFERENCIA \"self\" DENTRO DE UN METODO ESTATICO.");
				}
			} catch (error.semantic.declarations.InternalError e) {
				throw new InternalError(loc, e.getMessage());
			}
		}

		// TODO: obtener el atributo en ts.getVariableType(identifier); y validar ahí.
		// // Validar que no se accedan atributos privados heredados de otras clases.
		// if (attrEntry.isInherited()) {
		// // Si el atributo es privado, no se puede acceder desde una clase distinta.
		// throw new ASTError(loc, "EL ATRIBUTO " + attrEntry.name() + " DE LA CLASE "
		// + ts.currentClass().name() + " NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES UN
		// ATRIBUTO PRIVADO HEREDADO.");
		// }
		// TODO Validar que no se accede a un atributo (de instancia) en un método
		// estático.
		// try {
		// if (ts.currentMethod().isStatic()) {
		// throw new ASTError(loc,
		// "SE INTENTO ACCEDER AL ATRIBUTO " + identifier
		// + " DENTRO DEL METODO ESTATICO " + ts.currentMethod().name()
		// + ". NO SE PERMITE ACCEDER A ATRIBUTOS DINAMICOS DENTRO DE UN CONTEXTO
		// ESTATICO.");
		// }
		// } catch (error.semantic.declarations.InternalError e) {
		// throw new InternalError(loc, e.getMessage());
		// }

		// Validar que el tipo de la variable es el esperado.
		if (chainedAccess != null) {
			// Recursivo: resolver y validar el encadenado.
			ClassEntry varTypeClass = ts.getClass(varType.type());
			if (varTypeClass == null) {
				// La clase debería existir ya que la TS ya está validada y consolidada.
				throw new InternalError(loc,
						"LA CLASE DE LA VARIABLE" + identifier + " NO EXISTE EN LA TS.");
			}
			resolvedType = chainedAccess.validateAndResolveType(ts, varTypeClass);
		} else {
			// Tope recursivo: devolver el tipo de la variable.
			resolvedType = varType;
		}

		// Validar que el tipo resuelto es el esperado para la variable.
		super.setResolveType(resolvedType);
		super.validateType(ts);
	}
}
