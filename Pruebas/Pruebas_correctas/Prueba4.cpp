//Prueba en que se manipulan objetos locales y globales
//modificando atributos públicos y llamando a métodos que manipulan
//atributos privados

class Persona
	{
	public:
		int edad;
		int DNI;
		char* nombre;
		void SetSexo(int);
		int GetSexo();
	private:
		int sexo;
	};

void Persona :: SetSexo (int sexo_entrada)
	{
	sexo = sexo_entrada;
	}

int Persona :: GetSexo (void)
	{
	return sexo;
	}

Persona maria;


void main ()
	{
	Persona juan;
	juan.edad = 15;
	juan.DNI = 32443;
	juan.nombre = "Juan";
	juan.SetSexo (1);
	maria.edad = 16;
	maria.DNI = 23334;
	maria.nombre = "Maria";

	if (juan.GetSexo() == 1)
		{
		cout << juan.nombre << " tiene una edad de "<< juan.edad << " anios, su DNI es " << juan.DNI << " y es hombre";
		}
	else
		{
		cout << juan.nombre << " tiene una edad de "<< juan.edad << " anios, su DNI es " << juan.DNI << " y es mujer";
		}

	if (maria.GetSexo() == 1)
		{
		cout << maria.nombre << " tiene una edad de "<< maria.edad << " anios, su DNI es " << maria.DNI << " y es hombre";
		}
	else
		{
		cout << maria.nombre << " tiene una edad de "<< maria.edad << " anios, su DNI es " << maria.DNI << " y es mujer";
		}
	
	}
