/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilationunit;



import java.util.*;
import java.util.Queue;
/**
 *
 * @author Edu
 */
public class Simbolo {

    //Comunes a todos los símbolos
private String id;



/*'e' entero, 'c' constante entera, 's' constante cadena,
 *'v' vector,'m' metodo,'p' palabra reservada, 'n' void,
 * 'x' clase, 'b' boolean, 'a' operador aritmetico,
 * 'l' operador logico, 'j' operador booleano, 'o' tipo objeto.
 */
private char tipo;
private int tamano;//tamaño
private int linea;
//Se puede extraer del token actual (la)
private int col;
//Equivale a la posición del token desde el inicio del código fuente.
private int pos;

//Especificaos de algunos.

//numargmetodo tambien sirve señala el tamaño del vector para los token tipo vector.
private int numargumetodo = 0;
private char tipoenvector;
private char tiporetornometodo;
//private int desplazamiento; Para qué quiero esto?? el desplazamiento es de la tabla
private boolean inicializado = false;
private LinkedList<Argumento> colaArgumentos = new LinkedList<Argumento>();
private String claserelacionada;
private boolean publico = false;

public Simbolo(String identificador, char tipo){
   id = identificador;
   this.tipo = tipo;
}


public Simbolo(String identificador, char tipo, int linea){
   id = identificador;
   this.tipo = tipo;
   this.linea = linea;
}

public Simbolo(){
    //pilaArgumentos = new Queue();
  }


public void setId(String identificador){
    id = identificador;
}


public void setTipo(char type){
    tipo = type;
}

public void setLinea(int line){
    linea = line;
}

//desplazamiento desde el inicio del codigo fuente
public void setPos(int posicion){
    pos = posicion;
}

public void setCol(int columna){
    col = columna;
}

public void setTamano(int size){
    tamano = size;
}

public void setPublico(boolean publico){
    this.publico = publico;
}

public void setClaseRelacionada(String clase){
    claserelacionada = clase;
}

public void setTipoRetorno(char tipo){
    tiporetornometodo = tipo;
}

public void setTipoEnVector(char tipo){
    tipoenvector = tipo;
}

public void setNumArgMetodo(int numargmetod){
    numargumetodo = numargmetod;
}

public void setColaArgumentos(LinkedList colaaargmetod){
    colaArgumentos = colaaargmetod;
}

//public void setDesplazamiento(int despl){
//    desplazamiento = despl;
//}

public void setInicializado(boolean inicializ){
    inicializado = inicializ;
}

public boolean getInicializado(){
    return inicializado;
}

public String getId(){
    return id;
}

public char getTipo(){
    return tipo;
}

public int getLinea(){
    return linea;
}

public int getTamano(){
    return tamano;
}

public boolean getPublico(){
    return publico;
}

public char getTipoRetorno(){
    return tiporetornometodo;
}

public char getTipoEnVector(){
    return tipoenvector;
}

public int getNumArgMetodo(){
    return numargumetodo;
}

public String getClaseRelacionada(){
    return claserelacionada;
}

public LinkedList getColaArgumentos(){
    LinkedList clon;
    if (colaArgumentos != null){
    clon = (LinkedList<Argumento>) colaArgumentos.clone();
    return clon;
    }else
        return colaArgumentos;
}

public int getColumna(){
    return col;
}

public int getPosicion(){
    return pos;
}

}


//Define un argumento en la declaracion de un metodo.
class Argumento{
    char tipo;
    String nombre;

    public Argumento(char tipe, String name){
        tipo = tipe;
        nombre = name;
    }

    public char getTipo(){
    return tipo;
}
    public String getNombre(){
        return nombre;
    }


}
