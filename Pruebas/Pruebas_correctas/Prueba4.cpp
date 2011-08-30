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

void main ()
	{
	Persona juan;
	juan.edad = 3;
	juan.DNI = 32443;
	juan.nombre = "Juan";
	juan.SetSexo (1);
	if (juan.GetSexo() == 1)
		{
		cout << juan.nombre << " tiene una edad de "<< juan.edad << " anios, su DNI es " << juan.DNI << " y es hombre";
		}
	else
		{
		cout << juan.nombre << " tiene una edad de "<< juan.edad << " anios y su DNI es " << juan.DNI << " y es mujer";
		}
	
	}
