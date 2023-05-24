package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.NotFoundError;
import semantic_analyzer.symbol_table.AttributeEntry;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;
import util.Json;

public class ChainedAttributeNode extends ChainedAccessNode {

    public ChainedAttributeNode(String accessedAttr, Location loc) {
        super(accessedAttr, null, loc);
    }

    public ChainedAttributeNode(String accessedAttr, ChainedAccessNode chainedAccess, Location loc) {
        super(accessedAttr, chainedAccess, loc);
    }

    @Override
    public String toJson() {
        Json json = new Json();
        json.addAttr("tipo", "acceso atributo simple");
        json.addAttr("nombre-atributo", super.accessedEntity());
        json.addAttr("encadenado", super.chainedAccess());
        return json.toString();
    }

    @Override
    protected Type validateAndResolveType(SymbolTable ts, ClassEntry accessedClass) throws ASTError {
        Type returnType;
        // Obtener la entrada de la TS del atributo accedido.
        AttributeEntry attrEntry = accessedClass.attributes().get(super.accessedEntity());

        if (attrEntry == null) {
            throw new NotFoundError(loc, "SE INTENTO ACCEDER AL ATRIBUTO " + super.accessedEntity() + " DE LA CLASE "
                    + accessedClass.name() + ", PERO LA CLASE NO POSEE TAL ATRIBUTO.");
        }

        // Validar que no se accedan atributos privados de otras clases.
        try {
            if (!attrEntry.isPublic() && !accessedClass.name().equals(ts.currentClass().name())) {
                // Si el atributo es privado, no se puede acceder desde una clase distinta.
                throw new ASTError(loc, "EL CAMPO " + super.accessedEntity() + " DE LA CLASE "
                        + accessedClass.name() + " NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO.");
            }
        } catch (error.semantic.declarations.InternalError e) {
            throw new InternalError(loc, e.getMessage());
        }

        if (super.chainedAccess() != null) {
            // Recursivo: resolver y validar el encadenado si existe.
            ClassEntry attrClass = ts.getClass(attrEntry.type().type());
            if (attrClass == null) {
                // La clase debería existir ya que la TS ya está validada y consolidada.
                throw new InternalError(loc,
                        "EL TIPO DEL ATRIBUTO " + super.accessedEntity() + " DE LA CLASE "
                                + accessedClass.name() + " NO EXISTE EN LA TS.");
            }
            returnType = super.chainedAccess().validateAndResolveType(ts, attrClass);
        } else {
            // Tope recursivo: devolver el tipo del atributo.
            returnType = attrEntry.type();
        }

        return returnType;
    }
}
