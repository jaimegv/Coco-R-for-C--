//ejmplos mios 
int main()
{
    float cambio; int cambioint, m10=0, m5=0, m2=0, m1=0, m50c=0;
    do
    {
        cout << "Cambio?: "; cin >> cambio;
        cambioint = (int)cambio;
    }while((cambio - cambioint) != 0 && (cambio - cambioint) != 0.50);
    while(cambio != 0)
    {
        if(cambio>=10)
        {
            m10++;
            cambio-=10;
        }
        else if(cambio>=5)
        {
            m5++;
            cambio-=5;
        }
        else if(cambio>=2)
        {
            m2++;
            cambio-=2;
        }
        else if(cambio>=1)
        {
            m1++;
            cambio-=1;
        }
        else if(cambio>=0.5)
        {
            m50c++;
            cambio-=0.5;
        }
    }
    cout << m10 << ", " << m5 << ", " << m2 << ", " << m1 << ", " << m50c;
}
///////////////////////////
float main (char jar, int qq, int argc, char *argv[]){
	int contador;
	char nombre = "fernando";
	contador ++;
	float jo, clase = 5.2, primero = lista (1);
	int vector[contador];
	
	area = figura.circulo(2,3);
	perimetro = figura.circulo(2,5);
	darnombre = figura.nombre;
	v[contador] = (v[contador] < v[contador]) ? v[contador]: v[contador];

}
	
//////////////////////////////
int main(int argc, char *argv[]) {
    int numero;
    std::cout << "Ingresa un numero: ";
    std::cin >> numero;
    numero = funcion_Devuelve(numero);
    std::cout << "La funcion funct_Devuelve ha devuelto el numero: " << numero << std::endl;
    std::cout << "Y ahora ejecutaremos la otra función que no devuelve valor pero que se ejecuta directamente" << std::endl;
    funcion_Nula();
    return 0;
}
int main(int argc, char *argv[]) {
            std::cout << "Hola mundo" << endl;
            return 0;
}
int main(int argc, char *argv[]) {
            printf("Hola Mundo");
            return 0;
}
//ejemplo luis
void main (char er, int qqwe, int qwe)	// función principal
{ 	
	int qwe, jarl = 123, HOLA, _asd, jarl=asd, cancion = lista(23);
	//Persona Luis;
	bool paco=0123, HOLA; int paco2;

	int i, k;	// Variables locales
	int v[10], zv[10];
	Persona yo, ella;	//declaro dos objetos de tipo Persona
	int lista(asd);
	
	// instruccion nula	
;

// aSIGNACIONES
s = "El factorial ";
hola = adios + 2;
For = FactorialFor (num);
ella.numero = 4321;	/* pongo su número de DNI con el atributo, pues es público */
v[0] = (v[2] < v[3]) ? v[2]: v[3];	/* si v[2]<v[3], v[0]=v[2]; en otro caso v[0]=v[3] */


	// llamadas a metodos
	yo.imprime ();
	yo.ponSexo (true);	// pongo el sexo
	yo.ponNumero (1234);	// pongo mi número de DNI con el método
	yo.ponNombre ("Pepe");	// pongo los nombres con el método
	fecha.ponFecha (11, 2, 1972);	 // creo mi fecha de nacimiento

	// ASignacion de un valor a un metodo
	ella.numero = fecha.ponFecha (11, 2, 1972);	// pongo su número de DNI con el atributo, pues es público 
//	s="El primer valor era cero\n";

//Ejemplo asignatura
void main (void)	// función principal
{
	int num;		// variable local a la función main
	int For, Do, While;	// tres variables enteras


	s = "El factorial ";

	cout << s;
	cout << "\nIntroduce un número";
	cin >> num;	/* se lee un número del teclado */


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
