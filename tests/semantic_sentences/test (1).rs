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
		a = someDifferentStuff();
	}
}

class Fibonacci: Fibo {
	Hola: create2;
	Bool: destroy2;
	I32: hola1, hola, chau;
	Char: chaU2;
	pub Array I32: hola2, hola3, hola4;

	fn hola() -> void {}

	fn destroy(Str: s) -> void {
		create = someOtherDifferentStuff();
		self = breakingChange();
		// TODO: chequear que ninguna VARIABLE se llame self. ni create??
	}

	fn hola3() -> Hola {
		return 12;
	}

	fn hola1(Hola: hola, Hola: h) -> I32 {
		Hola: chau, chau2, adios; Hey: hou;
		Array I32: c;
		return;
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
	n[21] = 12;
	a.b.c = !(1 + 2 * 3 != -10 + 2) || a / 4 % -+-5 < 5;	
}

/* CORRECTO: SEMANTICO - DECLARACIONES */
