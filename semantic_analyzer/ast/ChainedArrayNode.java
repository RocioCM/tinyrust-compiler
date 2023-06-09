package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.NotFoundError;
import semantic_analyzer.symbol_table.AttributeEntry;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.Type;
import util.Json;

public class ChainedArrayNode extends ChainedAccessNode {
    private ExpressionNode accessIndex;

    public ChainedArrayNode(String accessedEntity, ExpressionNode accessIndex, Location loc) {
        super(accessedEntity, null, loc);
        this.accessIndex = accessIndex;
    }

    @Override
    public String toJson() {
        Json json = new Json();
        json.addAttr("tipo", "acceso atributo arreglo");
        json.addAttr("nombre-atributo", super.accessedEntity());
        json.addAttr("indice", accessIndex);
        json.addAttr("encadenado", super.chainedAccess());
        return json.toString();
    }

    @Override
    protected Type validateAndResolveType(SymbolTable ts, ClassEntry accessedClass) throws ASTError {
        // Obtener la entrada de la TS del atributo accedido.
        AttributeEntry attrEntry = accessedClass.attributes().get(super.accessedEntity());

        if (attrEntry == null) {
            throw new NotFoundError(loc, "SE INTENTO ACCEDER AL ATRIBUTO " + super.accessedEntity() + " DE LA CLASE "
                    + accessedClass.name() + ", PERO LA CLASE NO POSEE TAL ATRIBUTO.");
        }

        // Validar que no se accedan atributos privados de otras clases.
        if (!attrEntry.isPublic() // El atributo es privado.
                && (attrEntry.isInherited() // Es heredado (no visible) o...
                        || !accessedClass.name().equals(ts.currentClass().name())) // La clase no es la actual.
        ) {
            // Si el atributo es privado, no se puede acceder desde una clase distinta.
            throw new ASTError(loc, "EL CAMPO " + super.accessedEntity() + " DE LA CLASE "
                    + accessedClass.name() + " NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO.");
        }

        // Validar que el atributo es de tipo arreglo y que el índice es de tipo I32.
        validate(ts, attrEntry, accessedClass.name());

        Array arrayAttr = (Array) (attrEntry.type());
        return arrayAttr.itemsType();
    }

    /**
     * Valida que el atributo dado es de tipo arreglo y que la expresión del índice
     * del arreglo es de tipo I32.
     * 
     * @param attr
     * @throws ASTError
     */
    private void validate(SymbolTable ts, AttributeEntry attr, String className) throws ASTError {
        // Validar el tipo del atributo.
        if (!new Array().equals(attr.type())) {
            throw new ASTError(loc,
                    "SE INTENTO ACCEDER A UN INDICE DEL ATRIBUTO " + super.accessedEntity() + " DE LA CLASE "
                            + className + ", PERO EL ATRIBUTO NO ES DE TIPO Array.");

        }

        // Validar el tipo del índice.
        accessIndex.setExpectedResolveType(new I32());
        accessIndex.validate(ts);
    }
}
