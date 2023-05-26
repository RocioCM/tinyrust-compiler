class Derivada: IO {
	I322: x;
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
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}
/* ERROR: SEMANTICO - DECLARACIONES 
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 2 | COLUMNA 8 | EL ATRIBUTO x ES DEL TIPO NO DECLARADO I322 | */
