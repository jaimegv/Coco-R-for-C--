//----CUENTA ALUMNOS CON NOTAS NOTABLES----//

#include <math.h>
#include <iostream.h>
#include <stdlib.h>
#include <conio.h>

void main(void)
{int id_nota, cantidad_notas=0, nota=0, cuenta_notables=0;
clrscr();

cout <<"CONTAR CANTIDAD DE ALUMNOS CON NOTAS NOTABLES (ENTRE 15 Y 20)";
cout <<"\n\n\Indique la cantidad de notas que ingresara: " ; cin>>cantidad_notas;

for (id_nota=1; id_nota<=cantidad_notas; id_nota++)
	{
		cout <<"\n\Nota N� " <<id_nota<<" : "; cin>>nota;
		if (nota>=15) 
			{
			if (nota<=20) cuenta_notables=cuenta_notables+1;
			}
	}
cout <<"\n\La cantidad de alumnos con notas notables: "<<cuenta_notables;
getch();
}


