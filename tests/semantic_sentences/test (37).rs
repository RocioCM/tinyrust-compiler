class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base {
	pub Str: s;
	I32: a;
	fn m1() -> void {
	}
}

fn main() {
	(new Base().s.length().a);
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 15 | COLUMNA 25 | SE INTENTO ACCEDER AL ATRIBUTO a DE LA CLASE I32, PERO LA CLASE NO POSEE TAL ATRIBUTO. | */
