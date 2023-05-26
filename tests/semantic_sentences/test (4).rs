class Derivada : Base {
	pub I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base {
	fn m1() -> void {
		x.y = 10; // x es de la subclase
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 9 | COLUMNA 3 | SE INTENTO ACCEDER A LA VARIABLE x PERO NO ESTA DEFINIDA EN EL AMBITO ACTUAL. | */
