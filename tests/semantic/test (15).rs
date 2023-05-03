class Derivada : Base {
	I32: x;
	fn m2(Str: a, I32: b) -> void {
	}
}

class Base {
	pub Str: s;
	I32: a;
	fn m2(I32: b, Str: a) -> void {
	}
}

fn main() {
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}