/* Prueba evaluadora de objetos, metodos y atributos */



class Persona {
		bool novia;	// definido como privado
	public:
		int Altura;
		bool Moreno;
		int getEdad();
		void setEdad (int, int);
		bool esMoreno();
		bool tieneNovia();
	private:
		int Edad;
};

/*
 * Actualiza Edad del objeto con el valor de numero, ademas comprueba
 * que el segundo argumento es igual a la Altura de dicho objeto
*/
void Persona::setEdad (int numero, int medida) {
	int variable_;
	variable_ =0;
	variable_ += numero;	// operacion con asignacion
	if (variable_ == numero) {
		if (Altura != medida) { // no deberia entrar aqui
			cout << "Ha habido un error en setEdad.";
			cout << "setEdad con segundo argumento incorrecto.y no se modifica Edad, valor: " << Edad;
		} else {
			Edad = variable_;
			cout << "setEdad a: " << variable_;
			cout << "Actualizado Edad a: " << Edad;
		}
	} else {
		cout << "Ha habido un error en setEdad. y no se modifica Edad, valor: " << Edad;
	}
	return;	// no devuelve nada
}

int Persona::getEdad () {
	cout << "Edad con valor: " << Edad;
	return Edad;
}


/*
 * Dado un valor booleano comprueba si es cierto o no, imprimiendo
 * para cada caso un valor por pantalla
*/
void esmoreno (bool moreno) {
	if (moreno) {
		cout << "Es Moreno.";
	} else {
		cout << "No es Moreno.";
	}	
	// Denotamos que no hay return; devuelve void!
}


Persona Generico;	// Objeto Global
int caracola;		// variable Global

void main (void) {
	Persona Juan;	// Objeto local
	int variable;	// Varible local

	caracola =30;	// inicializacion variable global

	cout << "#################################################";
	cout << "Comienzo Main.";
	cout << "Inicializo el objeto Juan, declarado como Persona";
	cout << "Dame una altura para Juan: ";
	cin >> Juan.Altura;	// Introducir por pantalla entero.
	cout << "La altura de Juan es de: " << Juan.Altura;
	Juan.Moreno = true;
	esmoreno (Juan.Moreno);	// Llamar a una funcion con atributo, local
	Juan.setEdad(caracola, Juan.Altura+1);	// emitira un error y no modifica la edad
						// operacion como argumento de metodo
	Juan.setEdad(caracola, Juan.Altura);	// Metodo que recibe como parametro un argumento global
	variable = Juan.getEdad();		// esto imprime la Edad por pantalla
						// y obtengo cosas por pantalla.
// Juan.getEdad(void);
	cout << "Valor de Edad de Juan: "  << variable;



	cout << "FIN Main.";
	cout << "#################################################";
}
