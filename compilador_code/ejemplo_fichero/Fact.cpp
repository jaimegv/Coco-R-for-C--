/* Programa de ejemplo  */  

char *s;	/* variable global sin inicializar */

int FactorialRecursivo (int n)	/* par�metro entero local a la funci�n */
{
	if (n == 0)
		return 1;
	return n * FactorialRecursivo (n - 1);	/* llamada recursiva */
}

int FactorialDo (int n)
{
	int factorial = 1;	// variable local inicializada a uno
	do
	{
		factorial *= n--;	// equivale a: factorial = factorial * n; n = n - 1;
	} while (n);		// mientras n no sea 0
	return factorial;	// devuelve el valor de la variable factorial
}

int FactorialWhile (int n)
{
	int factorial = 1, i = 0;	// variables locales inicializadas
	while (i < n)
		factorial *= ++i;	// equivale a: i = i + 1; factorial = factorial * i;
	return factorial;
}

int FactorialFor (int n)
{
	int i, factorial = 1;	/* variables locales */
	for (i = 1; i <= n; i++)
		factorial *= i;
	return factorial;
}

void imprime (char *msg,  f)	/* funci�n que recibe una cadena y un entero, pero no devuelve nada */
{
	cout << s << msg << f;
	cout << "\n";	// imprime un salto de l�nea */
	return;	/* finaliza la ejecuci�n de la funci�n (en este caso, se podr�a omitir) */
}

void cuadrados (int z[10], int dim)	
/* el vector se pasa por referencia, por tanto los cambios aqu� realizados se reflejan fuera */
{
	int i;
	for (i=0; i < dim; i++)
		z[i] *= z[i];	/* z[i] = z[i] * z[i];	*/
}

//Declaraci�n de la clase Fecha:
class Fecha
{
public:
	void ponFecha (int, int, int);	/* m�todo que recibe tres enteros y no devuelve nada */
	int daDia (void);	// m�todo que devuelve un entero
	int daMes (void);	// m�todo que devuelve un entero
	int daAnno (void);	// m�todo que devuelve un entero
	void imprime (void);	// m�todo que no recibe ni devuelve nada
private:
	int d,m,a;	// tres enteros privados
};

//Definici�n de los m�todos de la clase Fecha:
void Fecha::ponFecha (int dd, int mm, int aa)
{
	d=dd;
	m=mm;
	a=aa;
}
int Fecha::daDia (void)
{
	return d;
}
int Fecha::daMes (void)
{
	return m;
}
int Fecha::daAnno (void)
{
	return a;
}
void Fecha::imprime (void)	/* no se confunde con la funci�n imprime por estar en otro �mbito */
{
	cout << d << "-" << m << "-" << a;
}

//Declaraci�n de la clase Persona
class Persona
{	//atributos privados
	char * nombre;	// el nombre es una cadena
	Fecha nacimiento;	// la fecha de nacimiento es de tipo Fecha
				// No hay que implementar atributos de tipo clase
	bool masculino;	// el sexo se representa con un valor l�gico
	int edad (void);	// m�todo privado que devuelve un entero
public:
	int numero;	// el n�mero de Persona es entero y p�blico
	void ponNombre (char *);	// m�todo que recibe una cadena y no devuelve nada
	void ponNumero (int);	// m�todo que recibe un entero y no devuelve nada
	void ponFechaNacimiento (Fecha);	/* m�todo que recibe una Fecha y no devuelve nada */
	void ponSexo (bool);	// m�todo que recibe un l�gico y no devuelve nada
	char *daNombre (void);	// m�todo que no recibe nada y devuelve una cadena
	int daNumero (void);	// m�todo que no recibe nada y devuelve un entero
	void daFechaNacimiento (Fecha &);	/* m�todo que tiene un par�metro Fecha por referencia y no devuelve nada */
	bool esMujer (void);	// m�todo que no recibe nada y devuelve un l�gico
	void imprime (void);	// m�todo que no recibe ni devuelve nada
};

//Definici�n de los m�todos de la clase Persona:
void Persona::ponNombre (char * s)
{
	nombre=s;
}
void Persona::ponNumero (int n)
{
	numero=n;
}
void Persona::ponFechaNacimiento (Fecha f)	// no hay que implementar clases como par�metros
{
	nacimiento=f;
}
void Persona::ponSexo (bool b)
{
	masculino=b;
}
char * Persona::daNombre (void)	// no hay que implementar funciones que devuelvan cadenas
{
	return nombre;
}
Persona::daNumero (void)
{
	return numero;
}
void Persona::daFechaNacimiento (Fecha &f)	// no hay que implementar clases como par�metros
{
	f=nacimiento;
}
bool Persona::esMujer (void)
{
	return !masculino;
}
int Persona::edad (void)
{
	return 2001-nacimiento.daAnno ();
}
void Persona::imprime (void)
{
	cout << nombre << ", con DNI n� " << numero << ", " << (masculino?"hombre":"mujer")
	     << ", de " << edad () << "a�os de edad, naci� el: ";
	nacimiento.imprime();
}

void personas (void)
{
	Persona yo, ella;	//declaro dos objetos de tipo Persona
	Fecha fecha;		//declaro un objeto de tipo Fecha

	yo.ponNumero (1234);	// pongo mi n�mero de DNI con el m�todo
	ella.numero = 4321;	/* pongo su n�mero de DNI con el atributo, pues es p�blico */
	yo.ponNombre ("Pepe");	// pongo los nombres con el m�todo
	ella.ponNombre ("Paloma");

	fecha.ponFecha (11, 2, 1972);	 // creo mi fecha de nacimiento
	yo.ponFechaNacimiento (fecha);	 // pongo mi fecha de nacimiento
	fecha.ponFecha (13, 10, 1975);	 // creo su fecha de nacimiento
	ella.ponFechaNacimiento (fecha); // pongo su fecha de nacimiento
	yo.ponSexo (true);	// pongo el sexo
	ella.ponSexo (0);

	cout << "Voy a imprimir los datos que he recopilado:";
	yo.imprime ();
	ella.imprime ();
	cout << "Ahora voy a imprimir mis datos usando los m�todos:";
	cout << yo.daNombre() << ", con DNI n� " << yo.numero << ", "
   	  << (!yo.esMujer()?"hombre":"mujer") << ", de ";
				// no puedo usar el m�todo edad() porque es privado
	yo.daFechaNacimiento (fecha);	/* se modifica el par�metro, porque es por referencia */
	cout << (2001-fecha.daAnno()) << "a�os de edad, naci� el ";
	fecha.imprime();	/*si nacimiento fuera p�blico, se podr�a hacer yo.nacimiento.imprime() */
}

void demo ()	/* funci�n sin argumentos que no devuelve nada */
{
	int i, k;	// Variables locales
	int v[10], zv[10];
	char *s;

	s="El primer valor era cero\n";

	cout << "Escriba tres n�meros: ";
	cin >> v[1]; cin >> v[2]; cin >> v[3];

	if (!((v[1] == v[2]) && (v[1] != v[3])))	/* NOT ((v[1] igual a v[2]) AND (v[1] distinto de v[3]))  */
		v[0] = (v[2] < v[3]) ? v[2]: v[3];	/* si v[2]<v[3], v[0]=v[2]; en otro caso v[0]=v[3] */
	if (!v[1])
		cout << s;
	k=4;
	cuadrados (v, k);
	for (i=0; i < k; i++)
		cout << v[i];	/* Imprime los elementos del vector al cuadrado */
	for (i=1; i <= 10; ++i)
		zv[i-1]=i;
	cuadrados (zv, 10);
	for (i=0; i <= 9; i++)
		cout << (zv[i]);	/* Imprime los elementos del vector al cuadrado */
   cout << "\n";
}


void main (void)	// funci�n principal
{
	int num;		// variable local a la funci�n main
	int For, Do, While;	// tres variables enteras


	s = "El factorial ";

	cout << s;
	cout << "\nIntroduce un n�mero";
	cin >> num;	/* se lee un n�mero del teclado */


	switch (num)
	{
		case 0: cout << "El factorial de 0 siempre es 1.";
			break;
		default:
			if (num < 0)
				cout << "No existe el factorial de un negativo.";
			else
			{
				For = FactorialFor (num);
				While = FactorialWhile (num);
				Do = FactorialDo (num);

				imprime ("recursivo es: ", FactorialRecursivo (num));
				imprime ("con do-while es: ", Do);
				imprime ("con while es: ", While);
				imprime ("con for es: ", For);
			}
	}
	demo();	// llamada a funciones sin argumentos 
	personas ();
}
