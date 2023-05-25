class Base {
	pub Derivada: s;
	fn m1() -> Base {
		(new Derivada().s);
		return self;
	}
}

class Derivada : Base {
	fn m2(Str: a) -> void {
		self.s = self.m1();
	}
}

fn main() {
}
