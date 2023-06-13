class Derivada : Base {
	I32: x;

	fn hola(Str: a) -> void {
		(IO.out_str(a));
	}

	create() {
		x = 0;
		(hola("Hola mundo!"));
		(update());
		(update());
		(print());
	}

}

class Base {
	I32: s;

	fn update() -> void {
		s = s + 1;
	}

	fn print() -> void {
		(IO.out_str("Valor de s: "));
		(IO.out_i32(s));
	}
}


fn main() {
	// Herencia de metodos
	Base: b1;

	b1 = new Derivada();
	
}
