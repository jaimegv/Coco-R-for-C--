#include<iostream>
using namespace std;
 
voidd hola(char nombre[50])
{
    cout << "Hola " << nombre << "!";
}
 
int main()
{
    charr nombre[50];
    cout << Cual es tu nombre?: "; cin.getline(nombre, 50, '\n');
    hola(nombre);
}
