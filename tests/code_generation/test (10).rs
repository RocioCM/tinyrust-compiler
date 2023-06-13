fn main() {
	// Bucles
	I32: a;
	Bool: b;


	while (false) {
		(IO.out_str("not printed"));
	}
	(IO.out_str("out 1"));

	a = 0;
	while (a<=10) {
		(IO.out_i32(a));
		a = a + 1;
	}
	(IO.out_str("out 2"));

	b = true;
	while (b) {
		(IO.out_bool(b));
		b = !b;
	}
	(IO.out_str("out 3"));

	// El loop infinito tambien funciona!
	// while (true) {
	// 	(IO.out_str("Infinito..."));
	// }
}
