fn main(){
	// Input output de enteros
	I32: a;

	(IO.out_i32(10));
	
	a = IO.in_i32();
	(IO.out_i32(a));
	
	(IO.out_i32(IO.in_i32()));
}