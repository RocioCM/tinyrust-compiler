class Derivada : Base {
	I32: x;
	fn m1(Str: a, Str: b) -> void {
	}
}

class Base {
	pub Str: s;
	I32: a;
	fn m1(Str: a) -> void {
	}
}

fn main() {
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}