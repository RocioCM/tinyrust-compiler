class Derivada : Base {
	fn print() -> void {
		(IO.out_str("Hola mundo! :D "));
	}

	create() {
		(print());
	}

}

class Base {
	fn print() -> void {
		(IO.out_str("Hola mundo cruel :c "));
	}

	create() {
		(print());
	}
}


fn main() {
	// Polimorfismo
	Base: b1, b2;

	b1 = new Base();
	b1 = new Derivada();

	// b1.print(); // TODO: implementar encadenados
	// b2.print(); // TODO: implementar encadenados
	
}
