package compilationunit;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;
import java.io.*;
import java.util.*;

// Set the name of your grammar here (and at the end of this grammar):


public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _enteros = 2;
	public static final int _cadenaCar = 3;
	public static final int _bool = 4;
	public static final int _boool = 5;
	public static final int _charr = 6;
	public static final int _classs = 7;
	public static final int _ffalse = 8;
	public static final int _intt = 9;
	public static final int _new = 10;
	public static final int _short = 11;
	public static final int _static = 12;
	public static final int _ttrue = 13;
	public static final int _voidd = 14;
	public static final int _publicc = 15;
	public static final int _privatte = 16;
	public static final int _dospuntos_dos = 17;
	public static final int _rreturn = 18;
	public static final int _cout = 19;
	public static final int _cin = 20;
	public static final int _menor_menor = 21;
	public static final int _mayor_mayor = 22;
	public static final int _iff = 23;
	public static final int _elsse = 24;
	public static final int _main = 25;
	public static final int _dosPuntos = 26;
	public static final int _comma = 27;
	public static final int _punto = 28;
	public static final int _llave_ab = 29;
	public static final int _corchete_ab = 30;
	public static final int _parent_ab = 31;
	public static final int _op_menos = 32;
	public static final int _op_menosmenos = 33;
	public static final int _op_mas = 34;
	public static final int _op_masmas = 35;
	public static final int _llave_ce = 36;
	public static final int _corchete_ce = 37;
	public static final int _parent_ce = 38;
	public static final int _op_producto = 39;
	public static final int _op_division = 40;
	public static final int _op_asig = 41;
	public static final int _puntoComa = 42;
	public static final int _doblesComillas = 43;
	public static final int _interrogacion = 44;
	public static final int _barra_vert = 45;
	public static final int _op_negacion = 46;
	public static final int _op_menor = 47;
	public static final int _op_mayor = 48;
	public static final int _op_menor_igual = 49;
	public static final int _op_mayor_igual = 50;
	public static final int _op_igual = 51;
	public static final int _op_distinto = 52;
	public static final int _op_and = 53;
	public static final int _op_or = 54;
	public static final int _op_asig_mas = 55;
	public static final int _op_asig_menos = 56;
	public static final int _op_asig_producto = 57;
	public static final int _op_asig_division = 58;
	public static final int _op_asig_modulo = 59;
	public static final int maxT = 60;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	final int undef=0, entera=1, bool=2, cadena=3, vacio=4, identificador=5, vector=6;
	// DeclaraciÃ³n de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3, parametro=4;
	//DeclaraciÃ³n de visibilidad
	final int privado=0, publico=1;

	// Tabla de simbolos global
	public Tablas tabla;
	// Tupla devuelta por las expresiones (tipo, valor)
	
	// Este simbolo servira para indicar la clase del objeto encontrado por ValorFinalExp
	// En realidad es una solucion un poco chapuza, pero es necesario tenerlo aqui
	public Simbolo simboloClaseObjeto = null;
	
	//Utilizaremos este simbolo para ir anadiendo los valores temporales a la tabla de simbolos
	public Simbolo simboloNuevoTemporal = null;
	
	// Esta variable indicarÃ¡ si se ha producido un return en una funcion/metodo que devuelva
	// algo distinto de void
	public boolean hayreturn = false;
	
	//Otra chapucilla
	int indice_vector = -1;
	
	//Otra chapuza mas
	Simbolo simboloObjetoGlob = null;

	// Clase tercetos.
	Tercetos tercetos = new Tercetos();
	String terceto_actual;
	LinkedList<tupla_Tercetos> colaTercetos = new LinkedList<tupla_Tercetos> ();
    LinkedList<tupla_Tercetos> colaMain = new LinkedList<tupla_Tercetos> ();
	
	GenFinal codigo_final;
	// Fichero de salida para el codigo Ensamblador
	String fichero = new String();
	//String fichero = new String("/tmp/CodigoObjeto");
// If you want your generated compiler case insensitive add the
// keyword IGNORECASE here.




	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void CEMASMAS1() {
		tabla = new Tablas();	
		CEMASMAS();
		int desplazamiento; 
		desplazamiento = tabla.GetAmbitoGlobal().ActualizarDesplazamiento();
		System.out.println("El desplazamiento del ambito global es " + desplazamiento);
		tercetos = new Tercetos();
		try {
			if (errors.count==0) {	// todo ok!
				codigo_final = new GenFinal(colaTercetos, tabla, fichero);
			}
		} catch( Exception e ) {	// Codigo Final genera excepcion
			System.err.println("GeneraciÃ³n de CÃ³digo Final desbocado.");
		}
		
	}

	void CEMASMAS() {
		int type=undef; 
		int type1;
		Simbolo simbolo_dev = null;
		String nombre = null;	// podre ser nombre de Objeto
		
		if (la.kind == 7 || la.kind == 15 || la.kind == 16) {
			DecClase();
			CEMASMAS();
		} else if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
				nombre = new String(t.val);
				if (type==identificador) {
				Simbolo simbolo = new Simbolo(t.val, type, 0);
				simbolo.SetLine(t.line);
				simbolo.SetColumn(t.col);
				if ((tabla.EstaRecur(simbolo.GetNombre()))) {	// este ident ya existia!, ok!
					simbolo_dev = tabla.GetSimboloRecur(simbolo.GetNombre());
						if (simbolo_dev.GetKind() != clase)
							SemErr(simbolo_dev.GetNombre() + " Error declaracion.");
				} else {
					SemErr(t.val + " no declarado anteriormente.");
				}
				}
				
			} else {
				Get();
				type = vacio;
			}
			if (la.kind == 25) {
				Main(type, nombre);
			} else if (la.kind == 1) {
				Get();
				Simbolo simbolo = new Simbolo(t.val, type, var);
				simbolo.SetLine(t.line);
				simbolo.SetColumn(t.col);
				if ((simbolo_dev != null) && (simbolo_dev.GetKind() == clase))
							simbolo.SetClase(simbolo_dev);
				 Simbolo sim = tabla.GetSimboloRecur(t.val);
				// Metodo de una clase: Persona::daAnio
				if (la.val.equals((String) "::")) { // casting, si el siguiente es metodo, caso especial
						if (tabla.EstaEnActual(simbolo.GetNombre())		// Existe
									&& (sim!=null) && (sim.GetKind()==clase)) {	// y es de tipo clase
							System.out.println("Metodo de una clase existente!(Clase:"+simbolo.GetNombre()+"), todo ok!");
						} else {
							SemErr("No existe la clase: "+t.val);
						}
				} else if (tabla.EstaEnActual(simbolo.GetNombre())) {
						Simbolo simbolonuevo = tabla.GetSimboloRecur(t.val);
						SemErr(simbolonuevo.GetNombre() + " ya estaba declarado en la linea " + simbolonuevo.GetLine() + " columna " + simbolonuevo.GetColumn());
				} else {
					//SemErr("No existe ningÃºn objeto "+simbolo.GetNombre()+" creado.");
					tabla.InsertarEnActual(simbolo); // Estaba puesto antes...
				}
				
				if (la.kind == 31) {
					Subprograma(simbolo);
				} else if (la.kind == 30) {
					Vector(simbolo);
					Expect(42);
				} else if (la.kind == 41 || la.kind == 42) {
					DecVar(simbolo);
				} else if (la.kind == 17) {
					String Clase= (String) t.val; 
					DecMetodo(Clase, type, nombre);
				} else SynErr(61);
				CEMASMAS();
			} else SynErr(62);
		} else SynErr(63);
	}

	void DecClase() {
		int visible=privado; 
		Simbolo simbolo = new Simbolo ("undef", 0, clase); System.out.println("DecClase!");
		if (la.kind == 15 || la.kind == 16) {
			if (la.kind == 15) {
				Get();
				simbolo.SetVisibilidad(1); 
			} else {
				Get();
				simbolo.SetVisibilidad(0); 
			}
		}
		Expect(7);
		Expect(1);
		simbolo.SetNombre(t.val);	// Modifico el nombre del Simbolo
		simbolo.SetLine(t.line);
		simbolo.SetColumn(t.col);
		if ((tabla.EstaEnActual(t.val)) &&
				(tabla.GetSimboloRecur(t.val) != null) &&
					(tabla.GetSimboloRecur(t.val).GetKind()==clase) ) {
			SemErr("Clase "+ t.val +" ya definida en lÃ­nea "+tabla.GetSimboloRecur(t.val).GetLine()+
						" y columna:"+ tabla.GetSimboloRecur(t.val).GetColumn());
			// Consumimo todo hasta el final de la clase
			while (!((t.val.equals((String) "}")) && (la.val.equals((String) ";")))) {	// fin de clase
				
		Get();
		}
		} else {	// Nueva clase!
			tabla.InsertarEnActual(simbolo);
			tabla.NuevoAmbito(simbolo);		// Nuevo ambito para meter metodos
		
		Expect(29);
		Cuerpo_Clase(visible, simbolo);
		if (la.kind == 15 || la.kind == 16) {
			if (la.kind == 15) {
				Get();
				visible=publico; 
				Expect(26);
				Cuerpo_Clase(visible, simbolo);
				if (la.kind == 16) {
					Get();
					Expect(26);
					visible=publico; 
					Cuerpo_Clase(visible, simbolo);
				}
			} else {
				Get();
				visible=privado; 
				Expect(26);
				Cuerpo_Clase(visible, simbolo);
				if (la.kind == 15) {
					Get();
					Expect(26);
					visible=publico; 
					Cuerpo_Clase(visible, simbolo);
				}
			}
		}
		Expect(36);
		tabla.CerrarAmbito();
		}	// fin else, para el ambito de nua clase OK! 
		
		Expect(42);
	}

	int  Ttipo() {
		int  type;
		type = undef; //inicializamos
		
		if (la.kind == 9) {
			Get();
			type = entera;	
		} else if (la.kind == 4) {
			Get();
			type = bool; 	
		} else if (la.kind == 5) {
			Get();
			type = bool; 	
		} else if (la.kind == 6) {
			Get();
			if (la.kind == 39) {
				Get();
			}
			type = cadena; 
		} else if (la.kind == 1) {
			Get();
			type = identificador; 
		} else SynErr(64);
		return type;
	}

	void Main(int type, String nombre) {
		Simbolo simbolo_funcion = new Simbolo("main",type,funcion);
		Simbolo simbolo_clase = null;
		hayreturn = false;
		simbolo_funcion.SetEtiqueta(tercetos.darEtiqueta());
		tabla.InsertarEnActual(simbolo_funcion);								   
		
		Expect(25);
		tabla.NuevoAmbito(simbolo_funcion);
		simbolo_funcion.SetKind (funcion);
		simbolo_funcion.SetTipoRetorno(simbolo_funcion.GetType());
		//AÃ±adimos el terceto a la cola de tercetos!
		terceto_actual=tercetos.EtiquetaSubprograma(simbolo_funcion.GetEtiqueta());
		tupla_Tercetos tupla= new tupla_Tercetos(tabla.GetAmbitoActual(),terceto_actual);
		colaTercetos.add(tupla);
		
		if (simbolo_funcion.GetTipoRetorno() == identificador)
		{
		if (!(tabla.EstaRecur(nombre)))
			SemErr("No se ha encontrado ninguna clase con nombre " + nombre);
		else
			simbolo_clase = tabla.GetSimboloRecur(nombre);
			if (simbolo_clase.GetKind() != clase)
				SemErr(identificador + " no es una clase");
			else
				simbolo_funcion.SetClaseDevuelta(simbolo_clase);
		} 
		Expect(31);
		if (la.kind == 14) {
			Get();
		}
		Expect(38);
		Expect(29);
		Cuerpo(simbolo_funcion);
		if ((simbolo_funcion.GetTipoRetorno() != vacio) && !hayreturn)
		{
		System.out.println(simbolo_funcion.GetTipoRetorno());
		SemErr("La funcion tiene que devolver algo (No se ha encontrado instruccion Return)");
		}
		else
			hayreturn = false; 
		  
		Expect(36);
		tabla.CerrarAmbito();
	}

	void Subprograma(Simbolo simbolo_funcion) {
		tabla.NuevoAmbito(simbolo_funcion);
		simbolo_funcion.SetKind (funcion);
		simbolo_funcion.SetTipoRetorno(simbolo_funcion.GetType()); //Cuando se ha creado el sÃ­mbolo que nos pasan, en type se ha metido el tipo de retorno.
		simbolo_funcion.SetType(undef);
		simbolo_funcion.SetEtiqueta(tercetos.darEtiqueta());
		// Una vez generada la eti aÃ±ado a la cola de tercetos
		
		if (simbolo_funcion.GetTipoRetorno() == identificador)
			{
			simbolo_funcion.SetClaseDevuelta(simbolo_funcion.GetClase()); //Cuando se ha creado el simbolo que nos pasan, en Clase se ha metido el sÃ­mbolo de la clase que vamos a devolver.
			}  
		Expect(31);
		if (StartOf(1)) {
			Parametros(simbolo_funcion);
		}
		Expect(38);
		Expect(29);
		Cuerpo(simbolo_funcion);
		tabla.CerrarAmbito();
		if ((simbolo_funcion.GetTipoRetorno() != vacio) && !hayreturn)
				{
				System.out.println(hayreturn);
				SemErr("La funcion tiene que devolver algo (No se ha encontrado instruccion Return)");
				}
		else
				hayreturn = false; 
		Expect(36);
	}

	void Vector(Simbolo simbolo) {
		Expect(30);
		Expect(2);
		if (simbolo.GetType() != entera)
		SemErr("El contenido de un vector debe ser de tipo entero");
		try {										
			if (Integer.parseInt(t.val) == 0)
				SemErr("Error declarando la dimension del vector");
			
			simbolo.SetToVector(Integer.parseInt(t.val)); //Si no ha venido un entero se generara una excepcion
			}
		catch (NumberFormatException e)
			{
			System.out.println("Deberia haberse especificado la dimension del vector");
			}
		
		Expect(37);
	}

	void DecVar(Simbolo simbolo_anterior) {
		simboloClaseObjeto = null;
		int type = undef;
		Simbolo simbolo_temp = null;
		if (la.kind == 41) {
			Get();
			simbolo_temp = VExpresion();
			if (simbolo_temp.GetType() != simbolo_anterior.GetType())
			SemErr("Error de tipos en la inicializacion de la variable");
			else if (type == identificador)
				{
				if (simbolo_anterior.GetClase() == null)
					SemErr("Error de tipos en la inicializacion de la variable. Error en la clase.");
				else if (simbolo_anterior.GetClase() != simboloClaseObjeto)
					SemErr("Error de tipos en la inicializacion de la variable. Clases distintas.");
				}
			else
				simbolo_anterior.SetValor(null); //AsÃ­ la variable constarÃ¡ como inicializada
				simboloClaseObjeto = null;
			
		}
		Expect(42);
		if (simbolo_temp == null) {	// caso: int Var;
		simbolo_anterior.SetEtiqueta(tercetos.darEtiqueta());
		} else {	// caso: int Var=Expresion;
		}
		
	}

	void DecMetodo(String Clase, int tipoRetorno, String Nombre) {
		Simbolo simDecMetodo = new Simbolo("simDecMetodo", 0, metodo);		// Anado metodo al ambito actual
		boolean hay_param=false;
		//System.out.println("Estas en declaracion Metodo");
		
		Expect(17);
		Expect(1);
		if ((tabla.GetSimboloRecur(Clase) != null) &&	// clase existe?Â¿
			(tabla.GetSimboloRecur(Clase).GetKind()==clase)) {
			//clase y metodo existen!
			if (tabla.GetSimboloRecur(Clase).GetAmbitoAsociado().Esta(t.val)) {	// metodo asoc a claseÂ¿?
				// Cojo el simbolo del metodo que se declaro dentro de la clase
				Simbolo simMetodo=tabla.GetSimboloRecur(Clase).GetAmbitoAsociado().GetSimbolo(t.val);
				// Comprobacion tipoRetoron y argumentos son iguales
				if (simMetodo.GetTipoRetorno()==tipoRetorno) {
				
					/*Comprobacion de tipo devuelvo DecMetodo correcto y del mismo Objeto (si es ident)*/
					if (tipoRetorno==identificador) {
						if ((tabla.GetSimboloRecur(Nombre)!=null) &&	// Existe
								(tabla.GetSimboloRecur(Nombre).GetKind()==clase) &&	// es claseÂ¿?
									(!(simMetodo.GetClaseDevuelta().GetNombre().equalsIgnoreCase(Nombre))) ) {
							SemErr("Clases devueltas difieren");
						}
					}
					tabla.AbrirAmbito(tabla.GetSimboloRecur(Clase).GetAmbitoAsociado());											
					// Nuevo ambito para la dec del metodo
					tabla.NuevoAmbito(simMetodo);
					//simMetodo.SetAmbitoAsociado(tabla.GetAmbitoActual());
					
		Expect(31);
		if (StartOf(1)) {
			Parametros(simMetodo);
			hay_param=true; 
		}
		Expect(38);
		if ((!(hay_param)) && 
			(simMetodo.GetNParametros()!=0)) {	// No hemos entrado en parametros
		SemErr("Error tipos declaracion Metodo.");
		}
		
		Expect(29);
		simMetodo.SetEtiqueta(tercetos.darEtiqueta());
		Cuerpo(simMetodo);
		if ((simMetodo.GetTipoRetorno() != vacio) && !hayreturn)
		SemErr("El metodo tiene que devolver el tipo especificado en la declaracion (no hay sentencia return)");
		else
			hayreturn = false;
		Expect(36);
		tabla.CerrarAmbito();	// cierro ambito del metodo
		tabla.CerrarAmbito();	// Cerramos el ambito de la clase
								// y volveremos, seguramente, al global
		} else {
			SemErr("DeclaraciÃ³n metodo devuelve un tipo diferente a la declaraciÃ³n en la Clase: "+Clase+", en linea-col:"+simMetodo.GetColumn()+"-"+simMetodo.GetColumn());
		}
		} else {
			SemErr("Existe la clase: "+Clase+", pero no tiene ningun metodo asociado: "+t.val);
		}
		} else {
			SemErr("No existe dicha clase: "+Clase);
			
		Expect(29);
		Cuerpo(simDecMetodo);
		Expect(36);
		}
		
	}

	void Cuerpo_Clase(int visible, Simbolo simClase) {
		int type;
		Simbolo ObjetoDevuelto=null;	// inicializado
		
		if (StartOf(1)) {
			while (StartOf(1)) {
				if (StartOf(2)) {
					type = Ttipo();
					Simbolo sim = new Simbolo("elem_clase", 0, 0);	// Creacion del simbolo
					sim.SetClase(simClase);	// clase a la q pertenece
					   if (type==identificador) {
					   	if ((tabla.GetSimboloRecur(t.val) != null) &&
							(tabla.GetSimboloRecur(t.val).GetKind()==clase)) {
						//Guardamos la direcciÃ³n para engancharlo luego
							ObjetoDevuelto=tabla.GetSimboloRecur(t.val);
						} else {
							SemErr("No existe ningun objeto con dicho nombre.");
						}
					}
					tabla.InsertarEnActual(sim);
					
					Expect(1);
					sim.SetNombre(t.val);
					sim.SetVisibilidad(visible);
					sim.SetLine(t.line);
					    sim.SetColumn(t.col);
					    //System.out.println(t.val+" Simbolo q estas mirando, y su tipo:"+type);
					
					if (la.kind == 41 || la.kind == 42) {
						sim.SetKind(var);	// Es variable
						sim.SetType(type);	// Su tipo
						if (type==identificador) {
							sim.SetClaseDevuelta(ObjetoDevuelto);
						}
						
						DecVar(sim);
					} else if (la.kind == 31) {
						sim.SetKind(metodo);
						sim.SetTipoRetorno(type);
						sim.SetClase(simClase);		// a q clase pertenece le metodo
						sim.SetAmbitoAsociado(tabla.GetAmbitoActual());	// ambito de accion
						//System.out.println("El Metodo pertenece a la clase:"+sim.GetClase().GetNombre());
						if (type==identificador) {
							sim.SetClaseDevuelta(ObjetoDevuelto);	//devuelve un tipo objeto
							//System.out.println("Clase devuelta:"+ObjetoDevuelto.GetNombre());
						}
						
						DecCabMet(sim);
					} else SynErr(65);
				} else {
					Get();
					Expect(1);
					Simbolo simbol = new Simbolo(t.val, 0, metodo);
					simbol.SetKind(metodo);
					simbol.SetTipoRetorno(vacio);
					simbol.SetClase(simClase);		// a q clase pertenece le metodo
					simbol.SetAmbitoAsociado(tabla.GetAmbitoActual());	// ambito de accion
					
					DecCabMet(simbol);
				}
			}
		}
	}

	void DecCabMet(Simbolo simbolo) {
		int type; 
		Expect(31);
		Param_Cab(simbolo);
		Expect(38);
		Expect(42);
	}

	void Param_Cab(Simbolo simbolo) {
		int type; 
		if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
				Simbolo sim = new Simbolo("Arg_Metodo",0,parametro); 
				if (la.kind == 30) {
					Vector(sim);
					System.out.println("Funcionalidad por hacer");
				}
				sim.SetType(type);
				simbolo.AnadirParametro(sim);
				
				if (la.kind == 27) {
					Get();
					Param_Cab(simbolo);
				}
			} else {
				Get();
			}
		}
	}

	void Parametros(Simbolo simbolo_nombre_funcion) {
		int type, contador=0;
		String nombre_clase = null;
		if (StartOf(2)) {
			type = Ttipo();
			nombre_clase = new String(t.val);
			Expect(1);
			Simbolo simbolo_parametro = new Simbolo(t.val, type, parametro);
			simbolo_parametro.SetLine(t.line);
			simbolo_parametro.SetColumn(t.col);
			if (type == identificador)
				{
				if (!(tabla.EstaRecur(nombre_clase)))
					SemErr(t.val + " no estaba declarado previamente");
				else
					{
					Simbolo simbolo_clase = tabla.GetSimboloRecur(nombre_clase);
					if (simbolo_clase.GetKind() != clase)
						SemErr(t.val + " no es una clase");
					else
						simbolo_parametro.SetClase(simbolo_clase);
					}
				}
			if (simbolo_nombre_funcion.GetKind() != metodo) {						
					simbolo_nombre_funcion.AnadirParametro(simbolo_parametro);
					tabla.InsertarEnActual(simbolo_parametro);		
			} else { // Caso decMetodo
				System.out.println("Estas en caso decMetodo PARAMETROS.");
				if (simbolo_nombre_funcion.GetNParametros() == 0) {
					SemErr("Error numero argumentos en declaracion Metodo.");
				} else {
					if (simbolo_nombre_funcion.GetParametros(contador).GetType()!=type) { 
						SemErr("Error tipos declaracion Metodo.");
					} else {	// coincide en tipos
						simbolo_nombre_funcion.GetParametros(contador).SetNombre(t.val);
						//String temp_nueva = new String(t.val);
						//simbolo_nombre_funcion.GetParametros(0).SetNombre(temp_nueva);
						tabla.InsertarEnActual(simbolo_nombre_funcion.GetParametros(contador));	// inserto en ambito Metodo el simb
						contador++;
					}
				}
			}
			
			if (la.kind == 30) {
				Vector(simbolo_parametro);
			}
			if (la.kind == 27) {
				while (la.kind == 27) {
					Get();
					type = Ttipo();
					Expect(1);
					simbolo_parametro = new Simbolo(t.val, type, parametro);
					simbolo_parametro.SetLine(t.line);
					simbolo_parametro.SetColumn(t.col);
					if (simbolo_nombre_funcion.GetKind() != metodo) {						
						if (tabla.EstaEnActual(simbolo_parametro.GetNombre())) {
							SemErr("Hay otro parametro con el mismo nombre");
						} else {
							simbolo_nombre_funcion.AnadirParametro(simbolo_parametro);
							tabla.InsertarEnActual(simbolo_parametro);		
						}
					} else { // Caso decMetodo
						System.out.println("Estas en caso decMetodo PARAMETROS.");
						if (simbolo_nombre_funcion.GetNParametros() == 0) {
							SemErr("Error numero argumentos en declaracion Metodo.");
						} else {
							if (simbolo_nombre_funcion.GetParametros(contador).GetType()!=type) { 
								SemErr("Error tipos declaracion Metodo.");
							} else {	// coincide en tipos
								simbolo_nombre_funcion.GetParametros(contador).SetNombre(t.val);
								//String temp_nueva = new String(t.val);
								//simbolo_nombre_funcion.GetParametros(0).SetNombre(temp_nueva);
								tabla.InsertarEnActual(simbolo_nombre_funcion.GetParametros(contador));	// inserto en ambito Metodo el simb
								contador++;
							}
						}
					}
					
					if (la.kind == 30) {
						Vector(simbolo_parametro);
					}
				}
			}
		} else if (la.kind == 14) {
			Get();
			System.out.println("Estas en caso decMetodo PARAMETROS.");
			if (simbolo_nombre_funcion.GetKind() == metodo) {
				if (simbolo_nombre_funcion.GetNParametros() != 0)
					SemErr("Error tipos declaracion Metodo.");
			}
			
		} else SynErr(66);
	}

	void Cuerpo(Simbolo simbolo_funcion) {
		int type = undef; 
		Simbolo simbolo_anterior = null;
		boolean estaba_declarado = false;
		while (StartOf(3)) {
			if (StartOf(4)) {
				Instruccion(simbolo_funcion);
			} else {
				if (StartOf(2)) {
					type = Ttipo();
				} else {
					Get();
				}
				if (type == identificador)
				{
				if (!(tabla.EstaRecur(t.val)))
					{
					SemErr(t.val + " no ha sido declarado previamente");
					}
				else
					{
					simbolo_anterior = tabla.GetSimboloRecur(t.val);
					estaba_declarado = true;
					}
				}
				else if (la.val.contentEquals("["))
						SemErr("No existe el tipo especificado");
				
				if (la.kind == 30) {
					Simbolo simbolo_lo_que_tiene_el_vector = new Simbolo("Estesimbolonovale", entera, var);
					DarPosVector(simbolo_anterior, simbolo_lo_que_tiene_el_vector);
				}
				if (la.kind == 28) {
					Llamada(simbolo_anterior);
				} else if (StartOf(5)) {
					InstExpresion(simbolo_anterior);
				} else if (la.kind == 1) {
					Get();
					if (estaba_declarado)
					{
					if (simbolo_anterior.GetKind() != clase) 
					{
					SemErr("Se esperaba un tipo o un identificador declarado como una clase: '" + simbolo_anterior.GetNombre() + "' declarado en la linea " + simbolo_anterior.GetLine() + " columna " + simbolo_anterior.GetColumn() + " no fue declarado como una clase");
						}
					} 
					Simbolo simbolo = new Simbolo(t.val, type, 0);
					  simbolo.SetLine(t.line);
					 	  simbolo.SetColumn(t.col);
					 	  simbolo.SetClase(simbolo_anterior);
					 
					 	  if (tabla.EstaEnActual(simbolo.GetNombre()))
					 			{
					 			Simbolo simbolonuevo = tabla.GetSimboloRecur(t.val);
					 			SemErr("'" + simbolonuevo.GetNombre() + "' ya estaba declarado en la linea " + simbolonuevo.GetLine() + " columna " + simbolonuevo.GetColumn());
					 			}
					 	  else
					 	  		{
					  		tabla.InsertarEnActual(simbolo);
					  		}	
					  	
					if (la.kind == 31) {
						Subprograma(simbolo);
					} else if (la.kind == 30 || la.kind == 41 || la.kind == 42) {
						if (la.kind == 30) {
							Vector(simbolo);
							Expect(42);
						} else {
							DecVar(simbolo);
						}
					} else SynErr(67);
				} else SynErr(68);
			}
		}
	}

	void Instruccion(Simbolo simbolo_funcion) {
		int type = undef; 
		if (la.kind == 18) {
			System.out.println("Estamos en Instruccion");
			type  = InstReturn();
			if (simbolo_funcion.GetTipoRetorno() != type)
			SemErr("return devuelve un tipo distinto al declarado.");
			else if (type == identificador)
					{
					if (simbolo_funcion.GetClaseDevuelta() != simboloClaseObjeto)
						{
						SemErr("return devuelve una clase distinta a la declarada.");
						}
					}
			
		} else if (la.kind == 19) {
			InstCout();
		} else if (la.kind == 20) {
			InstCin();
		} else if (la.kind == 23) {
			InstIfElse(simbolo_funcion);
		} else SynErr(69);
	}

	void DarPosVector(Simbolo sim, Simbolo simbolo_resultado) {
		int tipoDev; 
		System.out.println("Estamos en DarPosVector");
		Expect(30);
		try
		{ 
		if (!(sim.Es_Vector()))
				SemErr(sim.GetNombre() + " definido en la linea " + sim.GetLine() + " columna " + sim.GetColumn() + " no es un vector");
			}
		catch (NullPointerException e)
			{
			System.out.println("Se le ha pasado un simbolo nulo a DarPosVector");
		}
		
		simbolo_resultado.SetType(entera);
		tabla.InsertarEnActual(simbolo_resultado);
		
		Expect(2);
		if (Integer.parseInt(t.val) >= sim.Actualiza_Tamano())
		SemErr("Posicion fuera de rango");
		 indice_vector = Integer.parseInt(t.val);
		 tercetos.elemento_vector(sim.GetNombre(), Integer.parseInt(t.val), simbolo_resultado.GetNombre());
		
		Expect(37);
	}

	void Llamada(Simbolo simbolo_objeto) {
		TablaSimbolos ambito_clase = null; //AtenciÃ³n!! Llamada solo sirve para llamar a un mÃ©todo.
		Simbolo simbolo_metodoargumento = null;
		System.out.println("Entramos en Llamada");
		simboloObjetoGlob = simbolo_objeto; 
		Expect(28);
		Expect(1);
		if (simbolo_objeto != null)
		{
		Simbolo simbolo_clase =  simbolo_objeto.GetClase();
		if (simbolo_clase == null)
			SemErr("El identificador " + simbolo_objeto.GetNombre() + " no ha sido declarado perteneciente a ninguna clase.");
		else
			{
			ambito_clase = simbolo_clase.GetAmbitoAsociado();
			if (ambito_clase == null)
				SemErr("La clase " + simbolo_clase.GetNombre() + " no tiene ningun ambito asociado");
			else
				{
				if (!(ambito_clase.Esta(t.val)))
					SemErr("El identificador " + t.val + " no se ha encontrado declarado en la clase " + simbolo_clase.GetNombre());
				else
					{
					simbolo_metodoargumento = ambito_clase.GetSimbolo(t.val);
					if (simbolo_metodoargumento.GetVisibilidad() == privado)
						SemErr(simbolo_metodoargumento.GetNombre() + " no es publico.");
					}
				}
			} 
		}
		if (la.kind == 31) {
			Get();
			if (simbolo_metodoargumento != null)
			{
			if (simbolo_metodoargumento.GetKind() != metodo) //Si hay un parÃ©ntesis tiene que ser un mÃ©todo, y debe ser pÃºblico.
				{
				SemErr("'" + simbolo_metodoargumento.GetNombre() + "' declarado en la linea " + simbolo_metodoargumento.GetLine() + " no fue declarado como un metodo");
				}
			else if (simbolo_metodoargumento.GetVisibilidad() == privado)
			 	SemErr(simbolo_metodoargumento.GetNombre() + " no es un metodo publico.");
				}
			
			VArgumentos(simbolo_metodoargumento, 0);
			Expect(38);
			Expect(42);
		} else if (StartOf(5)) {
			InstExpresion(simbolo_metodoargumento);
			simboloObjetoGlob = null;
		} else SynErr(70);
	}

	void InstExpresion(Simbolo simbolo) {
		int type=undef;
		System.out.println("Entramos en InstExpresion");
		simboloClaseObjeto = null;
		Simbolo simbolo_resultado = null;
		int ind_vector = indice_vector; //Guardamos indice_vector por si el simbolo anterior era un vector y no queremos perder el indice
		if (!(t.val.contentEquals("]")))
				{
		    if (((la.val.contentEquals("=")) || (la.val.contentEquals("+=")) || (la.val.contentEquals("-=")) || (la.val.contentEquals("*="))|| (la.val.contentEquals("/="))|| (la.val.contentEquals("%="))) && simbolo.Es_Vector())
					SemErr("Operacion no permitida: La parte izquierda de la asignacion es un vector");
		}
		 		 
		if (la.kind == 31) {
			Get();
			VArgumentos(simbolo, 0);
			Expect(38);
			Expect(42);
		} else if (StartOf(6)) {
			switch (la.kind) {
			case 41: {
				Get();
				break;
			}
			case 55: {
				Get();
				break;
			}
			case 56: {
				Get();
				break;
			}
			case 57: {
				Get();
				break;
			}
			case 58: {
				Get();
				break;
			}
			case 59: {
				Get();
				break;
			}
			}
			simbolo_resultado = VExpresion();
			try
			{
			if ((simbolo.GetType() != simbolo_resultado.GetType()) && (simbolo.GetType() != vector))
			  		SemErr("El tipo del identificador no coincide con el tipo de la expresion");
			  	else if ((simbolo.GetType() == vector) && (simbolo_resultado.GetType() != entera))
			  		SemErr("El tipo del identificador no coincide con el tipo de la expresion.Nombre");
			else if (simbolo_resultado.GetType() == identificador)
				{
				if (simbolo.GetClase() != simboloClaseObjeto)
					{
					SemErr("La clase del identificador no coincide con la clase de la expresion");
					}
				else
					simboloClaseObjeto = null;
				}
			}
			catch (NullPointerException e)
				{
				}
			
			
			if (simbolo.GetType() == vector)
			{
			String terceto = new String(tercetos.a_elemento_vector(simbolo.GetNombre(), ind_vector, simbolo_resultado.GetNombre()));
			tupla_Tercetos tupla_temp = new tupla_Tercetos(tabla.GetAmbitoActual(),terceto);
			    colaTercetos.add(tupla_temp);
						}
					else if (simboloObjetoGlob != null) //En este caso sera en el que estemos ante un atributo de un objeto
						{
			String terceto = new String(tercetos.asignacion(simboloObjetoGlob.GetNombre() + "." + simbolo.GetNombre(), simbolo_resultado.GetNombre()));
			tupla_Tercetos tupla_temp = new tupla_Tercetos(tabla.GetAmbitoActual(),terceto);
			    colaTercetos.add(tupla_temp);			   												
						}
					else
						{
			String terceto = new String(tercetos.asignacion(simbolo.GetNombre(), simbolo_resultado.GetNombre()));
			tupla_Tercetos tupla_temp = new tupla_Tercetos(tabla.GetAmbitoActual(),terceto);
			    colaTercetos.add(tupla_temp);	
						}
			
			
			Expect(42);
		} else SynErr(71);
	}

	Simbolo  VExpresion() {
		Simbolo  simbolo;
		System.out.println("Entramos en la nueva version de VExpresion");
		int type=undef;
		simbolo = null;
		Simbolo simbolo_temp1 = null;
		String terceto;
		tupla_Tercetos tupla;
		if (la.kind == 32) {
			simbolo = VCambio_Signo();
		} else if (StartOf(7)) {
			if (StartOf(8)) {
				simbolo_temp1 = ValorFinalExp();
			} else {
				Get();
				simbolo_temp1 = VExpresion();
				Expect(38);
			}
			simbolo = simbolo_temp1;
			if (StartOf(9)) {
				Simbolo simbolo_temp2 = null;
				if (la.kind == 32 || la.kind == 34) {
					simbolo_temp2 = VExpSuma(simbolo_temp1);
					if ((simbolo_temp1.GetType() != entera) || (simbolo_temp2.GetType() != entera))
					SemErr("Error de tipos en la expresion");
					
					
				} else if (la.kind == 39 || la.kind == 40) {
					simbolo_temp2 = VExpMul(simbolo_temp1);
					if ((simbolo_temp1.GetType() != entera) || (simbolo_temp2.GetType() != entera))
					SemErr("Error de tipos en la expresion");
					
				} else {
					simbolo_temp2 = VExpOR(simbolo_temp1);
					if ((simbolo_temp1.GetType() != bool) || (simbolo_temp2.GetType() != bool))
					SemErr("Error de tipos en la expresion");
					
				}
				simbolo = simbolo_temp2;
			}
		} else SynErr(72);
		return simbolo;
	}

	void VArgumentos(Simbolo simbolo, int pos) {
		int type=undef;
		Simbolo simbolo_nuevo = null; 
		System.out.println("Entramos en VArgumentos");
		Simbolo simbolo_temp = null;
			
		if (StartOf(10)) {
			simbolo_temp = VExpresion();
			if (simbolo != null)
			{
			if  (pos >= simbolo.GetNParametros())
					{
					SemErr("Numero de parametros no coincidente");
					}
				else
					{ 
					simbolo_nuevo = simbolo.GetParametros(pos);
					if (simbolo_nuevo.GetType() != type)
						SemErr("Tipo de los parametros no coincidente");
					}
			}
					  
			if (la.kind == 27) {
				Get();
				VArgumentos(simbolo, pos + 1);
			}
		}
	}

	int  InstReturn() {
		int  tipoDev;
		tipoDev = undef;
		Simbolo simbolo_temp = null; 
		System.out.println("Entramos en InstReturn");
		simboloClaseObjeto = null;
		simbolo_temp = new Simbolo(tercetos.darEtiqueta(), undef, var);
		Expect(18);
		if (StartOf(10)) {
			simbolo_temp = VExpresion();
			System.out.println("Return devuelve un tipo:" + simbolo_temp.GetType());
			tipoDev= simbolo_temp.GetType(); 
			hayreturn = true;
		}
		Expect(42);
		return tipoDev;
	}

	void InstCout() {
		int type=undef; 
		Expect(19);
		while (la.kind == 21) {
			Get();
			type = Arg_io();
		}
		Expect(42);
		if (type==undef) {
		SemErr("InstCout: Error tipos.");
		}
	}

	void InstCin() {
		int type; 
		Expect(20);
		Expect(22);
		type = Arg_io();
		Expect(42);
	}

	void InstIfElse(Simbolo simbolo_funcion) {
		int type, type1;
		Simbolo simbolo = null; //new Simbolo(t.val, 0, 0);	// borrarcuando corrigas
		Simbolo simbolo_temp = null;
		
		Expect(23);
		simbolo_temp = VExpresion();
		if (simbolo_temp.GetType() != bool) {SemErr("La condicion de un if debe ser logica");}
		simbolo = new Simbolo ("IF", 0, 0);
		if (la.kind == 29) {
			Get();
			tabla.NuevoAmbito(simbolo); 
			Cuerpo(simbolo_funcion);
			tabla.CerrarAmbito();
			Expect(36);
		} else if (StartOf(4)) {
			Instruccion(simbolo_funcion);
		} else if (la.kind == 1) {
			Get();
			if (!(tabla.EstaRecur(t.val)))
			{
			SemErr(t.val + " no ha sido declarado previamente");
			}
			else
				{
				simbolo = tabla.GetSimboloRecur(t.val);
			}
			
			InstExpresion(simbolo);
		} else SynErr(73);
		if (la.kind == 24) {
			Else(simbolo_funcion);
		}
	}

	void Else(Simbolo simbolo_funcion) {
		int type;
		Simbolo sim = new Simbolo("ELSE", 0, 0);	// borrarcuando corrigas
		
		Expect(24);
		if (la.kind == 29) {
			Get();
			tabla.NuevoAmbito(sim); 
			Cuerpo(simbolo_funcion);
			tabla.CerrarAmbito();
			Expect(36);
		} else if (StartOf(4)) {
			Instruccion(simbolo_funcion);
		} else SynErr(74);
	}

	int  Arg_io() {
		int  tipoDev;
		tipoDev=undef;
		Simbolo sim = new Simbolo("Temp",0,0);
		Simbolo simbolo_temp = null; 
		if (la.kind == 1) {
			Get();
			if (!(tabla.EstaRecur(t.val)))
			SemErr(t.val + " no esta definido");
			else if (sim.GetKind() == clase)
				SemErr(t.val + " es una clase");
			else
				{
				sim = null;
				sim = tabla.GetSimboloRecur(t.val);
				tipoDev = sim.GetType();
				} 
			if (la.kind == 30) {
				Simbolo simbolo_lo_que_tiene_el_vector = new Simbolo(tercetos.darTemporal(),entera,var);
				DarPosVector(sim, simbolo_lo_que_tiene_el_vector);
			}
		} else if (la.kind == 3) {
			Get();
			tipoDev = cadena; 
		} else if (StartOf(10)) {
			simbolo_temp = VExpresion();
			tipoDev = simbolo_temp.GetType();
		} else SynErr(75);
		return tipoDev;
	}

	Simbolo  VCambio_Signo() {
		Simbolo  simbolo_resultado;
		System.out.println("Entramos VCambio_Signo");
		boolean cambio_signo_simple = true; //Esto indicara si la operacion ha terminado o sigue con mas expresiones
		Simbolo simbolo_temp1 = null; //Aqui se almacenara VExpFinal
		simbolo_resultado = null;
		
		
		if (t.val.contentEquals("+") || t.val.contentEquals("-") || t.val.contentEquals("/") ||t.val.contentEquals("*"))
				SemErr("No esta permitido poner dos operandos seguidos. Sugerencia: defina mediante parentesis las expresiones");
		Expect(32);
		if (StartOf(8)) {
			simbolo_temp1 = ValorFinalExp();
			if (simbolo_temp1.GetType() != entera)
			SemErr("Error de tipos en la expresion");
			//Ahora vamos a hacer que t2 = -1
			 Simbolo simbolo_temp2 = new Simbolo(tercetos.darTemporal(),entera,var); //Aqui almacenaremos t2
			 String terceto;
			  terceto =new String (tercetos.asignacion_valor(simbolo_temp2.GetNombre(), -1));
			  tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			  colaTercetos.add(tupla);
			 tabla.InsertarEnActual(simbolo_temp2);
			//t2 = -1 hecho
			//Ahora hay que emitir t3 = t1xt2 
			 Simbolo simbolo_temp3 = new Simbolo(tercetos.darTemporal(),entera,var); //TEMP3 t1xt2
			  terceto = new String(tercetos.operacionBinaria(simbolo_temp1.GetNombre(),simbolo_temp2.GetNombre(),"MUL",simbolo_temp3.GetNombre())); //t3 = t1 * t2
			  tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			  colaTercetos.add(tupla);
			  tabla.InsertarEnActual(simbolo_temp3);
			  
			  simbolo_resultado = simbolo_temp3;
			   
			
			if (StartOf(11)) {
				Simbolo simbolo_temp4 = null;
				if (la.kind == 32 || la.kind == 34) {
					simbolo_temp4 = VExpSuma(simbolo_temp3);
				} else {
					simbolo_temp4 = VExpMul(simbolo_temp3);
				}
				simbolo_resultado = simbolo_temp4;
				if ((simbolo_temp3.GetType() != entera) || (simbolo_temp4.GetType() != entera))
				SemErr("Error de tipos en la expresion");
														
																 	
			}
		} else if (la.kind == 31) {
			Get();
			simbolo_temp1 = VExpresion();
			Expect(38);
			if (simbolo_temp1.GetType() != entera)
			SemErr("VCambio_Signo: Error de tipos en la expresion");
			  Simbolo simbolo_temp2 = new Simbolo(tercetos.darTemporal(),entera,var); //Aqui almacenaremos t2
			  String terceto;
			   terceto =new String (tercetos.asignacion_valor(simbolo_temp2.GetNombre(), -1));
			   tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			   colaTercetos.add(tupla);
			  tabla.InsertarEnActual(simbolo_temp2);
			 //t2 = -1 hecho
			 //Ahora hay que emitir t3 = t1xt2 
			  simbolo_resultado = new Simbolo(tercetos.darTemporal(),entera,var); //TEMP3 t1xt2
			   terceto = new String(tercetos.operacionBinaria(simbolo_temp1.GetNombre(),simbolo_temp2.GetNombre(),"MUL",simbolo_resultado.GetNombre())); //t3 = t1 * t2
			   tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			   colaTercetos.add(tupla);
			   tabla.InsertarEnActual(simbolo_resultado);
										   		
		} else SynErr(76);
		return simbolo_resultado;
	}

	Simbolo  ValorFinalExp() {
		Simbolo  simbolo_resultado;
		Simbolo simbolo = null;
		boolean esta = false;
		int Pos_Vector;
		simbolo_resultado = null;
		int aux = 0;
		System.out.println("Entramos en ValorFinalExp"); 
		if (la.kind == 1) {
			Get();
			if (!(tabla.EstaRecur(t.val)))
			{
			SemErr(t.val + " no estaba declarado previamente");
			}
			else
			{
			//System.out.println("identificador ident!!"+t.val);
			simbolo = tabla.GetSimboloRecur(t.val);
			//System.out.println("Tipo del ident!!"+tipoDev);
			simboloClaseObjeto = simbolo.GetClase();
			esta = true;
			if ((la.val != "(") && (la.val != "."))
				simbolo_resultado = simbolo;
			}
			
			if (la.kind == 28 || la.kind == 30 || la.kind == 31) {
				if (la.kind == 31) {
					Get();
					if  (esta)			// Esto es la llamada a una funcion
					{
					//tipoDev = simbolo.GetTipoRetorno(); //Aqui tendremos que devolver un temporal con el resultado
					}
					
					if (simbolo.GetKind() != funcion)
					SemErr("'" + simbolo.GetNombre() + "' declarado en la linea " + simbolo.GetLine() + " no es una funcion");
					
					VArgumentos(simbolo, aux);
					Expect(38);
				} else if (la.kind == 28) {
					Get();
					Expect(1);
					Simbolo simbolo_metodoatributo = null;
					Simbolo simbolo_clase = simbolo.GetClase();
					TablaSimbolos ambitoclase;
					if (simbolo_clase == null)
							SemErr("El identificador " + simbolo.GetNombre() + " no fue declarado como un objeto de ninguna clase.");
					else
							{
						ambitoclase = simbolo_clase.GetAmbitoAsociado();
						 				 		
						if (!(ambitoclase.Esta(t.val)))
							SemErr(t.val + " no fue declarado dentro de la clase" + simbolo_clase.GetNombre());
						else
							{
							simbolo_metodoatributo = ambitoclase.GetSimbolo(t.val);
							simboloClaseObjeto = simbolo_metodoatributo.GetClase();
						if ((simbolo_metodoatributo.GetVisibilidad() == privado) &&
										(tabla.GetAmbitoActual().Ambito_Padre() != ambitoclase))	//Si el mÃ©todo o atributo es privado
											SemErr(t.val + " es de tipo privado");
							if (simbolo_metodoatributo.GetKind() == metodo)
								{
								//tipoDev = simbolo_metodoatributo.GetTipoRetorno(); //Hay que hacer que en simbolo_resultado se almacene el resultado del metodo
								if (!la.val.contentEquals("("))
									SemErr("Llamada invalida a metodo");
								}
							else if (simbolo_metodoatributo.GetKind() == var)
								{
								//System.out.println("EL TIPO DE " + simbolo_metodoatributo.GetNombre() +" ES " + simbolo_metodoatributo.GetType());
								String nombreatributo = new String (simbolo.GetNombre() + "." + simbolo_metodoatributo.GetNombre());
								System.out.println(nombreatributo);
								//tipoDev = simbolo_metodoatributo.GetType();//Lo mismo, aqui tendremos que emitir el simbolo correspondiente al atributo
								simbolo_resultado=simbolo_metodoatributo;
								//simbolo_resultado.SetType(entera);
											String terceto;
											//terceto = tercetos.asignacion(simbolo_resultado.GetNombre(), nombreatributo);
											//tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
									 	//colaTercetos.add(tupla);
											//tabla.InsertarEnActual(simbolo_resultado);
								
								}
							}
							}
					
					if (la.kind == 31) {
						Get();
						if (simbolo_metodoatributo.GetKind() != metodo) //Este es el caso de que sea un metodo
						SemErr(simbolo_metodoatributo.GetNombre() + " no fue declarado como un metodo");
						else
							{
							simboloClaseObjeto = simbolo_metodoatributo.GetClaseDevuelta();
							//tipoDev = simbolo_metodoatributo.GetTipoRetorno(); //Aqui tendremos que devolver lo que devuelva el metodo
							}
						 
						VArgumentos(simbolo_metodoatributo, aux);
						Expect(38);
					}
				} else {
					DarPosVector(simbolo, simbolo_resultado);
					simbolo_resultado.SetType(entera);
				}
			}
		} else if (la.kind == 2) {
			Get();
			simbolo_resultado = new Simbolo (tercetos.darTemporal(), entera, var);
			String terceto;
			terceto = tercetos.asignacion_valor(simbolo_resultado.GetNombre(), Integer.parseInt(t.val));
			tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			colaTercetos.add(tupla);
			tabla.InsertarEnActual(simbolo_resultado);
			System.out.println(simbolo_resultado.GetNombre());
		} else if (la.kind == 13) {
			Get();
			simbolo_resultado = new Simbolo (tercetos.darTemporal(), bool, var);
			String terceto;  			 
			terceto = tercetos.asignacion_valor(simbolo_resultado.GetNombre(), 1);
			tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			colaTercetos.add(tupla);
			tabla.InsertarEnActual(simbolo_resultado);
		} else if (la.kind == 8) {
			Get();
			simbolo_resultado = new Simbolo (tercetos.darTemporal(), bool, var);
			String terceto;
			terceto = tercetos.asignacion_valor(simbolo_resultado.GetNombre(), 0);
			tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			colaTercetos.add(tupla);
			tabla.InsertarEnActual(simbolo_resultado);
		} else if (la.kind == 3) {
			Get();
			simbolo_resultado = new Simbolo (tercetos.darTemporal(), cadena, var);
			String terceto;
			// asignare a la eti el valor de la cadena.
			 terceto = tercetos.asignacion_cadena(simbolo_resultado.GetNombre(), t.val);
			if (t.val.equals("\"\"")) {
			 SemErr("Error: Cadena nula. La cadena debe contener al menos un caracter.");
			}
			 	 System.out.println("Terceto de la cadena:"+terceto);
			 	 tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			 	 colaTercetos.add(tupla);
			 	 tabla.InsertarEnActual(simbolo_resultado);
		} else SynErr(77);
		return simbolo_resultado;
	}

	Simbolo  VExpSuma(Simbolo simbolo_exp_anterior) {
		Simbolo  simbolo;
		System.out.println("Entramos VExpSuma");
		String operacion = null;
		Simbolo simbolo_resultado = null;
		if (la.kind == 34) {
			Get();
			operacion = new String("SUMA");
		} else if (la.kind == 32) {
			Get();
			operacion = new String("RESTA");
		} else SynErr(78);
		simbolo_resultado = VExpresion();
		if (simbolo_resultado.GetType() != entera)
		SemErr("VExpSuma: Error de tipos en la expresion");
		Simbolo simbolo_temp1 = new Simbolo(tercetos.darTemporal(),entera,var);
		tabla.InsertarEnActual(simbolo_temp1);
		System.out.println(simbolo_resultado.GetNombre()+"CUCU");
		System.out.println(simbolo_temp1.GetNombre()+"CUCU");
		 String terceto = new String(tercetos.operacionBinaria(simbolo_resultado.GetNombre(),simbolo_exp_anterior.GetNombre(),operacion,simbolo_temp1.GetNombre()));
		   tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(),terceto);
		 colaTercetos.add(tupla);
		 simbolo = simbolo_temp1;
		return simbolo;
	}

	Simbolo  VExpMul(Simbolo simbolo_exp_anterior) {
		Simbolo  simbolo;
		System.out.println("Entramos VExpMul");
		Simbolo simbolo_temp1 = null;
		String operador = null;
		simbolo = null;
		if (la.kind == 39) {
			Get();
			operador = new String("MUL");
		} else if (la.kind == 40) {
			Get();
			operador = new String ("DIV");
		} else SynErr(79);
		if (StartOf(8)) {
			simbolo_temp1 = ValorFinalExp();
			Simbolo simbolo_temp2 = new Simbolo(tercetos.darTemporal(),entera,var);
			tabla.InsertarEnActual(simbolo_temp2);
			System.out.println("hola holita!!");
			String terceto = new String(tercetos.operacionBinaria(simbolo_temp1.GetNombre(), simbolo_exp_anterior.GetNombre(), operador, simbolo_temp2.GetNombre()));
			tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			      
			colaTercetos.add(tupla);
			simbolo = simbolo_temp2;
			if (StartOf(11)) {
				if (la.kind == 32 || la.kind == 34) {
					simbolo = VExpSuma(simbolo_temp2);
				} else {
					simbolo = VExpMul(simbolo_temp2);
				}
			}
		} else if (la.kind == 31) {
			Get();
			simbolo_temp1 = VExpresion();
			Expect(38);
			if ((simbolo_temp1.GetType() != entera) || (simbolo_exp_anterior.GetType() != entera))
			SemErr("ExpMul: Error de tipos en la expresion");
			 else
			 		{
			 		System.out.println("YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			 		simbolo = new Simbolo(tercetos.darEtiqueta(),entera,var);
			     tabla.InsertarEnActual(simbolo);
			   String terceto = new String(tercetos.operacionBinaria(simbolo_temp1.GetNombre(), simbolo_exp_anterior.GetNombre(), operador, simbolo.GetNombre()));
			   tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
			   colaTercetos.add(tupla);
			   }
			   
			 		
			
		} else SynErr(80);
		return simbolo;
	}

	Simbolo  VExpOR(Simbolo simbolo_exp_anterior) {
		Simbolo  simbolo;
		System.out.println("Entramos VExpOR");
		Simbolo simbolo_temp = null;
		simbolo = null;
		Expect(54);
		simbolo_temp = VExpresion();
		if ((simbolo_temp.GetType() != bool) || (simbolo_exp_anterior.GetType() != bool)) 
		SemErr("VExpoOR: Error de tipos en la expresion");
		else
				{
				simbolo = new Simbolo(tercetos.darEtiqueta(), bool, var);
				tabla.InsertarEnActual(simbolo);
				String terceto = new String(tercetos.operacionBinaria(simbolo_exp_anterior.GetNombre(), simbolo_temp.GetNombre(), "OR", simbolo.GetNombre()));
				tupla_Tercetos tupla = new tupla_Tercetos(tabla.GetAmbitoActual(), terceto);
				colaTercetos.add(tupla);
				}		
		
		return simbolo;
	}

	void Operador_Aritmetico() {
		if (la.kind == 34) {
			Get();
		} else if (la.kind == 32) {
			Get();
		} else if (la.kind == 39) {
			Get();
		} else if (la.kind == 40) {
			Get();
		} else SynErr(81);
	}

	void Operador_Logico() {
		if (la.kind == 54) {
			Get();
		} else if (la.kind == 53) {
			Get();
		} else if (la.kind == 46) {
			Get();
		} else SynErr(82);
	}

	void Operador_Relacional() {
		switch (la.kind) {
		case 47: {
			Get();
			break;
		}
		case 48: {
			Get();
			break;
		}
		case 49: {
			Get();
			break;
		}
		case 50: {
			Get();
			break;
		}
		case 51: {
			Get();
			break;
		}
		case 52: {
			Get();
			break;
		}
		default: SynErr(83); break;
		}
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		CEMASMAS1();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,T,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "enteros expected"; break;
			case 3: s = "cadenaCar expected"; break;
			case 4: s = "bool expected"; break;
			case 5: s = "boool expected"; break;
			case 6: s = "charr expected"; break;
			case 7: s = "classs expected"; break;
			case 8: s = "ffalse expected"; break;
			case 9: s = "intt expected"; break;
			case 10: s = "new expected"; break;
			case 11: s = "short expected"; break;
			case 12: s = "static expected"; break;
			case 13: s = "ttrue expected"; break;
			case 14: s = "voidd expected"; break;
			case 15: s = "publicc expected"; break;
			case 16: s = "privatte expected"; break;
			case 17: s = "dospuntos_dos expected"; break;
			case 18: s = "rreturn expected"; break;
			case 19: s = "cout expected"; break;
			case 20: s = "cin expected"; break;
			case 21: s = "menor_menor expected"; break;
			case 22: s = "mayor_mayor expected"; break;
			case 23: s = "iff expected"; break;
			case 24: s = "elsse expected"; break;
			case 25: s = "main expected"; break;
			case 26: s = "dosPuntos expected"; break;
			case 27: s = "comma expected"; break;
			case 28: s = "punto expected"; break;
			case 29: s = "llave_ab expected"; break;
			case 30: s = "corchete_ab expected"; break;
			case 31: s = "parent_ab expected"; break;
			case 32: s = "op_menos expected"; break;
			case 33: s = "op_menosmenos expected"; break;
			case 34: s = "op_mas expected"; break;
			case 35: s = "op_masmas expected"; break;
			case 36: s = "llave_ce expected"; break;
			case 37: s = "corchete_ce expected"; break;
			case 38: s = "parent_ce expected"; break;
			case 39: s = "op_producto expected"; break;
			case 40: s = "op_division expected"; break;
			case 41: s = "op_asig expected"; break;
			case 42: s = "puntoComa expected"; break;
			case 43: s = "doblesComillas expected"; break;
			case 44: s = "interrogacion expected"; break;
			case 45: s = "barra_vert expected"; break;
			case 46: s = "op_negacion expected"; break;
			case 47: s = "op_menor expected"; break;
			case 48: s = "op_mayor expected"; break;
			case 49: s = "op_menor_igual expected"; break;
			case 50: s = "op_mayor_igual expected"; break;
			case 51: s = "op_igual expected"; break;
			case 52: s = "op_distinto expected"; break;
			case 53: s = "op_and expected"; break;
			case 54: s = "op_or expected"; break;
			case 55: s = "op_asig_mas expected"; break;
			case 56: s = "op_asig_menos expected"; break;
			case 57: s = "op_asig_producto expected"; break;
			case 58: s = "op_asig_division expected"; break;
			case 59: s = "op_asig_modulo expected"; break;
			case 60: s = "??? expected"; break;
			case 61: s = "invalid CEMASMAS"; break;
			case 62: s = "invalid CEMASMAS"; break;
			case 63: s = "invalid CEMASMAS"; break;
			case 64: s = "invalid Ttipo"; break;
			case 65: s = "invalid Cuerpo_Clase"; break;
			case 66: s = "invalid Parametros"; break;
			case 67: s = "invalid Cuerpo"; break;
			case 68: s = "invalid Cuerpo"; break;
			case 69: s = "invalid Instruccion"; break;
			case 70: s = "invalid Llamada"; break;
			case 71: s = "invalid InstExpresion"; break;
			case 72: s = "invalid VExpresion"; break;
			case 73: s = "invalid InstIfElse"; break;
			case 74: s = "invalid Else"; break;
			case 75: s = "invalid Arg_io"; break;
			case 76: s = "invalid VCambio_Signo"; break;
			case 77: s = "invalid ValorFinalExp"; break;
			case 78: s = "invalid VExpSuma"; break;
			case 79: s = "invalid VExpMul"; break;
			case 80: s = "invalid VExpMul"; break;
			case 81: s = "invalid Operador_Aritmetico"; break;
			case 82: s = "invalid Operador_Logico"; break;
			case 83: s = "invalid Operador_Relacional"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
