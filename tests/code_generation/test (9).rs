fn main() {
	// If
	I32: a, b;
	Bool: c;

	a = 10;	b = 20;	c = true;

	// true
	if (true) {
		(IO.out_str("true"));
	} 
	
	// false
	if (false) {
		(IO.out_str("false"));
	}

	// true
	if (!false) {
		(IO.out_str("true"));
	} else {
		(IO.out_str("false"));
	}

	// false
	if (!true) {
		(IO.out_str("false"));
	} else {
		(IO.out_str("true"));
	}

	// menor
	if (a > b) {
		(IO.out_str("mayor o igual"));
	} else {
		(IO.out_str("menor"));
	}

	// igual
	if (a < 10) {
		(IO.out_str("menor"));
	} else if (a == b) {
		(IO.out_str("igual"));
	} else (IO.out_str("mayor"));
	
	// menor
	if (a <= b) {
		(IO.out_str("menor o igual"));
		if (a == b) {
			(IO.out_str("igual"));
		} else {
			(IO.out_str("menor"));
		}
	} else {
		(IO.out_str("mayor"));
	} 
}
