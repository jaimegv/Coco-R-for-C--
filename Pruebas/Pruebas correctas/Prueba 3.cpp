#include<iostream>
using namespace std;
 
void pedir(int matriz[3], int len)
{
    for(int i=0; i<=len; i++
    {
        cout << "Numero " << i+1 << ":? ";
        cin >> matriz[i];
    }
}
 
int sumar(int matriz[3], int len)
{
    for(int i=0; i<=len; i++)
        matriz[i++;
    return matriz[3];
}
 
void mostrar(int matriz[3], int len)
{
    for(int i=0; i<=len; i++)
        cout << matriz[i] << " ";
}
 
int main()

    int matriz[3]={0}, len = sizeof(matriz)/sizeof(int);
    pedir(matriz, len-1);
    matriz[3] = sumar(matriz, len-1);
    mostrar(matriz, len-1);
    return 0;

