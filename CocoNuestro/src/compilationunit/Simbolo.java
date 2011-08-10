package compilationunit;

public class Simbolo {
	private String nombre;	// Nombre del objeto
	private int type;		// tipo del objeto
	private int kind;		// varible, funcion, metodo...
	private int line;		// línea dónde se encontró el símbolo
	private int column;		// columna dónde se encontró el símbolo
	private int parametros; // En caso de ser una función, indica cuántos parámetros tiene
							// Si no fuera una función siempre estará a 0.
	private int visible;    // Público o privado.
	private int tiporetorno; //Si es una función esto indicará el tipo de valor devuelto
	


public Simbolo(String nombre, int type, int kind){
	   this.nombre = nombre;
	   this.type = type;
	   this.kind = kind;
	   this.parametros = 0;
	}

public void SetNombre(String nombre){
	   this.nombre = nombre;
	}

public void SetType(int type){
	   this.type = type;
	}

public void SetKind(int kind){
	   this.kind = kind;
	}

public void SetLine(int line){
	   this.line = line;
	}

public void SetColumn(int column){
	   this.column = column;
	}

public void SetNParametros(int parametros){
	   this.parametros = parametros;
	}

public void SetVisibilidad(int visible){
	   this.visible = visible;
	}

public void SetTipoRetorno(int tiporetorno){
	   this.tiporetorno = tiporetorno;
	}

}