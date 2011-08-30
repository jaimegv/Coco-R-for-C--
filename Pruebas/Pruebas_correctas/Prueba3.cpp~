//Se prueban las expresiones booleanas, las sentencias if-else,
//los vectores, llamadas a funciones, recursividad, etc...


int caracola, tronquito;

int vector[10];

void rellenar_vector(int numero)
	{
	if (numero == 9)
		{
		vector[numero] = numero * 10;
		}
	else
		{
		vector[numero] = numero *10;
		rellenar_vector(numero+1);
		}
	return;
	}

void imprime_vector(int numero)
	{
	numero = numero - 1;
	cout << vector [numero];
	if (numero != 0)
		{
		imprime_vector(numero);
		}
	}

bool variable, variable2; 

void expresiones_booleanas(void)
	{
	variable = true;
	variable2 = false;

	if !variable && (!(variable || variable2))
		{
		cout << "false && (false && true) = verdadero"; //Evidentemente nunca ira por esta rama
		}
	else
		{
		cout << "false && (false && true) = falso";
		}	
	}

void main()
	{
	expresiones_booleanas();
	caracola = 5;
	tronquito = caracola + 5 * caracola; //Pruebas de precedencia de operador
	cout << "Tronquito vale " << tronquito; // Tronquito valdra 30
	rellenar_vector(0);
	imprime_vector(10);
	// El resultado de esto sera imprimir 10 lineas con el valor de cada una de las posiciones del vector
	// que se habra inicializado antes (90 80 70 60 50 40 30 20 10 0)
	}
