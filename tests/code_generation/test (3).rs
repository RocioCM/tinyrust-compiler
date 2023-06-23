fn main(){
	// Expresiones booleanas
	(IO.out_bool(true)); // true
	(IO.out_bool(false)); // false
	(IO.out_bool(true || false)); // true
	(IO.out_bool(false || false)); // false
	(IO.out_bool(true || true)); // true
	(IO.out_bool(true && true)); // true
	(IO.out_bool(true && false)); // false
	(IO.out_bool(true && !false)); // true
	(IO.out_bool(!!true)); // true
	(IO.out_bool(!!!false)); // false
	(IO.out_bool(1<2)); // true
	(IO.out_bool(3<2)); // false
	(IO.out_bool(2<2)); // false
	(IO.out_bool(1>2)); // false
	(IO.out_bool(3>2)); // true
	(IO.out_bool(2>2)); // false
	(IO.out_bool(2>=2)); // true
	(IO.out_bool(2==2)); // true
	(IO.out_bool(2!=2)); // false
	(IO.out_bool(2==3)); // false
	(IO.out_bool(2!=3)); // true


}