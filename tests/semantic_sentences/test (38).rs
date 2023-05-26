class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base {
	pub Derivada: s;
	Derivada: a;
	fn m1() -> Base {
		return self;
	}
}

fn main() {
	(new Base().s.m1().a.m1());
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 16 | COLUMNA 21 | EL CAMPO a DE LA CLASE Base NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO. | */
