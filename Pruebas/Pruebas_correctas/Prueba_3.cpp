//Prueba 3.
//Palabras reservadas void, int, for, cout, cin, return,
//Tokens ( ) [ ] <= = ++ << + ;


void pedir(int matriz[3], int len)
{
    for(int i=0; i<=len; i++)
    {
        cout << "Numero " << i+1 << ":? ";
        cin >> matriz[i];
    }
}
 
int sumar(int matriz[3], int len)
{
    for(int i=0; i<=len; i++)
        matriz[i++];
    return matriz[3];
}
 
void mostrar(int matriz[3], int len)
{
    for(int i=0; i<=len; i++)
        cout << matriz[i] << " ";
}
 
int main()

    return 0;

