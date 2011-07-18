/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilationunit;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 *
 * @author Esther SM
 */
public class GCF {
    BufferedWriter bw;
    File archiEscri=null;
    String temporal;
    String operacion,op1,op2,op3;
    String etiquetasputs="";
    int num_param_actual = 0;
    int c_etiqueta;
    
    //constructor de la clase
    public GCF(LinkedList<tupla_Tercetos> colaTercetos, LinkedList<tupla_Tercetos> colaMain, TablaSimbolos tabla, String fichero) {
        
        int desp_total;  //variable para el desplazamiento total de las tablas de simbolos
        archiEscri= new File(fichero);
        tupla_Tercetos tupla_actual;
        String terceto_actual, metodo_actual;
        //cola para ir metiendo los metodos a los que se llama
        LinkedList<String> colaMetodos = new LinkedList<String> (); 
        Simbolo simbolo;
        TablaSimb tabla_aux;
        c_etiqueta = 0;
        
        //preparamos el fichero que contendra el codigo objeto
        try {
            bw= new BufferedWriter(new FileWriter(fichero));
        }catch (IOException e) {
             // TODO
        }
        
        //preparamos la tabla de simbolos para que t_actual apunte a la tabla del main
        tabla.t_actual = tabla.BuscarEnHijos("main");

        //inicializamos el codigo objeto y lo dejamos todo preparado para leer los
        //tercetos del main
        try {
            bw.write("ORG 0\n");
            // Inicializamos la pila al maximo puesto que es decreciente
            // y la guardamos en el puntero de pila
            bw.write ("MOVE #65535, .SP\n");
            bw.write ("MOVE .SP, .IX\n");
            
            /* creamos el RA de la clase que contiene el metodo principal, dejando
             * hueco para todos sus atributos, despues guardamos el IX, que apuntará
             * al primer atributo de la clase que contiene el metodo main
             * para luego poder acceder cogiendo el desplazamiento de la tabla
             * de simbolos */
            tabla_aux = tabla.t_actual.getSuperior();  //buscamos la tabla de la clase del metodo principal
            desp_total = tabla_aux.getDesplazamiento(); //cogemos el desp_total de la tabla de simbolos padre
            bw.write ("ADD #-" + desp_total + ", .SP\n"); //sumamos desp_total de la tabla de simbolos padre al SP
            bw.write("MOVE .A, .SP\n"); //actualizamos SP
            bw.write("PUSH .IX\n");  //guardamos el IX para saber donde empiezan los atributos de la tabla de simbolos padre
            bw.write ("MOVE .SP, .IX\n");  //actualizamos IX
            
            /* ahora empezamos a crear el RA del main, dejando hueco para sus
             * variables locales y temporales */
            bw.write("MAIN:\n"); //creamos la etiqueta para el metodo main
            // Ahora tenemos que dejar hueco para las variables locales del main
            // para que cuando le demos valor luego tengamos sitio para meterlo
            desp_total = tabla.t_actual.getDesplazamiento();
            bw.write ("ADD #-" + desp_total + ", .SP\n"); //sumamos desp_total al SP
            bw.write("MOVE .A, .SP\n"); //actualizamos SP
            
        } catch (IOException e) {
                // TODO
        }
         
        //ahora recorremos la cola de tercetos del main
        /* Por cada CALL que encontremos, metemos el nombre del metodo en la
         * cola de metodos para saber qué métodos hay que pasar a código final
         */
        while (!colaMain.isEmpty()) {
            tupla_actual = colaMain.removeFirst();
            terceto_actual = tupla_actual.getTerceto();
            this.separar(terceto_actual);
            if (operacion.compareTo("CALL") == 0){
                colaMetodos.add(op1);
            }
            this.traducir(tabla);
        }
        
        while (!colaTercetos.isEmpty()){
            tupla_actual = colaTercetos.removeFirst();
            terceto_actual = tupla_actual.getTerceto();
            this.separar(terceto_actual);
            this.traducir(tabla);
        
        }
         
        /* leo la cola de metodos y por cada metodo llamo a una funcion
         * que busca en la cola de tercetos los tercetos correspondientes a 
         * dicho metodo */
//        while (!colaMetodos.isEmpty()) {
//            metodo_actual = colaMetodos.removeFirst();
//            Buscar_En_ColaTercetos(colaTercetos, metodo_actual, tabla);
//        }
    
        //cierra el fichero de salida
        try {
            bw.write("mens1:     DATA \"Introduzca el numero:\" \n");
            bw.write("eol:            DATA \"\\n\"\n"+etiquetasputs);
            bw.write("valor_falso: DATA \"FALSE\"\n");
            bw.write("valor_verdad: DATA \"TRUE\"\n");
            bw.write("cadena_get: RES 1\n");
            bw.close();
            
        } catch (IOException e) {
                // TODO
        }
    }
   
  

    //esta funcion separa la linea en el operador y los operandos
    public void separar(String linea){
        int u= linea.indexOf(",");
        this.operacion=linea.substring(0,u); //cogemos la operación
        linea=linea.substring(u+1);
        
        u= linea.indexOf(",");
        op1=linea.substring(0,u);
        linea=linea.substring(u+1);

        u= linea.indexOf(",");
        op2=linea.substring(0,u);
        linea=linea.substring(u+1);

        op3=linea.substring(0,linea.indexOf("\n"));
}
    
    
    public void traducir(TablaSimbolos tabla){
        Tercetos terceto;
        int desp_arg1, desp_arg2, desp_resultado, desp_total;
        int indice_int, desp_vector1, desp_vector2, desp_indice1, desp_indice2;
        String objeto, atributo, ident, indice; //variables para la asignacion
        String etiqueta1, etiqueta2;
        Simbolo simbolo1, simbolo2, simbolo3;
        int valor;  //para dar valor a un temporal
        int aux_arg;
        boolean esta_en_padre1 = false;
        boolean esta_en_padre2 = false;  
        boolean es_vector1 = false;
        boolean es_vector2 = false;
        boolean es_objeto1 = false;
        boolean es_objeto2 = false;
        
        //SUMA - ENTENDIDO
        // terceto = Emite (+, op1, op2, op3)
        if ( operacion.compareTo("+") == 0 ){
            //System.out.println("suma");
            try {
                // cogemos los datos del resultado
                simbolo3 = tabla.Buscar_actual(op3); //buscamos sólo en la actual ya q es un temporal
                desp_resultado = simbolo3.getTamano();
                
                int u1= op1.indexOf(".");
                int u2 = op1.indexOf("[");
                int u3= op2.indexOf(".");
                int u4 = op2.indexOf("[");
                
                if (u1 != -1)
                    es_objeto1 = true;
                if (u2 != -1) //el operador 1 es un vector
                    es_vector1 = true;
                if (u3 != -1)
                    es_objeto2 = true;
                if (u4 != -1) //el operador 2 es un vector
                    es_vector2 = true;
                
                if (!es_objeto1 && !es_objeto2 && !es_vector1 && !es_vector2){
                    //hallamos los desplazamientos de los argumentos y del resultado
                    simbolo1 = tabla.Buscar_actual(op1); //buscamos en la tabla actual y en la del padre
                    simbolo2 = tabla.Buscar_actual(op2); //buscamos en la actual y padre
                    if (simbolo1 == null){
                        //si no se ha encontrado el operador en la tabla de simbolos
                        //es que está utilizando las variables del padre
                        simbolo1 = tabla.Buscar(op1);
                        desp_arg1 = simbolo1.getTamano();
                        esta_en_padre1 = true;
                    }
                    else {
                          desp_arg1 = simbolo1.getTamano();
                    }
                    if (simbolo2 == null){
                        //si no se ha encontrado el operador en la tabla de simbolos
                        //es que está utilizando las variables del padre
                        simbolo2 = tabla.Buscar(op2);
                        desp_arg2 = simbolo2.getTamano();
                        esta_en_padre2 = true;
                    }
                    else {
                            desp_arg2 = simbolo2.getTamano();
                    }
                    
                    if (!esta_en_padre1 && !esta_en_padre2){
                        bw.write("ADD #-" + desp_arg1 + "[.IX], #-" + desp_arg2 + "[.IX]\n"); //el res de la suma se queda en el registro acumulador
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                    }
                    else if (esta_en_padre1 && esta_en_padre2){
                        bw.write("MOVE #1[.IX], .IY\n");//apuntamos al reg de activacion del padre
                        bw.write("ADD #-" + desp_arg1 + "[.IY], #-" + desp_arg2 + "[.IY]\n"); //el res de la suma se queda en el registro acumulador
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                    }
                    else if (esta_en_padre1){
                        bw.write("MOVE #1[.IX], .IY\n");//apuntamos al reg de activacion del padre
                        bw.write("ADD #-" + desp_arg1 + "[.IY], #-" + desp_arg2 + "[.IX]\n"); //el res de la suma se queda en el registro acumulador
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                    }
                    else if (esta_en_padre2){
                        bw.write("MOVE #1[.IX], .IY\n");//apuntamos al reg de activacion del padre
                        bw.write("ADD #-" + desp_arg1 + "[.IX], #-" + desp_arg2 + "[.IY]\n"); //el res de la suma se queda en el registro acumulador
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                    }
                }
                else{
                    if (es_objeto1){
                    }
                    if (es_objeto2){
                    }
                    if (es_vector1 && es_vector2){
                        ident = op1.substring(0, u2); //contiene el ident del vector
                        op1 = op1.substring(u2+1);
                        u2 = op1.indexOf("]");
                        indice = op1.substring(0, u2); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);

                        if (simbolo1 == null){
                            simbolo1 = tabla.Buscar(ident);
                            esta_en_padre1 = true;
                            desp_vector1 = simbolo1.getTamano();
                        }
                        else {
                            desp_vector1 = simbolo1.getTamano();
                        }

                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice1 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal

                        if (!esta_en_padre1){
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                        }
                        else{
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n"); 
                        }
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector1 +"\n");
                        bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar
                        
                        //ahora vamos con el operador 2
                        ident = op2.substring(0, u4); //contiene el ident del vector
                        op2 = op2.substring(u4+1);
                        u4 = op2.indexOf("]");
                        indice = op2.substring(0, u4); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);

                        if (simbolo1 == null){
                            simbolo1 = tabla.Buscar(ident);
                            esta_en_padre2 = true;
                            desp_vector2 = simbolo1.getTamano();
                        }
                        else {
                            desp_vector2 = simbolo1.getTamano();
                        }

                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice2 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal

                        if (!esta_en_padre2){
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                        }
                        else{
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n"); 
                        }
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector2 +"\n");
                        bw.write("MOVE .A, .r6\n"); //r6 contiene la direccion del elemento del vector
                        
                        // en r5 tenemos el op1 y en r6 el op2
                        bw.write("ADD [.r5], [.r6]\n"); //el res de la suma se queda en el registro acumulador
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                    }

                    if (es_vector1 && !es_vector2){
                        ident = op1.substring(0, u2); //contiene el ident del vector
                        op1 = op1.substring(u2+1);
                        u2 = op1.indexOf("]");
                        indice = op1.substring(0, u2); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);

                        if (simbolo1 == null){
                            simbolo1 = tabla.Buscar(ident);
                            esta_en_padre1 = true;
                            desp_vector1 = simbolo1.getTamano();
                        }
                        else {
                            desp_vector1 = simbolo1.getTamano();
                        }

                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice1 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal

                        if (!esta_en_padre1){
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                        }
                        else{
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n"); 
                        }
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector1 +"\n");
                        bw.write("MOVE .A, .r5\n"); //r6 contiene la direccion del elemento del vector
                        
                        //ahora el operador 2
                        simbolo1 = tabla.Buscar_actual(op2); //buscamos en la tabla actual y en la del padre
                        if (simbolo1 == null){
                            //si no se ha encontrado el operador en la tabla de simbolos
                            //es que está utilizando las variables del padre
                            simbolo1 = tabla.Buscar(op2);
                            desp_arg2 = simbolo1.getTamano();
                            esta_en_padre2 = true;
                        }
                        else {
                            desp_arg2 = simbolo1.getTamano();
                        }
                        
                        if (!esta_en_padre2){
                            bw.write("ADD [.r5], #-" + desp_arg2 + "[.IX]\n"); //el res de la suma se queda en el registro acumulador
                        }
                        else{
                            bw.write("MOVE #1[.IX], .IY\n");//apuntamos al reg de activacion del padre
                            bw.write("MOVE #-" + desp_arg2 + "[.IY], .r5\n"); //metemos en r6 el desplazamiento dentro del vector
                        }
                        
                        bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado

                    }
                    if (!es_vector1 && es_vector2){
                        //hallamos los desplazamientos de los argumentos y del resultado
                        simbolo1 = tabla.Buscar_actual(op1); //buscamos en la tabla actual y en la del padre
                        if (simbolo1 == null){
                            //si no se ha encontrado el operador en la tabla de simbolos
                            //es que está utilizando las variables del padre
                            simbolo1 = tabla.Buscar(op1);
                            desp_arg1 = simbolo1.getTamano();
                            esta_en_padre1 = true;
                        }
                        else {
                            desp_arg1 = simbolo1.getTamano();
                        }
                        
                        //ahora cogemos el operador 2
                        ident = op2.substring(0, u4); //contiene el ident del vector
                        op2 = op2.substring(u4+1);
                        u4 = op2.indexOf("]");
                        indice = op2.substring(0, u4); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);

                        if (simbolo1 == null){
                            simbolo1 = tabla.Buscar(ident);
                            esta_en_padre2 = true;
                            desp_vector2 = simbolo1.getTamano();
                        }
                        else {
                            desp_vector2 = simbolo1.getTamano();
                        }

                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice2 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal
                        
                        //generamos el codigo
                        if (!esta_en_padre2){
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                        }
                        else{
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n"); 
                        }
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector2 +"\n");
                        bw.write("MOVE .A, .r6\n"); //r6 contiene la direccion del elemento del vector 2
                        
                        if (esta_en_padre1){
                            bw.write("MOVE #1[.IX], .IY\n");//apuntamos al reg de activacion del padre
                            bw.write("ADD #-" + desp_arg1 + "[.IY], [.r6]\n"); //el res de la suma se queda en el registro acumulador
                            bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado
                        }
                        else{
                            bw.write("ADD #-" + desp_arg1 + "[.IX], [.r6]\n"); //el res de la suma se queda en el registro acumulador
                            bw.write("MOVE .A, #-" + desp_resultado + "[.IX]\n");  //pasamos lo del acumulador al resultado                        }
                        }
                    }
                }
            }
            catch (IOException e) {
             // TODO
            }
        }
      
        //ASIGNACIONES CUANDO EN LA PARTE DCHA HAY UN STRING - ENTENDIDO
        //terceto = Emite (ASIGNA, opIzda, opDcha)
        /*hay que hacer varias opciones, la parte izquierda de la expresion puede
         * ser:  ident, ident.ident, , ident[num] */
        else if ( operacion.compareTo("ASIGNA") == 0 ){
            try {
                //primero tratamos los casos del operador 1
                int u1= op1.indexOf(".");
                int u2 = op1.indexOf("[");
                
                // El elemento de la izquierda es un objeto
                if (u1 != -1){ //si ha encontrado un . entonces estamos en el caso ident.ident
                    objeto = op1.substring(0, u1); //contiene el primer ident, q es un objeto
                    atributo = op1.substring(u1+1); //contiene un atributo de un objeto (segundo ident)
                    desp_arg1 = 4; //PARA QUE COMPILE
                    
                    //FALTA BUSCAR LOS DESPLAZAMIENTOS Y PONER EL CODIGO MAQUINA
                    
                }
                // El elemento de la izquierda es un vector
                else if (u2 != -1){  //si ha encontrado un [ entonces estamos en el caso del vector
                    ident = op1.substring(0, u2); //contiene el ident del vector
                    op1 = op1.substring(u2+1);
                    u2 = op1.indexOf("]");
                    indice = op1.substring(0, u2); //contiene el indice del vector
                    simbolo1 = tabla.Buscar_actual(ident);
                    
                    if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos
                    //es que está utilizando las variables del padre
                        simbolo1 = tabla.Buscar(ident);
                        esta_en_padre1 = true;
                        desp_vector1 = simbolo1.getTamano();
                    }
                    else {
                        desp_vector1 = simbolo1.getTamano();
                    }
                    //buscamos el desplazamiento del temporal que contiene el indice del vector
                    simbolo1 = tabla.Buscar_actual(indice);
                    desp_indice1 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal
                    
                    int u3= op2.indexOf(".");
                    int u4 = op2.indexOf("[");
                    
                    // El elemento de la derecha es un objeto
                    if (u3 != -1){ //si ha encontrado un . entonces estamos en el caso ident.ident
                        objeto = op2.substring(0, u3); //contiene el primer ident, q es un objeto
                        atributo = op2.substring(u3+1); //contiene un atributo de un objeto (segundo ident)
                                            
                        //FALTA BUSCAR LOS DESPLAZAMIENTOS Y PONER EL CODIGO MAQUINA
                    
                    }
                    
                    // El elemento de la derecha es un vector
                    else if (u4 != -1){  //si ha encontrado un [ entonces estamos en el caso del vector
                        ident = op2.substring(0, u4); //contiene el ident del vector
                        op2 = op2.substring(u4+1);
                        u4 = op2.indexOf("]");
                        indice = op2.substring(0, u4); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);
                        
                        if (simbolo1 == null){
                            //si no se ha encontrado el operador en la tabla de simbolos
                            //es que está utilizando las variables del padre
                            simbolo1 = tabla.Buscar(ident);
                            desp_vector2 = simbolo1.getTamano();
                            esta_en_padre2 = true;
                        }
                        else {
                            desp_vector2 = simbolo1.getTamano();
                        }
                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice2 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal
                        
                        //generamos codigo
                        if (!esta_en_padre1 && !esta_en_padre2){
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar

                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r6\n"); //contiene la direccion del elemento q vamos a modificar

                            bw.write("MOVE [.r6], [.r5]\n");
                        }
                        else if (esta_en_padre1 && esta_en_padre2){
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar
                            
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r6\n"); //contiene la direccion del elemento q vamos a modificar

                            bw.write("MOVE [.r6], [.r5]\n");
                            
                            esta_en_padre1 = false;
                            esta_en_padre2 = false;
                        }
                        else if (esta_en_padre1){ //op1 esta en padre y op2 esta en hijo
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar
                            
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r6\n"); //contiene la direccion del elemento q vamos a modificar

                            bw.write("MOVE [.r6], [.r5]\n");
                            
                            esta_en_padre1 = false;
                        }
                        else{ //op2 esta en padre y op1 en hijo
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar
                            
                            bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r6\n"); //contiene la direccion del elemento q vamos a modificar

                            bw.write("MOVE [.r6], [.r5]\n");
                            
                            esta_en_padre2 = false;
                        }
                        
                    }
                    
                    // el elemento de la derecha es un ident
                    else { //estamos en el caso de ident
                        //hallamos los desplazamientos del argumento y del resultado
                        simbolo2 = tabla.Buscar_actual(op2);
                        if (simbolo2 == null){
                            //si no se ha encontrado el operador en la tabla de simbolos
                            //es que está utilizando las variables del padre
                            simbolo2 = tabla.Buscar(op2);
                            desp_arg2 = simbolo2.getTamano();
                            esta_en_padre2 = true;
                        }
                        else {
                            desp_arg2 = simbolo2.getTamano();
                        }
                        if (!esta_en_padre1 && !esta_en_padre2){
                            //generamos codigo
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IX],[.r3]\n");
                        }
                        else if (esta_en_padre1 && esta_en_padre2){
                            //generamos codigo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IY],[.r3]\n");
                            esta_en_padre1 = false;
                            esta_en_padre2 = false;
                        }
                        else if (esta_en_padre1){
                            //generamos codigo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IX],[.r3]\n");
                            esta_en_padre1 = false;
                        }
                        else{ //op2 en padre y op1 en hijo
                            //generamos codigo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector1 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IY],[.r3]\n");
                            esta_en_padre2 = false;
                        }
                    
                       
                    }
                }
                
                //el elemento de la izquierda es un ident
                else { //estamos en el caso de ident
                    //hallamos los desplazamientos del argumento y del resultado
                    simbolo1 = tabla.Buscar_actual(op1); //cogemos el operador de la izquierda de la asignacion
                    if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos
                    //es que está utilizando las variables del padre
                        simbolo1 = tabla.Buscar(op1);
                        desp_arg1 = simbolo1.getTamano();
                        esta_en_padre1 = true;
                    }
                    else {
                        desp_arg1 = simbolo1.getTamano();
                    }
                    
                    int u3= op2.indexOf(".");
                    int u4 = op2.indexOf("[");
                    
                    // El elemento de la derecha es un objeto
                    if (u3 != -1){ //si ha encontrado un . entonces estamos en el caso ident.ident
                        objeto = op2.substring(0, u3); //contiene el primer ident, q es un objeto
                        atributo = op2.substring(u3+1); //contiene un atributo de un objeto (segundo ident)
                        //FALTA BUSCAR LOS DESPLAZAMIENTOS Y PONER EL CODIGO MAQUINA
                    
                    }
                    // El elemento de la derecha es un vector
                    else if (u4 != -1) {
                        ident = op2.substring(0, u4); //contiene el ident del vector
                        op2 = op2.substring(u4+1);
                        u4 = op2.indexOf("]");
                        indice = op2.substring(0, u4); //contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(ident);
                        
                        if (simbolo1 == null){
                            //si no se ha encontrado el operador en la tabla de simbolos
                            //es que está utilizando las variables del padre
                            simbolo1 = tabla.Buscar(ident);
                            desp_vector2 = simbolo1.getTamano();
                            esta_en_padre2 = true;
                        }
                        else {
                            desp_vector2 = simbolo1.getTamano();
                        }
                        //buscamos el desplazamiento del temporal que contiene el indice del vector
                        simbolo1 = tabla.Buscar_actual(indice);
                        desp_indice2 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal
                        
                        if (!esta_en_padre1 && !esta_en_padre2){
                            //generamos codigo
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE [.r3], #-" + desp_arg1 + "[.IX]\n");
                        }
                        else if (esta_en_padre1 && esta_en_padre2){
                            //generamos codigo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE [.r3], #-" + desp_arg1 + "[.IY]\n");
                            esta_en_padre1 = false;
                            esta_en_padre2 = false;
                        }
                        else if (esta_en_padre1){
                            //generamos codigo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IX, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE [.r3], #-" + desp_arg1 + "[.IY]\n");
                            esta_en_padre1 = false;
                        }
                        else{ //op2 en padre y op1 en hijo
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_indice2 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                            bw.write("MOVE .IY, .r4\n");  
                            bw.write("SUB .r4, .r3\n");//movemos el IY 
                            bw.write("SUB .A, #" + desp_vector2 +"\n");
                            bw.write("MOVE .A, .r3\n");
                            bw.write("MOVE [.r3], #-" + desp_arg1 + "[.IX]\n");
                            esta_en_padre2 = false;
                        }
                    }
                    
                    //el elemento de la derecho es un ident
                    else{
                        //hallamos los desplazamientos del argumento y del resultado
                        simbolo2 = tabla.Buscar_actual(op2);
                        if (simbolo2 == null){
                        //si no se ha encontrado el operador en la tabla de simbolos
                        //es que está utilizando las variables del padre
                            simbolo2 = tabla.Buscar(op2);
                            desp_arg2 = simbolo2.getTamano();
                            esta_en_padre2 = true;
                        }
                        else
                            desp_arg2 = simbolo2.getTamano();
                        
                        if (!esta_en_padre1 && !esta_en_padre2){
                            bw.write("MOVE #-" + desp_arg2 + "[.IX], #-" + desp_arg1 + "[.IX]\n");
                        }
                        else if (esta_en_padre1 && esta_en_padre2){
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IY], #-" + desp_arg1 + "[.IY]\n");
                        }
                        else if (esta_en_padre1){
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IX], #-" + desp_arg1 + "[.IY]\n");
                        }
                        else { //esta en padre el op2
                            bw.write("MOVE #1[.IX], .IY\n");
                            bw.write("MOVE #-" + desp_arg2 + "[.IY], #-" + desp_arg1 + "[.IX]\n");                            
                        }
                    }
                }
            }
            catch (IOException e){
            }
        }
        
        // DA VALOR A TEMPORAL - ENTENDIDO
        // terceto = Emite (TEMP, temp, valor)
        //para dar valor a un temporal justo antes de una asignacion del tipo
        // x = 1;
        else if (operacion.compareTo("TEMP") == 0){
            try {
                valor = Integer.parseInt(op2); //pasamos el op2 a entero
                simbolo1 = tabla.Buscar_actual(op1);
                desp_arg1 = simbolo1.getTamano(); //cogemos el desplazamiento del temporal
                bw.write("MOVE #" + valor + ", #-" + desp_arg1 + "[.IX]\n");
            }
            catch (IOException e){
                
            }
        }
        
        // ETIQUETA_METODO - ENTENDIDO
        // terceto = Emite (ETIQUETA_METODO, nombre_etiq)
        /* Va a crear una etiqueta de una funcion y va a añadir el codigo final
         * que va a ir justo despues de esa etiqueta, es decir, inicializa el SP,
         * deja hueco para las variables globales, guarda los parametros*/
        else if ( operacion.compareTo("ETIQUETA_METODO") == 0 ){
            try {
                //tenemos q actualizar la tabla de simbolos para que busque en la tabla de la funcion
                //que esta ejecutando ahora
                tabla.t_actual = tabla.BuscarEnHijos(op1);
                bw.write(op1 + ":\n"); //escribimos el nombre de la etiqueta
                bw.write("MOVE .SP,.IX\n");  //copiamos el puntero de pila al marco de pila para q apunte a los parametros
                desp_total = tabla.t_actual.getDesplazamiento(); //cogemos el desp total de la tabla
                bw.write("ADD #-" + desp_total + ", .SP\n"); //dejamos hueco para los param y variables locales
                bw.write("MOVE .A, .SP\n"); //metemos el nuevo valor al SP

                //copiamos los parametros en el registro de activacion de la nueva funcion para que 
                //luego esté todo seguido (param + var_locales + var_temp)
                aux_arg = num_param_actual;
                int aux_aux; //variable para saber de donde hay que coger el parametro
                int aux_aux2 = 0; //variable para saber donde hay que colocar el parametro
                while (aux_arg != 0){
                    aux_aux = 5 + aux_arg;
                    bw.write("MOVE #"+ aux_aux + "[.IX], #-" + aux_aux2 + "[.IX]\n");
                    aux_arg--;
                    aux_aux2 ++;
                }
            }
            catch (IOException e){
                
            }
        }
        // ETIQUETA - ENTENDIDO
        // Emite (ETIQUETA, nombre_etiqueta)
        // introduce una etiqueta en el codigo final
        else if (operacion.compareTo("ETIQUETA") == 0){
            try{
                bw.write(op1 + ":\n");
            }catch (IOException e){
                
            }
        }
        
        //SALTO INCONDICIONAL - ENTENDIDO
        // terceto = emite (GOTO, ident)
        else if ( operacion.compareTo("GOTO") == 0 ){
            try {
                bw.write("BR /"+op1+"\n");
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // RETURNop - ENTENDIDO - FALTA BUSCAR EL OPERADOR EN EL PADRE
        // terceto = Emite (RETURNop, ident)
        //vamos a tener dos tipos de return, uno para los procedimientos y otro
        //para las funciones (uno tiene operador y el otro no)
        
        //RETURNop = con operador
        //almacenamos en el valor devuelto del registro de activación
        // (desp 2), el valor que se encuentre en el registro de activación
        //con un desplazamiento igual al del argumento
        else if ( operacion.compareTo("RETURNop") == 0 ){
            try{
                //consigo el desplazamiento del operador
                simbolo1 = tabla.Buscar_actual(op1);
                if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos
                    //es que está utilizando las variables del padre
                     simbolo1 = tabla.Buscar(op1);
                     desp_arg1 = simbolo1.getTamano();
                     bw.write("MOVE #1[.IX], .IY\n");
                     bw.write("MOVE #-" + desp_arg1 + "[.IY], #2[.IX]\n");//metemos el resultado en el campo de valor devuelto
                }
                else {
                    desp_arg1 = simbolo1.getTamano();
                    bw.write("MOVE #-" + desp_arg1 + "[.IX], #2[.IX]\n");//metemos el resultado en el campo de valor devuelto
                }
                    
                //cogemos el desplazamiento total de la tabla
                desp_total = tabla.t_actual.getDesplazamiento();
                bw.write("ADD #" + desp_total + ", .SP\n"); //recuperamos el SP inicial para el ret
                bw.write("MOVE .A, .SP\n"); //metemos el nuevo valor al SP
                bw.write("RET\n");
            }
            catch (IOException e){
                
            }
         
        }
        
        //RETURN = return sin operador - ENTENDIDO
        // terceto = Emite (RETURN)
        else if (operacion.compareTo("RETURN")==0){
            try {
                //como no se devuelve nada, solo recuperamos el SP inicial y hacemos el RET
                desp_total = tabla.t_actual.getDesplazamiento();
                bw.write("ADD #" + desp_total + ", .SP\n"); //recuperamos el SP inicial para el ret
                bw.write("MOVE .A, .SP\n"); //metemos el nuevo valor al SP
                bw.write("RET\n");
            }
            catch (IOException e){
                
            }
        }
        
        //PARAM - ENTENDIDO
        // terceto = Emite (PARAM, ident)
        //mete en la pila el parametro indicado en "ident"
        else if ( operacion.compareTo("PARAM") == 0 ){
            try {
                //calculo el desplazamiento del parametro
                simbolo1 = tabla.Buscar_actual(op3);
                if (simbolo1 == null){
                    simbolo1 = tabla.Buscar(op3);
                    desp_arg1 = simbolo1.getTamano();
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("PUSH #-" + desp_arg1 + "[.IY]\n");
                }
                else{
                    desp_arg1 = simbolo1.getTamano();
                    //ahora guardamos los parametros de la funcion a la que vamos a llamar posteriormente
                    bw.write("PUSH #-" + desp_arg1 + "[.IX]\n");
                }
                
            }
            catch (IOException e){
                
            }
        }

        //CALL - ENTENDIDO 
        // terceto = Emite (CALL, nombre_funcion, num_param)
        //realiza una llamada a un metodo
        else if ( operacion.compareTo("CALL") == 0 ){
            try {
                //Guardamos los registros necesarios
                bw.write("PUSH .IX\n");  //guardamos el marco de pila
                bw.write("PUSH .r2\n");  //guardamos el puntero de acceso, q estará en el registro r2
                bw.write("PUSH .SR\n");  //guardamos el registro de estado
                bw.write("PUSH .r3\n");  //guardamos el registro donde estará el valor devuelto x la funcion a la q llama
                bw.write("CALL /" + op1 + "\n");  //realizamos llamada
                bw.write("POP .r3\n");  //sacamos el registro donde está el valor devuelto
                bw.write("POP .SR\n");  //Sacamos el registro de estado
                bw.write("POP .r2\n");  //Sacamos el puntero de acceso
                bw.write("POP .IX\n");  //Sacamos el puntero de pila y lo metemos en IX
                aux_arg = Integer.parseInt(op2);
                //actualizamos el num de parametros a la variable global
                num_param_actual = aux_arg;
                while (aux_arg != 0){
                    bw.write("POP .r1\n"); //Sacamos el argumento
                    aux_arg--;
                }
                bw.write("MOVE .IX, .SP\n"); //copiamos el marco de pila en el SP
            }
            catch (IOException e){
                
            }
        }
        
        // GUARDAres - ENTENDIDO
        // terceto = Emite (GUARDAres, temp)
        /* para las llamadas a funciones que devuelven algo, guarda el resultado
         * devuelto en la llamada que estará en el registro r3, en el temporal
         * que le pasamos como parámetro*/        
        else if (operacion.compareTo("GUARDAres") == 0){
            try {
                simbolo1 = tabla.Buscar_actual(op1);
                desp_arg1 = simbolo1.getTamano();
                bw.write("MOVE .r3, #-" + desp_arg1 + "[.IX]\n"); //lo guardamos en el temporal correspondiente
            }
            catch (IOException e){
                
            }
        }
        
        // TERMINA - ENTENDIDO
        // terceto = Emite (TERMINA)
        /* terceto para cuando termina el metodo main*/
        else if (operacion.compareTo("TERMINA") == 0) {
            try {
                bw.write("HALT\n");
            }
            catch (IOException e){
                
            }
        }

        
        //NEGACION - ENTENDIDO
        //Emite (NOT, ident, temp)
        //realiza la negacion del operador ident y mete el resultado en el temporal
        else if ( operacion.compareTo("NOT") == 0 ){
            try {
                simbolo1 = tabla.Buscar_actual(op1);
                if (simbolo1 == null){
                    simbolo1 = tabla.Buscar(op1);
                    desp_arg1 = simbolo1.getTamano();
                    esta_en_padre1 = true;
                }
                else
                    desp_arg1 = simbolo1.getTamano();  //desplazamiento de ident
                    
                simbolo2 = tabla.Buscar_actual(op2);
                desp_arg2 = simbolo2.getTamano();  //desplazamiento del temporal
                
                if (esta_en_padre1){
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("MOVE #-" + desp_arg1 + "[.IY],.r7\n"); //r7=op1
                    esta_en_padre1 = false;
                }
                else
                    bw.write("MOVE #-" + desp_arg1 + "[.IX],.r7\n"); //r7=op1
                
                bw.write("MOVE #0,.r8\n");
                bw.write("CMP .r7,.r8\n");  //comparamos los dos operadores
                etiqueta1 = darEtiqueta("CAMBIAR_A_TRUE");
                bw.write("BZ /" + etiqueta1 + "\n"); //Si es 0, tenemos false asi q cambiamos a true
                bw.write("MOVE #0,.r9\n"); //metemos false en r9
                etiqueta2 = darEtiqueta("SIGUE_NOT");
                bw.write("BR /" + etiqueta2 + "\n"); //salto incondicional a la etiqueta SIGUE_NOT
                bw.write(etiqueta1 + ": \n");
                bw.write("MOVE #1,.r9\n"); //cambiamos a true
                bw.write(etiqueta2 + ": \n");
                bw.write("MOVE .r9,#-" + desp_arg2 + "[.IX]\n"); //paso el resultado al temporal
            }
            catch (IOException e) {
            }
        }
        // MENOR QUE - ENTENDIDO
        // Emite (<, op1, op2, temp)
        else if (operacion.compareTo("<") == 0){
            try {
                //hallamos los desplazamientos de los argumentos y del resultado
                simbolo1 = tabla.Buscar_actual(op1); //buscamos en la tabla actual
                simbolo2 = tabla.Buscar_actual(op2); //buscamos en la actual
                simbolo3 = tabla.Buscar_actual(op3); //buscamos sólo en la actual ya q es un temporal
                if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos actual
                    //es que está utilizando las variables del padre
                    simbolo1 = tabla.Buscar(op1);
                    desp_arg1 = simbolo1.getTamano();
                    esta_en_padre1 = true;
                }
                else {
                    desp_arg1 = simbolo1.getTamano();
                }
                if (simbolo2 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos actual
                    //es que está utilizando las variables del padre
                    simbolo2 = tabla.Buscar(op2);
                    desp_arg2 = simbolo2.getTamano();
                    esta_en_padre2 = true;
                }
                else {
                        desp_arg2 = simbolo2.getTamano();
                }
                desp_resultado = simbolo3.getTamano();
                
                if (esta_en_padre1){
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("MOVE #-" + desp_arg1 + "[.IY],.r7\n"); //r7=op1
                    esta_en_padre1 = false;
                }
                else
                    bw.write("MOVE #-" + desp_arg1 + "[.IX],.r7\n"); //r7=op1
                
                if (esta_en_padre2){
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("MOVE #-" + desp_arg2 + "[.IY],.r8\n"); //r8=op2
                    esta_en_padre2 = false;
                }
                else
                    bw.write("MOVE #-" + desp_arg2 + "[.IX],.r8\n"); //r8=op2
                
                bw.write("CMP .r7,.r8\n");  //comparamos los dos operadores
                etiqueta1 = darEtiqueta("ES_MENOR");
                bw.write("BN /" + etiqueta1 + "\n"); //si op1<op2 entonces vamos a etiqueta "es_menor"
                bw.write("MOVE #0,.r9\n");  //si no, metemos un 0 en r9
                etiqueta2 = darEtiqueta("SIGUE_MENOR_QUE");
                bw.write("BR /" + etiqueta2 + "\n"); //saltamos a la etiqueta SIGUE_MENOR_QUE
                bw.write(etiqueta1 + ":\n");
                bw.write("MOVE #1,.r9\n");  //metemos un 1 en r9
                bw.write(etiqueta2 + ":\n");
                bw.write("MOVE .r9, #-" + desp_resultado + "[.IX]\n"); //guardamos el resultado en el temporal
            }
            catch (IOException e) {
             // TODO
            }
        }
        
        // IF - ENTENDIDO
        // Emite (IF,ident,etiqueta)
        else if (operacion.compareTo("IF") == 0){
            simbolo1 = tabla.Buscar_actual(op1); //buscamos primero en la actual
            if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos actual
                    //es que está utilizando las variables del padre
                    simbolo1 = tabla.Buscar(op1);
                    desp_arg1 = simbolo1.getTamano();
                    esta_en_padre1 = true;
            }
            else {
                   desp_arg1 = simbolo1.getTamano();
            }
            try{
                if (!esta_en_padre1){
                    bw.write("MOVE #-" + desp_arg1 + "[.IX],.r7\n");
                    bw.write("MOVE #1,.r8\n");
                    bw.write("CMP .r7,.r8\n");
                    bw.write("BZ /" + op2 + "\n"); //si se cumple condicion saltamos a etiqueta
                }
                else{
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("MOVE #-" + desp_arg1 + "[.IY],.r7\n");
                    bw.write("MOVE #1,.r8\n");
                    bw.write("CMP .r7,.r8\n");
                    bw.write("BZ /" + op2 + "\n"); //si se cumple condicion saltamos a etiqueta
                    esta_en_padre1 = false;
                }
                  
            }
            catch (IOException e){
                
            }
        }
          
        
        //para el constructor  bh = new Bicicleta ();
        else if (operacion.compareTo("CONSTR") == 0){
            //activar el registro de activacion de la clase correspondiente
            // *************** FALTA!!! ********************************
            // **********************************************************
        }
        
        // READ - ENTENDIDO
        // Emite (READ, ident)
        // introduce lo leido de consola en la variable ident
        else if (operacion.compareTo("READ") == 0){
            try {
                simbolo1 = tabla.Buscar_actual(op1);  //buscamos primero en la actual
                if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos actual
                    //es que está utilizando las variables del padre
                    simbolo1 = tabla.Buscar(op1);
                    desp_arg1 = simbolo1.getTamano();
                    esta_en_padre1 = true;
                }
                else {
                   desp_arg1 = simbolo1.getTamano(); 
                }
                if (!esta_en_padre1){
                    bw.write("ININT /cadena_get\nMOVE /cadena_get, #-" + desp_arg1 + "[.IX]\n");
                }
                else{
                    bw.write("MOVE #1[.IX], .IY\n");
                    bw.write("ININT /cadena_get\nMOVE /cadena_get, #-" + desp_arg1 + "[.IY]\n");
                    esta_en_padre1 = false;
                }
            } catch (IOException e) {
                // TODO
            }
        }
        
        // PUT_CADENA - ENTENDIDO
        // Emite (PUT_CADENA, cadena)
        // Escribe en consola una cadena
        else if (operacion.compareTo("PUT_CADENA")==0){
            String axu=this.darEtiqueta("PUT");
            try {
                etiquetasputs=etiquetasputs+axu+":  DATA " +op1+"\n";
                bw.write("WRSTR /"+axu+"\n");
            } catch (IOException e) {
                // TODO
            }
        }
        
        // PUT_SALTO_LINEA - ENTENDIDO
        // Emite (PUT_SALTO_LINEA)
        // Hace un salto de linea en la consola
        else if (operacion.compareTo("PUT_SALTO_LINEA") == 0){
            try {
                bw.write("WRCHAR /eol\n");
            } catch (IOException e) {
                // TODO
            }
        }
        
        // PUT_EXPRESION - ENTENDIDO
        // Emite (PUT_EXPRESION, ident)
        // Escribe el valor almacenado en la variable ident
        else if (operacion.compareTo("PUT_EXPRESION") == 0){
            try {
                int u1= op1.indexOf("[");
                
                if (u1 != -1){ //si es un vector
                   ident = op1.substring(0, u1); //contiene el ident del vector
                   op1 = op1.substring(u1+1);
                   u1 = op1.indexOf("]");
                   indice = op1.substring(0, u1); //contiene el indice del vector
                   simbolo1 = tabla.Buscar_actual(ident); 
                   if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos
                    //es que está utilizando las variables del padre
                        simbolo1 = tabla.Buscar(ident);
                        esta_en_padre1 = true;
                        desp_vector1 = simbolo1.getTamano();
                   }
                   else {
                        desp_vector1 = simbolo1.getTamano();
                   }
                    //buscamos el desplazamiento del temporal que contiene el indice del vector
                   simbolo1 = tabla.Buscar_actual(indice);
                   desp_indice1 = simbolo1.getTamano(); //aqui tenemos el desplazamiento del temporal
                   
                   if (!esta_en_padre1){
                        bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                        bw.write("MOVE .IX, .r4\n");  
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector1 +"\n");
                        bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar 
                        bw.write("WRINT [.r5]\n");
                   }
                   else{ //si esta en el padre
                        bw.write("MOVE #1[.IX], .IY\n");//IY apunta al reg de activacion del padre
                        bw.write("MOVE #-" + desp_indice1 + "[.IX], .r3\n"); //metemos en r3 el desplazamiento dentro del vector
                        bw.write("MOVE .IY, .r4\n");  
                        bw.write("SUB .r4, .r3\n");//movemos el IY 
                        bw.write("SUB .A, #" + desp_vector1 +"\n");
                        bw.write("MOVE .A, .r5\n"); //r5 contiene la direccion del elemento q vamos a modificar
                        bw.write("WRINT [.r5]\n");
                   }
                   
                }
                else{ //si es un ident normal, no vector
                    simbolo1 = tabla.Buscar_actual(op1);  //buscamos primero en la actual
                    if (simbolo1 == null){
                        //si no se ha encontrado el operador en la tabla de simbolos actual
                        //es que está utilizando las variables del padre
                        simbolo1 = tabla.Buscar(op1);
                        desp_arg1 = simbolo1.getTamano();
                        esta_en_padre1 = true;
                    }
                    else {
                       desp_arg1 = simbolo1.getTamano(); 
                    }
                    if (!esta_en_padre1){
                        bw.write("WRINT #-" + desp_arg1 + "[.IX]\n");
                    }
                    else{
                        bw.write("MOVE #1[.IX], .IY\n");
                        bw.write("WRINT #-" + desp_arg1 + "[.IY]\n");
                        esta_en_padre1 = false;
                    }
                }
                
            } catch (IOException e) {
                // TODO
            }
        }
        
        //PUT_BOOLEANO - ENTENDIDO
        // Emite (PUT_BOOLEANO, ident)
        // Escribe un 0 si el valor de ident es false y un 1 si es true
        else if (operacion.compareTo("PUT_BOOLEANO") == 0){
            try {
                simbolo1 = tabla.Buscar_actual(op1);  //buscamos primero en la actual
                if (simbolo1 == null){
                    //si no se ha encontrado el operador en la tabla de simbolos actual
                    //es que está utilizando las variables del padre
                    simbolo1 = tabla.Buscar(op1);
                    desp_arg1 = simbolo1.getTamano();
                    
                    //BUSCAR POR LOS PUNTEROS DE ACCESO HASTA ENCONTRAR
                    //LA VARIABLE (BUSCAR EN EL PADRE)
                }
                else {
                   desp_arg1 = simbolo1.getTamano(); 
                }
                bw.write("MOVE #-" + desp_arg1 + "[.IX], .r7\n");//r7 contiene el valor de ident
                bw.write("cmp #0, .r7\n");
                etiqueta1 = darEtiqueta("ES_FALSO");
                bw.write("BZ /" + etiqueta1 + "\n");
                bw.write("WRSTR /valor_verdad\n");
                etiqueta2 = darEtiqueta("FIN_BOOLEAN");
                bw.write("BR /" + etiqueta2 + "\n");
                bw.write(etiqueta1 + ":\n");
                bw.write("WRSTR /valor_falso\n");
                bw.write(etiqueta2 + ":\n");
           
            } catch (IOException e) {
                // TODO
            }
        }
            
       
    }
    
    //Busco en la cola de tercetos los correspondientes al metodo que le pasamos como parametro
    //y lo traducimos a codigo final. Despues los vamos borrando.
    public void Buscar_En_ColaTercetos (LinkedList<tupla_Tercetos> colaTercetos, String metodo, TablaSimbolos tabla){
        Iterator<tupla_Tercetos> iterador = colaTercetos.iterator();
        tupla_Tercetos terceto_actual;
        while (iterador.hasNext()){
            terceto_actual = iterador.next();
            if (terceto_actual.getMetodo().compareTo(metodo) == 0){
                this.separar(terceto_actual.getTerceto());
                traducir(tabla);
            }
        }
    }
    
    //metodo para crear etiquetas para el menor que y el not
    public String darEtiqueta(String etiqueta){
        c_etiqueta = c_etiqueta + 1;
        return etiqueta + c_etiqueta;
    }
   

}
