// Prueba Incorrecta

// declaracion de metodo y clase
class Clase_mal {
int a,b,c;	// declaracion de metodo privado, esto esta ok!
public 		// falan DOS_PUNTOS
	int metodo ();	// declaracion erronea
}	// falta PUNTO_COMA

// Declaración de la clase Fecha:
class Fecha {
public: int a, b, c;
	void ponFecha (int, int, int);	/* método que recibe tres enteros y no devuelve nada */
	int daDia (void);	// método que devuelve un entero
	int daMes (void);	// método que devuelve un entero
	int daAnno (void);	// método que devuelve un entero
	void imprime (void);	// método que no recibe ni devuelve nada
private:
	int d,m,a;	// tres enteros privados
};

// FALTA DEFINICION DE METODOS!


// Definicion de un metodo daDia de la clase Fecha
int Fecha::daDia (void){
	return d;
}

void main (int dia) {
	Fecha var;
	return var.daDia();
}
