class Derivada : Base {
	I32: x;
	static fn m1() -> void {
	}
}

class Base {
	pub Str: s;
	I32: a;
	static fn m1() -> void {
	}
}

fn main() {
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}
/* ERROR: SEMANTICO - DECLARACIONES
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 3 | COLUMNA 12 | NO ESTA PERMITIDO REDEFINIR METODOS ESTATICOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL METODO ESTATICO m1 | */
