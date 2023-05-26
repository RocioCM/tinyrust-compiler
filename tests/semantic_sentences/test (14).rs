class Clase {
	fn metodo() -> I32 {
		return 30; // bien
		return "hola"; // mal
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 4 | COLUMNA 10 | SE ESPERABA UNA EXPRESION DE TIPO "I32" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Str" | */
