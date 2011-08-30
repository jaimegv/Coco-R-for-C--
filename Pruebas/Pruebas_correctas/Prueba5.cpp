/* Prueba evaluadora de objetos, metodos y atributos */



class Persona {
		bool novia;	// definido como privado
	public:
		int Altura;
		bool Moreno;
		int getEdad();
		void setEdad (int);
		bool esMoreno();
		bool tieneNovia();
	private:
		int Edad;
};

void Persona::setEdad (int numero) {
	cout << "Actualizado valor de Edad.";
	Edad = numero;
}

int Persona::getEdad (void) {
	cout << "Edad con valor: " << Edad;
	return Edad;
}


Persona Generico;
int caracola;

void main (void) {
	Persona Juan;
	int variable;	//

	caracola =30;

	cout << "Comienzo Main.";
	cout << "Inicializo el objeto Juan, declarado como Persona";
	cout << "Dame una altura para Juan: ";
	cin >> Juan.Altura;
	Juan.Moreno = true;
	if (Juan.Moreno) {
		cout << "Juan es Moreno.";
	} else {
		cout << "Juan no es Moreno.";
	}
	Juan.setEdad(caracola);
	variable = Juan.getEdad();		// esto imprime cosas por pantalla

}
