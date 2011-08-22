package compilationunit;

public class Tercetos {
	
	
	/*
	 * Sobrenombre que llevaran todas las [temporales-etiquetas]+numero.
	 * pe: $temporal80, $etiqueta99
	 */
	String temporal ="$temporal";
	String etiqueta="$etiqueta";
	
	/*
	 * Llevará la cuenta del numero de etiquetas-temporales que llevamos emitidas
	 */
    int c_etiqueta;
    int c_temporal;
	
    /*
     * Objeto Terceto con el numero de temporales-etiquetas a cero.
     */
	public Tercetos () {
        c_etiqueta = 0;
        c_temporal = 0;
	}
	
	/*
	 * Para calculos intermedios o enteros...
	 */
    public String darTemporal(){
        c_temporal = c_temporal + 1;
        return temporal + c_temporal;
    }
    
    public String darEtiqueta(){
        c_etiqueta = c_etiqueta + 1;
        return etiqueta + c_etiqueta;
    }
    
    /*
     * Aginar valor a variable
     * Se tiene que pasar el nombre de la variable obtenida de darTemporal mas el valor a asignar
     */
    public String da_valor_temp (String temporal, String valor){
        return "TEMP," + temporal + "," + valor + "\n";
    }
	
	/*
	 * Parametros de funciones-metodos
	 * pe: metodo(int hola)
	 */
	public String Parametro(String parametro) {
		return "PARAM,,"+parametro+"\n";
	}
	
	/*
	 * Fin programa!
	 */
    public String termina_Main () {
        return "TERMINA,,,\n";
    }
}