package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.AttributeEntry;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.VariableEntry;
import semantic_analyzer.types.ClassType;
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

		// 1. Obtener el tipo de la variable.
		if (identifier.equals("self")) {
			// El acceso a self se trata de forma especial,
			// ya que es una referencia no una variable.
			try {
				varType = new ClassType(ts.currentClass().name());

				// Validar que no se acceda self en el método main.
				if (ts.currentClass().name().equals("main")) {
					throw new ASTError(loc,
							"SE INTENTO ACCEDER A LA VARIABLE " + identifier + " PERO NO ESTA DEFINIDA EN EL AMBITO ACTUAL.");
				}

				// Validar que no se acceda self en un contexto estático.
				if (ts.currentMethod().isStatic()) {
					throw new ASTError(loc,
							"NO SE PERMITE ACCEDER A LA REFERENCIA \"self\" DENTRO DE UN METODO ESTATICO.");
				}
			} catch (error.semantic.declarations.InternalError e) {
				throw new InternalError(loc, e.getMessage());
			}

		} else {
			// Si es una variable normal, se obtiene del contexto actual de la TS.
			try {
				VariableEntry var = ts.getVariable(identifier);

				// Validar que la variable exista.
				if (var == null) {
					throw new ASTError(loc,
							"SE INTENTO ACCEDER A LA VARIABLE " + identifier + " PERO NO ESTA DEFINIDA EN EL AMBITO ACTUAL.");
				}

				if (var instanceof AttributeEntry) {
					// Validar que no se accede a un atributo (de instancia) en un método estático.
					if (ts.currentMethod().isStatic()) {
						throw new ASTError(loc,
								"SE INTENTO ACCEDER AL ATRIBUTO " + identifier
										+ " DENTRO DEL METODO ESTATICO " + ts.currentMethod().name()
										+ ". NO SE PERMITE ACCEDER A ATRIBUTOS DINAMICOS DENTRO DE UN CONTEXTO ESTATICO.");
					}

					// Validar que no se accedan atributos privados heredados de otras clases.
					AttributeEntry attr = (AttributeEntry) (var);
					if (attr.isInherited() && !attr.isPublic()) {
						throw new ASTError(loc, "EL ATRIBUTO " + var.name() + " DE LA CLASE "
								+ ts.currentClass().name() + " NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES UN ATRIBUTO PRIVADO HEREDADO.");
					}
				}

				varType = var.type();

			} catch (error.semantic.declarations.InternalError e) {
				throw new InternalError(loc, e.getMessage());
			}
		}

		if (chainedAccess == null && mandatoryChain) {
			// En caso de que sea obligatorio tener al menos un encadenado, pero no haya
			// ninguno. Ejemplo: acceso self simple.
			throw new ASTError(loc,
					"NO SE PERMITE REASIGNAR EL IDENTIFICADOR \"self\", ESTE ES UNA REFERENCIA A LA CLASE ACTUAL.");
		}

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
