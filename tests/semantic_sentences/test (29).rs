class Derivada {
	static fn m1(Array I32: var) -> void {
		var[0] = 1;
	}
}

fn main() {
	(Derivada.m1(new Bool[5]));
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 8 | COLUMNA 23 | SE ESPERABA UNA EXPRESION DE TIPO "Array-I32" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Array-Bool" | */
