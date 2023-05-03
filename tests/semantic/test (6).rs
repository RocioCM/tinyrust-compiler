class Derivada : IO {
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
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}