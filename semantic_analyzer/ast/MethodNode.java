package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class MethodNode implements Node {
	private String name;
	private BlockNode block; // Es un bloque simple ya que las decl. de variables se guardan en la TS.
	private Boolean mocked = false; // Indicador de si es una clase predefinida y el método es un placeholder.
	protected Location loc; // Declaration location.

	public MethodNode(String name) {
		// Constructor usado para los métodos predefinidos de clases predefinidas.
		this.name = name;
		this.loc = new Location(-1, -1);
		this.block = new BlockNode(new TreeList<SentenceNode>(), loc); // Bloque vacío si no se especifica.
		this.mocked = true; // El metodo no será validado gracias a este flag.
	}

	public MethodNode(String name, BlockNode block, Location loc) {
		// Constructor usado por los métodos declarados en clases del usuario.
		this.name = name;
		this.block = block;
		this.loc = loc;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("Bloque", block);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		try {
			if (!mocked) {
				ts.startMethod(name); // Se actualiza el estado de la TS para poder acceder las variables del método.

				Type expectedReturnType = ts.currentMethod().returnType();
				block.setExpectedReturnType(expectedReturnType);
				block.validate(ts); // Se validan las sentencias del bloque.

				// Se valida el tipo de retorno del bloque.
				if (!expectedReturnType.equals(block.resolvedReturnType())) {
					// El bloque no retorna el tipo correcto en todas sus ramas.
					throw new ASTError(loc, "EL METODO " + name + " DEBE RETORNAR UN VALOR DEL TIPO "
							+ expectedReturnType.toJson() + " EN TODAS LAS RAMAS DEL BLOQUE.");
				}

				ts.endMethod();
			}
		} catch (error.semantic.declarations.InternalError e) {
			throw new InternalError(loc, e.getMessage());
		}
	}
}
