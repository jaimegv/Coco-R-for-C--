class Persona {
	int numero;
	public:
		int hola;
		void llamarNumero(int);
	private:
		void setNumero(int);		
};

void Persona::llamarNumero (int numerajo) {
	setNumero(numerajo);
}

void Persona::setNumero (int numerito) {
	cout << "Antiguo valor de numero: " << numero;
	numero = numerito;
	cout << "Nuevo valor de numero: " << numero;
}

void main (void) {
	Persona Juan;
	Juan.llamarNumero(300);
	
}


