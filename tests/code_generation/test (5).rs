fn main(){
	// Variables
	I32: a;
	Bool: b;

	a = 1+2*(3+5);
	b = !false && a > 15;

	(IO.out_i32(a)); // 17
	(IO.out_bool(b)); // true
	(IO.out_bool(!b)); // false
	(IO.out_bool(b && false)); // false
	(IO.out_bool(b || false)); // true
}