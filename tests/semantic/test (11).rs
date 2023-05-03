class Derivada : Base {
	I32: x;
}

class Base {
	I32: x;
	fn m1() -> void {
	}
}

fn main() {
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}