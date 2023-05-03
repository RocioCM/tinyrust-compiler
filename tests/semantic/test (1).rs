class Hola {} 
class Hola1 {
	static fn hola() -> void {}
	fn hola1() -> Array I32 {}
	static fn hola2() -> I32 {}
	fn hola3() -> Cosa {}
} 
class Hola2: Hola {
	create () {}
	I32: create, destroy;
	fn destroy() -> void {}
	fn hola3() -> Cosa {}
} 

class Fibo: Hola2 {
	fn destroy() -> void {
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

	fn destroy() -> void {
		create = someOtherDifferentStuff();
		self = breakingChange();
	}

	fn hola3() -> Cosa {
		return 12;
	}

	fn hola1(Hola: hola, Hola: h) -> I32 {
		Hola: chau, chau, adios; Hey: hou;
		return;
	}
	
	pub Hola: test;

	static fn hola4() -> Array I32 {}
	static fn hola2(Array Str: hola, Chau: chau, I32: hola33_12, Str: ho_la, Hola: hola21) -> Cosa {}
	pub Array Bool: holas, holaa, chau2;
	create (Array Char: hola, Chau: chau, I32: hola33_12, Str: ho_la, Hola: hola21) {
	}
}


fn main() {
	Hola: hola;
}

/* CORRECTO: SEMANTICO - DECLARACIONES */
