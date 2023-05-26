class Base {
	I32: a;
}

class Derivada : Base {
	fn m2() -> void {
		self.a = 2;
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 8 | EL CAMPO a DE LA CLASE Derivada NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO. | */
