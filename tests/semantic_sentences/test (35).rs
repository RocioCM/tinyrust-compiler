class Ejemplo {
	fn m1() -> I32 {
		while(true) {
			return 1;
		}
	}
	fn m2()-> I32 {
		while(true) {
			if (true) {
				return 1;
			}
			// Falta return aca.
		}
		// o aca.
	}
}

fn main () {
	return nil;
} 
	/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 5 | EL METODO m2 DEBE RETORNAR UN VALOR DEL TIPO "I32" EN TODAS LAS RAMAS DEL BLOQUE. | */
