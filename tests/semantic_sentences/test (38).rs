class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base {
	pub Derivada: s;
	Derivada: a;
	fn m1() -> Base {
		return self;
	}
}

fn main() {
	(new Base().s.m1().a.m1());
}
