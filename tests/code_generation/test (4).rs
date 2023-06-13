fn main(){
	// Expresiones aritmeticas
	(IO.out_i32(10)); // 10
	(IO.out_i32(10+5)); // 15
	(IO.out_i32(10-5)); // 5
	(IO.out_i32(+++10-(--15))); // -5
	(IO.out_i32(2*3)); // 6
	(IO.out_i32(30/5)); // 6
	(IO.out_i32(33/5)); // 6
	(IO.out_i32(30%5)); // 0
	(IO.out_i32(33%5)); // 3
	(IO.out_i32(1+2*(3+5))); // 31
	(IO.out_bool(1+2*(3+5)>10*2)); // false
	(IO.out_bool(1+2*(3+5)<10*2)); // true
}