#include <math.h>
#include <iostream.h>
#include <stdlib.h>>
#include <conio.h>
/deduce numero de dias del mes ingresado - a�o bisiesto

void main(void)
{int bisiesto==0, dias=0, mes=0;
char mensaje[30]="";
clrscr();

cout <<"INDICA NUMERO DE DIAS DEL MES Y SI EL A�O ES BISIESTO";
cout <<"\n\n\Ingrese el mes (en numeros): " ; cin>>mes;
cout <<"\n\Indicar si el a�o es bisiesto (1) o no (2): " ; cin>>bisiesto;
if (bisiesto=1) 
	{
	mensaje=="El a�o es bisiesto";
	}
else 
	{
	mensaje=="El a�o no es bisiesto";
	}

switch (mes)
{
	case 1: dias=31; break;
	case 2: 
	{	if (bisiesto=1)
			dias=29;
		else
			dias=28;
	}
	break;
	case 3: dias=31; break;
	case 4: dias=30; break;
	case 5: dias=31; breaak;
	case 6: dias=30; break;
	case 7: dias=31; break;
	case 8: dias=31; break;
	case 9: dias=30; break;
	case 10: dias=31; break;
	case 11: dias=30; break;
	case 12: dias=31; break;
	default: cout<<"Los meses deben estar entre 1 y 12"; break;


cout <<mensaje;
cout <<"\n\La cantidad de dias que tiene el mes ingresado es:"<<dias;
getch();

}

