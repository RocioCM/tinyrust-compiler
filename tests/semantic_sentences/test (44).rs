class Base {
	pub Str: s;
	I32: a;
	fn m1() -> void {
		(m1().a); // m1 retorna void, no se puede acceder "a".
	}
}

fn main() {
}
/* CORRECTO: SEMANTICO - DECLARACIONES */
