class Base {
	fn suma(I32: x, I32: y) -> I32 {
		return x + y;
	}

	fn restarNumeros(I32: x, I32: y) -> I32 {
		return x - y;
	}

	create(I32: x, I32: y, Bool: z) {
		I32: suma, resta;

		(IO.out_str("Booleano: "));
		(IO.out_bool(z));

		suma = suma(x, y);
		resta = restarNumeros(x, y);

		(IO.out_str("Suma: "));
		(IO.out_i32(suma));
		(IO.out_str("Resta: "));
		(IO.out_i32(resta));
	}
}


fn main() {
	// Argumentos y retorno de metodos
	I32: a, b;
	Bool: c;
	Base: b1;

	a = 10;
	b = 20;
	c = true;
	b1 = new Base(a, b, c);
}
