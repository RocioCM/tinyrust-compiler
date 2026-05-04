class Derivada : Base {
	fn print() -> void {
		(IO.out_str("Hola mundo! :D "));
	}

	create() {
	}
}

class Base {
	fn print() -> void {
		(IO.out_str("Hola mundo cruel :c "));
	}
}


fn main() {
	// Polimorfismo
	Base: b1, b2;

	b1 = new Base();
	b2 = new Derivada();

	(b1.print());
	(b2.print());
	
}
