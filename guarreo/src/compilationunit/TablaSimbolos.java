/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilationunit;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;


/**
 *
 * @author Edu
 */
public class TablaSimbolos {


 //HashMap que contiene todas las clases y variables globales a ellas
 TablaSimb abuelo;

 //Vector que contiene todas las tuplas de tipo TablaSimb padres(clases)
 Vector  padres;

 //Vector que contiene todas las tuplas de tipo TablaSimb hijos (métodos)
 Vector hijos;

 //puntero a la TablaSimb actual.
 TablaSimb t_actual;

 private boolean existeMain = false;



  public TablaSimbolos(){
        abuelo = new TablaSimb();
        padres = new Vector();
        hijos = new Vector();
        abuelo.setNombre("Abuelo");
        t_actual = abuelo;
    }



  public void DestruirTSG(){
        abuelo = null;
        padres = null;
        hijos = null;
        t_actual = null;
    }


  //Busca un lexema en la t_actual
  public Simbolo Buscar_actual(String lexema){
    Simbolo simbolo;
    simbolo = (Simbolo) (t_actual.getTabla().get(lexema));
    return simbolo;
  }


  //Busca un lexema en el padre de la actual
public Simbolo BuscarPadre(String lexema){
    Simbolo simbolo;
    simbolo = (Simbolo) (t_actual.getSuperior().getTabla().get(lexema));
    return simbolo;
  }



//Busca una tabla de nivel 2 cuyo nombre sea lexema
//Al declarar un nuevo objeto de una clase[Avion Boeing = new Avion();]
//guardamos una entrada en la tabla abuelo que se llama Boeing pero
//que también dice que su clase relacionada es Avion.
//BuscarTio usa la clase relacionada para buscar la tabla en el nivel
//de los padres.
//AL CONTRARIO QUE OTROS BUSCAR ESTE DEVUELVE LA TABLA!!!
public TablaSimb BuscarTio(String lexema){
    TablaSimb tio;
    try{
        for (int i=0;i< padres.size();i++){
            tio = (TablaSimb) padres.get(i);
            if (tio.getNombre().equals(lexema))
                return tio;
        }
        return null;
    }catch(NullPointerException e){
              System.err.println("BuscarTio ha fallado");
              return null;
    }

}

  //Busca un lexema en la tabla actual, si no se encuentra ahí busca en la
  //tabla padre.
  public Simbolo Buscar(String lexema){
      Simbolo simbolo;
      simbolo = Buscar_actual(lexema);
      if (simbolo == null){
          try{
          simbolo = (Simbolo) (t_actual.getSuperior().getTabla().get(lexema));
          }catch(NullPointerException e){
              return null;
          }
      }
      return simbolo;
}

public boolean existeId (String lexema) {
      Simbolo simba;
      boolean existe = false;

      simba = Buscar (lexema);
      if (simba == null) {
          existe = false;
      }
      else {
          existe = true;
      }
      return existe;
  
  }


  //Busca un lexema en la tabla que se le indique
  public Simbolo BuscarEn(TablaSimb tabla, String lexema){
      Simbolo simbolo;
      try{
      simbolo = (Simbolo) tabla.getTabla().get(lexema);
      }catch(NullPointerException e){
          return null;
      }
      return simbolo;
  }

  //Busca directamene en la tabla abuelo, devuelve null si no encuentra nada.
  public Simbolo BuscarAbuelo(String lexema){
      Simbolo simbolo;
      simbolo = (Simbolo) abuelo.getTabla().get(lexema);
      return simbolo;
  }
  
  //Dado un ident de un metodo, te busca la tabla correspondiente de nivel 3
  // de ese metodo.
  public TablaSimb BuscarEnHijos(String lexema){
      TablaSimb hijo;
      try{
          for (int i=0;i< hijos.size();i++){
              hijo = (TablaSimb) hijos.get(i);
              if (hijo.getNombre().equals(lexema))
                  return hijo;
          }
          return null;
      }catch(NullPointerException e){
          System.err.println("BuscarEnHijo ha fallado");
          return null;
      }
  }

  //Inserta un nuevo símbolo en la tabla actual.
  //si el simbolo ya existía devuelve -1
  public int InsertarSimbolo(Simbolo simbolo,String id){
      if (Buscar_actual(id) == null){
        int cop = simbolo.getTamano();
        simbolo.setTamano(t_actual.getDesplazamiento());
        t_actual.setDesplazamiento(cop);
        t_actual.getTabla().put(id, simbolo);
          return 0;
      }else
          return -1;
  }

  //Inserta un nuevo símbolo en la tabla actual.
  //si el simbolo ya existía devuelve -1
  public int InsertarSimboloEn(Simbolo simbolo,String id, TablaSimb tio){
      if (BuscarEn(tio, id) == null){
        tio.getTabla().put(id, simbolo);
          return 0;
      }else
          return -1;
  }


  //Inserta una nueva tabla en el vector padres al encontrar
  //la palabra class, el nombre será el que acompañe a class
  //ACTUALIZA T_ACTUAL APUNTANDO A LA NUEVA TABLA
public TablaSimb CrearTablaPadre(String nombre){
    TablaSimb tabla = new TablaSimb(abuelo, nombre);
    padres.add(tabla);
    t_actual = tabla;
    return tabla;
}

//Inserta una nueva tabla en el vector hijos al encontrar
public TablaSimb CrearTablaHijo(String nombre){
    TablaSimb tabla = new TablaSimb(t_actual, nombre);
    hijos.add(tabla);
    t_actual = tabla;

    return tabla;
}

public boolean getExisteMain(){
        return existeMain;
    }

public void setExisteMain(boolean main){
         existeMain = main;
    }

public void AnadirArgMetodo(LinkedList cola){
    LinkedList cola_aux = cola;
    Argumento arg;
    for (int i=0;i< cola_aux.size();i++){
        arg = (Argumento) cola_aux.poll();
        Simbolo simbolo = new Simbolo(arg.getNombre(),arg.getTipo());
        InsertarSimbolo(simbolo,arg.getNombre());
    }


}

//creamos un metodo para buscar el abuelo (TS principal)
public TablaSimb getAbuelo (){
    return this.abuelo;
}

public char ObtenerTipo (Simbolo simba){

    char tipe;
    tipe = simba.getTipo();
    return tipe;
}

public String ObtenerIds (Simbolo simba){

    String identificador;
    identificador = simba.getId();
    return identificador;
}

public int ObtenerDesplazamiento(Simbolo simba){

    int desplazamiento;
    desplazamiento = simba.getTamano();
    return desplazamiento;
}




}
