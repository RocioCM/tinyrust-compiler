class Clase {
	fn metodo() -> I32 {
		if (false) {
			if (false) {
				return 3;
			} else {while(true)(nil);}
			// Deberia incluir un return dentro del else o en esta linea o afuera del if mas externo.
		} else {
			{if (false){
				return 2;
			}};
		}
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 2 | COLUMNA 5 | EL METODO metodo DEBE RETORNAR UN VALOR DEL TIPO "I32" EN TODAS LAS RAMAS DEL BLOQUE. | */
