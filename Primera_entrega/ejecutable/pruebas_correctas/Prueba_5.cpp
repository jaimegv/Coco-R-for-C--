//Prueba 5
//Palabras reservadas class, private, public, char, void, cout, int, return
//Tokens { } * ( ) :: : ; << .
//Identificadores Empleado, m_nombre, ImprimirInfo, main, 

class Empleado {
    private:
        char* m_nombre;

    public:
        void ImprimirInfo();
};



void Empleado::ImprimirInfo( )
{
   cout << "Nombre: " << m_nombre;
}


int main()
{
    //creacion de un objeto de la clase Empleado
    Empleado empleado12;

    //impresion de los datos
    empleado12.ImprimirInfo();
    return 0;
}
