fn main() {
	Array Bool: a;
	a = new I32[5];
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 3 | COLUMNA 13 | SE ESPERABA UNA EXPRESION DE TIPO "Array-Bool" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Array-I32" | */
