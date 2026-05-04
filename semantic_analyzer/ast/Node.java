package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.TableElement;

public interface Node extends TableElement {
	/**
	 * Valida que el nodo cumpla con las reglas semánticas del lenguaje, es decir,
	 * que el nodo y sus subnodos sean del tipo esperado para el contexto en que son
	 * utilizados.
	 * 
	 * @param ts - Tabla de Símbolos consolidada.
	 * @throws ASTError En caso de que el nodo o su subárbol sean semánticamente
	 *                  inválidos.
	 */
	public void validate(SymbolTable ts) throws ASTError;

	/**
	 * Construye y retorna el código ASM generado para el nodo y todo su subárbol.
	 * 
	 * @param ts - Tabla de Símbolos consolidada.
	 */
	public String generateCode(SymbolTable ts) throws ASTError;
}
