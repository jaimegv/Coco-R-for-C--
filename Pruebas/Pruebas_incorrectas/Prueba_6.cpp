//Prueba 6. Errores a nivel sintáctico. Modificada la línea 31 (Intento de hace cout sin el operador <<
//Palabras reservadas int, for, cout, while, main
//Tokens ( ) { } += << -- > = 

int coco(int toto)
{
 int a;
 for (a = 50; a >0; a--)
	{
	toto += 2;
	cout << toto
	cout << a
	}
}

int cece(int toto)
{
 int a;
 for (a = 50; a >0; a--)
	{
	toto += 2;
	cout << toto
	cout << a
	}
}

int main((void) {     // ===============
  int i;
  while (i > 0)
	{
	cout /*<<*/ "hola";
	i--;
	}
}
