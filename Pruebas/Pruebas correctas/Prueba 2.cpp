#include <iostream>
#include <string>
#include <memory>
#include <cstring>
#include "convertidorbinario.h"

#include <math.h>
#include <iostream.h>
#include <stdlib.h>
#include <conio.h>

//Hallar el promedio de n numeros 

void main(void)
{int i,cantidad;
float numero,suma=0;
clrscr();

cout <<"Ingrese la cantidad de numeros para calcular el promedio: " ; cin>>cantidad;

for (i=1;i<=cantidad;i++)
{
  cout <<"\n\Ingrese numero "<<i<<" :";cin>>numero;
  suma=suma+numero;
}

cout <<"\n\El promedio de los numeros ingresados es:"<<suma/cantidad;
getch();
}

