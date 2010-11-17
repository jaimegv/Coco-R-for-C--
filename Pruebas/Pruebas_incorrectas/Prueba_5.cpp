//Prueba 5. Prueba de errores a nivel sintáctico. Intento de poner un identificador que es palabra reservada (línea 25)
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
    Empleado if;

    //impresion de los datos
    empleado12.ImprimirInfo();
    return 0;
}
