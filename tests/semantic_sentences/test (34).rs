class Ejemplo {
	fn m0() -> void {
		if (false) {
			return nil;
		}
	}
	fn m1() -> I32 {
		if (true){
			return 1;
		} else {
			if (false) {
				return 1;
			} 
		}
		return 2;
	}
	fn m2() -> I32 {
		if (true){
			return 1;
		} else {
			if (false) {
				return 1;
			} 
			// Falta return aca.
		}
	}
}

fn main () {
	return nil;
} 
	/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 17 | COLUMNA 5 | EL METODO m2 DEBE RETORNAR UN VALOR DEL TIPO "I32" EN TODAS LAS RAMAS DEL BLOQUE. | */
