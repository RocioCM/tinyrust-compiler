fn main() {
	if (true) {} else {
		return; // bien
		return nil; // bien
		return 12; // mal
	}
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 5 | COLUMNA 10 | SE ESPERABA UNA EXPRESION DE TIPO "void" PERO SE ENCONTRO UNA EXPRESION DE TIPO "I32" | */
