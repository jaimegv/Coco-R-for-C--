// Programa OPP01.CPP
#include <iostream>
 
using std::cout;
using std::endl;
 
 
// Esto crea la clase CRender
class CRender {
public:
    char buffer[256];
    void m_Renderear(const char *cadena);
};
 
 
/* implementar m_Renderear() para la c;*/
void CRender::m_Renderear(const char *cadena)
{
    strcpy(buffer, cadena);//copia la cadena
    return;
}
 
 
int main (int argc, char **argv)
{
    // crear 2 objetos CRender
    CRender render1, render2;
 
    render1.m_Renderear("Inicializando el objeto render1");
    render2.m_Renderear("Inicializando el objeto render2");	
 
    cout << "buffer en render1: ";
    cout << render1.buffer << endl;   // tenemos acceso a buffer ya que es publico.
 
    cout << "buffer en render2: ";
    cout << render2.buffer << endl;
 
return (0);
}


