class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
		s = self;
		b = self.s;
		self.s = self;
	}
}

class Base {
	pub Derivada: s;
	pub Base: b;
	pub I32: a;
	fn m1() -> Base {
		self.b = s;
		return self;
	}
}

fn main() {
	(new Base().s.m1().a + (new Base().a * 3 < 5));
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 21 | COLUMNA 43 | SE ESPERABA UNA EXPRESION DE TIPO "I32" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Bool" | */
