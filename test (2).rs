class Fibonacci {
	I32: suma;
	I32: i,j;
	static fn sucesion_fib(I32: n)-> void {
		I32: suma;
		I32: i,j;
		i=0; j=0; suma=0;
		while (i<= n){
			if (i==0){
				(Fibonacci.imprimo_numero(i));
				(Fibonacci.imprimo_sucesion(suma));
			}
			else if(i==1){
				(Fibonacci.imprimo_numero(i));
				suma=suma+i;
				(Fibonacci.imprimo_sucesion(suma));
			}
			else{
				(Fibonacci.imprimo_numero(i));
				suma=suma+j;
				j=suma;
				(Fibonacci.imprimo_sucesion(suma));
			}
			i = i + 1;
		}
	}
	create(){
		i=1;
		j=2;
		suma=3;
	}
	static fn imprimo_numero(I32: num) -> void{
		(IO.out_str("f_"));
		(IO.out_i32(num));
		(IO.out_str("="));
	}
	static fn imprimo_sucesion(I32: s) -> void{
		//"el valor es: ";
		(IO.out_i32(s));
		(IO.out_str("\n"));
	}
	static fn a(I32: i, I32: j, I32: k) -> void{
		I32: x, y, z;
		x = 5; y = 6; z = 7;
	}
}
fn main() {
	Fibonacci: fib;
	I32: n;
	fib = new Fibonacci();
	// n = IO.in_i32();
	// (Fibonacci.sucesion_fib(n));
}

// CORRECTO: SEMANTICO - SENTENCIAS
