class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
		s = self;
		b = self.s;
		self.s = self.m1();
	}
}

class Base {
	pub Derivada: s;
	pub Base: b;
	pub I32: a;
	fn m1() -> Base {
		return self;
	}
}

fn main() {
	(new Base().s.m1().a + (new Base().a * 3 < 5));
}
