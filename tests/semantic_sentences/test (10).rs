fn main() {
	if (12 || true) {} else {
		return; // bien
		return nil; // bien
		return 12; // mal
	}
}
