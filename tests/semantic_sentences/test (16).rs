class Clase {
	fn metodo() -> I32 {
		if (false) {
			return 2;
		}
		// Deberia incluir un return fuera del if.
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 2 | COLUMNA 5 | EL METODO metodo DEBE RETORNAR UN VALOR DEL TIPO "I32" EN TODAS LAS RAMAS DEL BLOQUE. | */
