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



/**
 *
 * @author Edu
 */



public class TablaSimb{
    private TablaSimb superior;
    private Vector inferior;
    private String nombre;
    private HashMap tabla;
    private int desplazamiento = 0;
    //Para las tablas de los metodos indica si ya ha aparecido la sentencia return.
    private boolean existeReturn;

     public TablaSimb(TablaSimb superior, String nombre){
        tabla = new HashMap();
        this.nombre = nombre;
        this.superior = superior;
        inferior = new Vector();
        desplazamiento = 0;
    }

     public TablaSimb(){
        inferior = new Vector();
        tabla = new HashMap();
        desplazamiento = 0;
     }

     public String getNombre(){
        return nombre;
    }

     public TablaSimb getSuperior(){
        return superior;
    }

      public Vector getInferior(){
        return inferior;
    }

     public HashMap getTabla(){
        return tabla;
    }

    public boolean getExisteReturn(){
        return existeReturn;
    }

     public void AnadirHijo(TablaSimb hijo){
         inferior.add(hijo);
     }

     public int getDesplazamiento(){
         return desplazamiento;
     }

     public void setNombre(String nombre){
         this.nombre = nombre;
     }

     public void setSuperior(TablaSimb sup){
         superior = sup;
     }

      public void setInferior(Vector inf){
         inferior = inf;
     }

      public void setTabla(HashMap tabl){
         tabla = tabl;
     }

      public void setDesplazamiento(int desp){
         desplazamiento = desplazamiento + desp;
     }

      public void setExisteReturn(boolean retorno){
          existeReturn = retorno;
      }


}
