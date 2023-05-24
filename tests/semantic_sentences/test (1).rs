class Hola {} 

class Hola1 {
	static fn hola() -> void {}
	fn hola1() -> Array I32 {}
	static fn hola2() -> I32 {}
	fn hola3() -> Hola {}
} 

class Hola2: Hola {
	create () {}
	I32: create, destroy;
	fn destroy(Str: n) -> void {}
	fn hola3() -> Hola {}
} 

class Fibo: Hola2 {
	fn destroy(Str: h) -> void {
	}
}

class Fibonacci: Fibo {
	Hola: create2;
	Bool: destroy2;
	I32: hola1, hola, chau;
	Char: chaU2;
	pub Array I32: hola2, hola3, hola4;

	fn hola() -> void {
		return;
		return nil;
	}

	fn destroy(Str: s) -> void {
		Hola2: var;
		var = new Fibo(); // Subclase
		(IO.out_str("Hola"));
		if (10<2) {};
		if (true) {
		} else if (false) {
			(IO.out_i32(("").length()));
		} else {
			(IO.out_i32(--+-+-+-+++(("hola").length())));
		}
		while (true || false) {
			(IO.out_char('a'));
		}
	}

	fn hola3() -> Hola {
		return new Hola(); // Clase original
		return new Hola2(); // Subclase
	}

	fn hola1(Hola: hola, Hola: h) -> I32 {
		Hola: chau, chau2, adios; Hola: hey;
		Array I32: c;
		return 12 / 0;
	}
	
	pub Hola: test;

	static fn hola4() -> Array I32 {}
	static fn hola2(Array Str: hola, Hola2: chau, I32: hola33_12, Str: ho_la, Hola: hola21) -> Hola {}
	pub Array Bool: holas, holaa, chau2;
	create (Array Char: hola, Hola2: chau, I32: hola33_12, Str: ho_la, Hola: hola21) {
	}
}


fn main() {
	Hola: a;
	a = new Fibonacci(new Char[2+3], new Fibo(), 3 /10, "Hola", new Hola()); 
	// a.b.c = !(1 + 2 * 3 != -10 + 2) || a / 4 % -+-5 < 5;	
}

/* CORRECTO: SEMANTICO - DECLARACIONES */
