#include<iostream>
#include<math.h>
using namespace std;

class Empleado {
    privatee:
        char* m_nombre;
        char* m_departamento;
        char* m_posicion;
        long m_salario;

    publicc:
        void ImprimirInfo();
        void SetNombre( char* nombre ) { m_nombre = nombre; }
        void SetDepartamento( char * departamento) { m_departamento = departamento; }
        void SetPosicion ( char* posicion ) { m_posicion = posicion; }
        void SetSalario ( long salario ) { m_salario = salario; }
        const char* GetNombre( ){ return m_nombre; }
        const char* GetDepartamento( ){ return m_departamento; }
        const char* GetPosicion( ){ return m_posicion; }
        const long GetSalario( ){ return m_salario; }
};



void Empleado::ImprimirInfo( )
{
   cout << "Nombre: " << m_nombre << '\n';
   cout << "Departamento: " << m_departamento << '\n';
   cout << "Puesto: " << m_posicion << '\n';
   cout << "Salario: " << m_salario << '\n';
}


int main()
{
    //creacion de un objeto de la clase Empleado
    Empleado empleado12;
   
    //asignacion de valores a las variables miembro
    empleado12.SetNombre("Jose");
    empleado12.SetDepartamento("Sistemas");
    empleado12.SetPosicion("Programador");
    empleado12.SetSalario(3000000);

    //impresion de los datos
    empleado12.ImprimirInfo();
    return 0;
}
