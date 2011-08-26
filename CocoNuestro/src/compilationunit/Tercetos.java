package compilationunit;

public class Tercetos{
    //File fichero;
    //BufferedWriter bw;
    String union;
    String etiqueta ="etiqueta";
    String temporal ="$temporal";
    int c_etiqueta;
    int c_temporal;
    
    //public Tercetos(String nombre){
            //fichero= new File(nombre);
        public Tercetos(){
            c_etiqueta = 0;
            c_temporal = 0;
        /*    
        try {
            bw= new BufferedWriter(new FileWriter(nombre));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }
      
    public String darTemporal(){
        c_temporal = c_temporal + 1;
        return temporal + c_temporal;
    }
    
    public String darEtiqueta(){
        c_etiqueta = c_etiqueta + 1;
        return etiqueta + c_etiqueta;
    }
    
    public String EtiquetaMetodo(String etiqueta){
            return "ETIQUETA_METODO,"+etiqueta+",,\n";
    }
    
    public String EtiquetaSubprograma (String etiqueta){
        return "ETIQUETA_SUBPROGRAMA,"+etiqueta+",,\n";	// atentos q el main esta aqui dentro 
    }													// esta aqui dentro
    
    public String InsertarEtiqueta(String etiqueta){

            return "ETIQUETA,"+etiqueta+",,\n";
    }
    
    // Comienzan las funciones en las que hay que llamar a la funcion "generar"

    public String operacionBinaria(String op1,String op2,String op_binaria,String resultado){
       
                return op_binaria +"," +op1 +","+ op2+ ","+resultado+"\n";
                        
    }
    
    public String operacionUnaria(String op1, String operador, String resultado){

                return operador+","+op1+","+"1"+","+resultado+"\n";
                
    }
    
    public String asignacion_valor(String op1, int op2){

    return "ASIGNACION,"+op1+","+op2+",\n";
    }
    
    public String asignacion_cadena(String op1, String op2){
    	/*
    	 * Asignamos a una etiqueta, op1, el valor de la cadena op2
    	 */
        return "ASIGNACION_CAD,"+op1+","+op2+",\n";
    }
    
    public String asignacion(String op1, String op2){

    return "ASIGNA,"+op1+","+op2+",\n";
    }
    public String saltoIncondicional(String etiqueta){

                return "GOTO,"+etiqueta+",,\n";
    }
    
    public String saltoCondicional(String ident, String etiqueta){

                return "IF,"+ident+","+etiqueta+",\n";
    }
    
    public String retorno(String nombre){

        return "RETURNop,"+nombre+",,\n";
    }
    
    public String retornoSinOp(){

        return "RETURN,,,\n";
    }
    
    public String asignaArray(String op1,String nombre,int num){

        return "AARRAYNUN,"+op1+","+nombre+","+num+"\n";
    }
    
    public String asignaArray(String op1,String nombre,String indice){

        return "AARRAYIND,"+op1+","+nombre+","+indice+"\n";
    }
    
    public String meteEnArray(String op1,String nombre,int num){

        return "EN_ARRAY,"+op1+","+nombre+","+num+"\n";
    }
    
    public String meteEnArray(String op1,String nombre,String num){

        return "EN_ARRAY,"+op1+","+nombre+","+num+"\n";
    }

    // mete en el terceto la llamada a una funcion

//    public String funcion_en_terceto (String nombre, int num_par, String parametros [], String etiqueta){
//
//        return "CALL,"+nombre + ", " + num_par+", "+parametros+", "+etiqueta+",\n";
//    }
    public String funcion_en_terceto (String nombre, int num_par){

        return "CALL,"+nombre + "," + num_par+",\n";
    }
    
    // hace el PARAM de cada parametro
    public String parametro (String param){
        return "PARAM,,," + param + "\n";
    }

    // Obtiene del terceto todos los parametros de la llamada a la funcion

    public String[] call (String nombre, int num_par, String parametros[], String etiqueta){
                
                String resultado[] = new String[num_par + 1];
                int i = 0;
                for (; i<num_par; i++){
                    resultado[i] = "PARAM,"+parametros[i]+",,\n";
                }
                resultado[i] = "CALL,"+nombre+","+num_par+",\n";
                return resultado;
    }
    
    
    public String putCadena(String op1){
     
        return "PUT_CADENA,"+op1+",,\n";
    }
    
    public String putEntero(String op1){
     
        return "PUT_ENTERO,"+op1+",,\n";
    }
    
    public String putSaltoLinea(){
     
        return "PUT_SALTO_LINEA,,,\n";
    }
    
     public String putBooleano(String op1){
     
        return "PUT_BOOLEANO,"+op1+",,\n";
    }
    
    public String readExpresion(String op1){
     
    return "READ,"+op1+",,\n";}
    
    public String not(String op1, String temporal){
        return "NOT,"+op1+"," + temporal +",\n";    
    }
        
    public String devValor(String op1){
        return "DEVVALOR,"+op1+",,\n";
    }

    public String termina_Main () {
        return "TERMINA,,,\n";
    }
    
    public String da_valor_temp (String temporal, String valor){
        return "TEMP," + temporal + ","+ valor + ",\n";
    }
    
    public String guardar_res (String temporal){
        return "GUARDAres,"+ temporal + ",,\n";
    }
    
    public String elemento_vector (String ident, int indice, String temporal){
        return "ASIGNAR_TEMP_A_POSVECTOR," + ident + "," + indice + "," + temporal + ",\n";
    }//Esto efectua temporal = ident[indice]
    
    public String a_elemento_vector (String ident, int indice, String temporal){
        return "ASIGNAR_A_POSVECTOR_TEMP," + ident + "," + indice + "," + temporal + ",\n";
    }//Esto efectua ident[indice] = temporal
    
    public String constructor (String ident){
        return "CONSTRUCTOR," + ident + ",,\n";
    }
} 