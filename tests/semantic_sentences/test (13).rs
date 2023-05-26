class Clase {
	fn metodo() -> I32 {
		{
			{
				while (12 < 3 * 2) {
					return; // mal, espera i32.
				}
			}
		}
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 6 | COLUMNA 6 | SE ESPERABA UNA EXPRESION DE TIPO "I32" PERO SE ENCONTRO UNA EXPRESION DE TIPO "void" | */
