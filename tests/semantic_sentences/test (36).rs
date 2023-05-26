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
	(new Base().m1().a); // m1 retorna void, no se puede acceder "a".
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 15 | COLUMNA 14 | EL METODO m1 NO RETORNA NINGUN VALOR, NO SE PUEDE ACCEDER A ENCADENADOS DE void. | */
