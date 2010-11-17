//Prueba 4
// Palabras reservadas 

int main()
{
    int a, b, c, A, B, C;

    cout << "Introduce lado a: "; cin >> a;
    cout << "Introduce lado b: "; cin >> b;
    cout << "Introduce lado c: "; cin >> c;

    A = a * 2;
    B = b / 4;
    C = c % 3;
    A += 3;
    B -= 1;
    C *= 2

    if(A == 90 || B == 90 || C == 90)
        cout << "coquito";
    if(A < 90 && B < 90 && C < 90)
        cout << "Los 3 son menores que 90";
    if(A > 90 || B > 90 || C > 90)
        cout << "Hay alguno mayor que 90";
}
