fn main() {
	if (true || 12) {} else {}
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 2 | COLUMNA 14 | SE ESPERABA UNA EXPRESION DE TIPO "Bool" PERO SE ENCONTRO UNA EXPRESION DE TIPO "I32" | */
