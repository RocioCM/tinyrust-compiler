class Base {
	pub Derivada: s;
	fn m1() -> Base {
		(new Derivada().s);
		return self;
	}
}

class Derivada : Base {
	fn m2(Str: a) -> void {
		self.s = self.m1();
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 11 | COLUMNA 12 | SE ESPERABA UNA EXPRESION DE TIPO "Derivada" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Base" | */
