package compilationunit;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;

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

	final int undef=0, entera=1, bool=2, cadena=3, vacio=4, identificador=5;
	// DeclaraciÃ³n de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3, parametro=4;
	//DeclaraciÃ³n de visibilidad
	final int privado=0, publico=1;

	// Tabla de simbolos global
	public Tablas tabla;
	// Tupla devuelta por las expresiones (tipo, valor)

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
	}

	void CEMASMAS() {
		int type=undef; 
		int type1;
		
		if (la.kind == 7 || la.kind == 15 || la.kind == 16) {
			DecClase();
			CEMASMAS();
		} else if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
				System.out.println("Hola!"+t.val+" cuyo type vale:"+type);
				if (type==identificador) {
				Simbolo simbolo = new Simbolo(t.val, type, 0);
				simbolo.SetLine(t.line);
				simbolo.SetColumn(t.col);
				if ((tabla.EstaEnActual(simbolo.GetNombre()))) {
					System.out.println(t.val+ " declarado anteriormente, esta ok!");
					//tabla.InsertarEnActual(simbolo);
				} else {
					System.out.println("Simbolo NO declarado, serÃ¡ return de otra cosa");
					System.out.println("=======Falta meter return de esto!!");
					//SemErr(t.val + " No declarado anteriormente");
				}
				}
				
			} else if (la.kind == 14) {
				Get();
			} else {
				Get();
				Simbolo simbolo = new Simbolo(t.val, type, 0);
				simbolo.SetLine(t.line);
				System.out.println("Declarando objeto de clase");
				simbolo.SetColumn(t.col);
				if (tabla.EstaEnActual(simbolo.GetNombre())) {
						Simbolo simbolonuevo = tabla.GetSimboloRecur(t.val);
						SemErr(simbolonuevo.GetNombre() + " ya estaba declarado en la linea " + simbolonuevo.GetLine() + " columna " + simbolonuevo.GetColumn());
					} else {
				 tabla.InsertarEnActual(simbolo);
				}
				
			}
			if (la.kind == 25) {
				Main(type);
			} else if (la.kind == 1) {
				Get();
				Simbolo simbolo = new Simbolo(t.val, type, 0);
				simbolo.SetLine(t.line);
				simbolo.SetColumn(t.col);
				System.out.println("Entraste en ident CEMASMAS, detras del main");
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
					System.out.println("Entras a Subprograma"); 
					Subprograma(simbolo);
				} else if (la.kind == 30) {
					Vector(simbolo);
					Expect(42);
				} else if (la.kind == 41 || la.kind == 42) {
					type1 = DecVar(simbolo);
					if (type1==type) {
					//System.out.println("tipos ok!"+type+" "+type1);
					}
					else if (type1==undef) {
						//System.out.println("tipos ok!__No has inicializado la var, pero ok!");
					} 
					else {
						SemErr("Tipos: Error: arg1="+type+",arg2=" + type1);
					}
					//System.out.println("El valor del simbolo es:"+simbolo.GetValor()); 
					
				} else if (la.kind == 17) {
					System.out.println("Entraste en DecMetodo: "+t.val+" con metodo "+la.val);
					String Clase= (String) t.val; 
					DecMetodo(Clase);
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

	void Main(int type) {
		Simbolo simbolo = new Simbolo("main",type,funcion);
		Expect(25);
		tabla.NuevoAmbito(simbolo);
		simbolo.SetKind (funcion);
		simbolo.SetTipoRetorno(simbolo.GetType());
		Expect(31);
		if (StartOf(1)) {
			Parametros(simbolo);
		}
		Expect(38);
		Expect(29);
		Cuerpo();
		Expect(36);
	}

	void Subprograma(Simbolo simbolo) {
		tabla.NuevoAmbito(simbolo);
		simbolo.SetKind (funcion);
		simbolo.SetTipoRetorno(simbolo.GetType());  
		Expect(31);
		if (StartOf(1)) {
			Parametros(simbolo);
		}
		Expect(38);
		Expect(29);
		Cuerpo();
		tabla.CerrarAmbito(); 
		Expect(36);
	}

	void Vector(Simbolo simbolo) {
		simbolo.SetToVector();
		Simbolo sim = new Simbolo ("Temp", 0,0);
		int type;
		Expect(30);
		if (StartOf(3)) {
			type = Exp(sim);
			((Number)sim.GetValor()).intValue();
			System.out.println("El tamano del vector es " + (Number)sim.GetValor());
		}
		Expect(37);
	}

	int  DecVar(Simbolo simbolo) {
		int  type;
		int type1=undef;
		//System.out.println("Estas en DecVar");
		
		if (la.kind == 41) {
			Get();
			type1 = Exp(simbolo);
			System.out.println("Variable inicializada a " + simbolo.GetValor()); 
		}
		Expect(42);
		type=type1; 
		return type;
	}

	void DecMetodo(String Clase) {
		Simbolo sim = new Simbolo("no def", 0, 0);		// AÃ±ado metodo al ambito actual
		System.out.println("Estas en declaracion Metodo");
		
		Expect(17);
		Expect(1);
		System.out.println("Comprobando si el metodo "+t.val+" pertenece a la clase "+Clase);
		if ((tabla.EstaEnActual(Clase) &&
			(tabla.GetSimboloRecur(Clase) != null)) &&
				(tabla.GetSimboloRecur(Clase).GetKind()==clase) ) {
			System.out.println("Clase existe todo ok!");
		} else {
			SemErr("No existe una clase con este mÃ©todo asociado");
		}
		
		Expect(31);
		if (StartOf(1)) {
			Parametros(sim);
		}
		Expect(38);
		Expect(29);
		Cuerpo();
		Expect(36);
	}

	void Cuerpo_Clase(int visible, Simbolo simClase) {
		int type;
		
		if (StartOf(1)) {
			while (StartOf(1)) {
				if (StartOf(2)) {
					type = Ttipo();
					Expect(1);
					Simbolo sim = new Simbolo(t.val, 0, 0);	// Creacion del simbolo
					sim.SetVisibilidad(visible);
					sim.SetLine(t.line);
					    sim.SetColumn(t.col);
					tabla.InsertarEnActual(sim);
					
					if (la.kind == 41 || la.kind == 42) {
						sim.SetKind(var);	// Es variable
						sim.SetType(type);	// Su tipo
						
						type = DecVar(sim);
					} else if (la.kind == 31) {
						sim.SetKind(metodo);
						sim.SetTipoRetorno(type);
						sim.SetClase(simClase);
						
						DecCabMet(sim);
					} else SynErr(65);
				} else {
					Simbolo sim = new Simbolo(t.val, 0, 0);
					Get();
					Expect(1);
					DecCabMet(sim);
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
		int nparam=0; 
		if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
				Simbolo sim = new Simbolo("Arg_Metodo",0,0); 
				if (la.kind == 30) {
					Vector(sim);
					System.out.println("Funcionalidad por hacer");
				}
				nparam=nparam+1;	// Un parametro mas	
				sim.SetType(type);
				simbolo.SetValor(sim);	// aÃ±ado el simbolo al metodo	
				
				if (la.kind == 27) {
					Get();
					Param_Cab(simbolo);
					nparam=nparam+1; 
				}
			} else {
				Get();
				Simbolo simb1 = new Simbolo("Arg_Metodo",0,0);
				nparam=nparam+1;	// Un parametro mas	
				simb1.SetType(vacio);
				simbolo.SetValor(simb1);	// aÃ±ado el simbolo al metodo
				
				if (la.kind == 27) {
					Get();
					Param_Cab(simbolo);
					nparam=nparam+1; 
				}
			}
		}
		simbolo.SetNParametros(nparam);
		System.out.println("Numero de argumentos del metodo:"+simbolo.GetNParametros());
		
	}

	void Parametros(Simbolo simbolo_nombre_funcion) {
		int type; 
		if (StartOf(2)) {
			type = Ttipo();
			Expect(1);
			Simbolo simbolo_parametro = new Simbolo(t.val, type, parametro);
			simbolo_parametro.SetLine(t.line);
			simbolo_parametro.SetColumn(t.col);
			simbolo_nombre_funcion.AnadirParametro(simbolo_parametro);
			tabla.InsertarEnActual(simbolo_parametro); 
			if (la.kind == 30) {
				Vector(simbolo_parametro);
			}
			if (la.kind == 27) {
				while (la.kind == 27) {
					Get();
					type = Ttipo();
					Expect(1);
					simbolo_parametro = new Simbolo(t.val, type, parametro);
					simbolo_nombre_funcion.AnadirParametro(simbolo_parametro);
					tabla.InsertarEnActual(simbolo_parametro);
					if (la.kind == 30) {
						Vector(simbolo_parametro);
					}
				}
			}
		} else if (la.kind == 14) {
			Get();
		} else SynErr(66);
	}

	void Cuerpo() {
		int type = undef; 
		Simbolo simbolo_anterior = null;
		boolean estaba_declarado = false;
		while (StartOf(4)) {
			if (StartOf(5)) {
				Instruccion();
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
					System.out.println("El simbolo estaba declarado en la TS");
					}
				}
				
				if (StartOf(6)) {
					InstExpresion(simbolo_anterior);
				} else if (la.kind == 1) {
					Get();
					if (estaba_declarado)
					{
					if (simbolo_anterior.GetKind() != clase) 
					{
					SemErr("Se esperaba un tipo o un identificador declarado como una clase: " + t.val + " declarado en la linea " + simbolo_anterior.GetLine() + " columna " + simbolo_anterior.GetColumn() + " no corresponde a una clase");
						}
					} 
					Simbolo simbolo = new Simbolo(t.val, type, 0);
					  simbolo.SetLine(t.line);
					 	  simbolo.SetColumn(t.col);
					 	  simbolo.SetClase(simbolo_anterior);
					 
					 	  if (tabla.EstaEnActual(simbolo.GetNombre()))
					 			{
					 			Simbolo simbolonuevo = tabla.GetSimboloRecur(t.val);
					 			SemErr(simbolonuevo.GetNombre() + " ya estaba declarado en la linea " + simbolonuevo.GetLine() + " columna " + simbolonuevo.GetColumn());
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
							type = DecVar(simbolo);
						}
					} else SynErr(67);
				} else SynErr(68);
			}
		}
	}

	void Instruccion() {
		int type; 
		if (la.kind == 18) {
			System.out.println("Estamos en Instruccion");
			type  = InstReturn();
		} else if (la.kind == 19) {
			InstCout();
		} else if (la.kind == 20) {
			InstCin();
		} else if (la.kind == 23) {
			InstIfElse();
		} else SynErr(69);
	}

	void InstExpresion(Simbolo simbolo) {
		int type=undef;
		System.out.println("Entramos en InstExpresion"); 
		if (la.kind == 31) {
			Get();
			VArgumentos(simbolo, 0);
			Expect(38);
			Expect(42);
		} else if (la.kind == 28) {
			Get();
			Expect(1);
			Expect(31);
			VArgumentos(simbolo, 0);
			Expect(38);
			Expect(42);
		} else if (StartOf(7)) {
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
			type = VExpresion();
			if (simbolo.GetType() != type)
				SemErr("El tipo del identificador no coincide con el tipo de la expresion");
			
			Expect(42);
		} else SynErr(70);
	}

	int  Exp(Simbolo sim) {
		int  tipoDev;
		tipoDev=undef;				// Si devuelvo este tipo es q algo fue mal!
		int valor=0, suma;
		int numErr=errors.count;	// Numero de errores actuales
		
		if (StartOf(8)) {
			suma = ExpAritmetica();
			if (numErr == errors.count) {	// todo fue ok!
			System.out.println("vas a sumar a "+valor+" el valor de"+suma);
			valor=suma+valor; 
			} 
		}
		if (numErr == errors.count) {
		sim.SetValor(valor);
		tipoDev=entera;
		} 
		return tipoDev;
	}

	void LlamMet() {
		Expect(1);
		Expect(28);
		Expect(1);
		while (la.kind == 31) {
			if (la.kind == 31) {
				Get();
				Expect(38);
			} else {
				Get();
				while (StartOf(9)) {
					Argumentos();
				}
				Expect(38);
			}
		}
	}

	void Argumentos() {
		int type=undef; 
		if (StartOf(10)) {
			type = Expresion();
			while (la.kind == 27) {
				Get();
				Argumentos();
			}
		}
	}

	int  InstReturn() {
		int  tipoDev;
		tipoDev = undef;
		int type; 
		System.out.println("Entramos en InstReturn");
		Expect(18);
		if (StartOf(11)) {
			type = VExpresion();
			tipoDev= type; 
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

	void InstIfElse() {
		int type, type1;
		Simbolo simbolo = null; //new Simbolo(t.val, 0, 0);	// borrarcuando corrigas
		
		Expect(23);
		type = Expresion();
		if (type != bool) {SemErr("La condicion de un if debe ser logica");}
		simbolo = new Simbolo ("IF", 0, 0);
		if (la.kind == 29) {
			Get();
			tabla.NuevoAmbito(simbolo); 
			Cuerpo();
			tabla.CerrarAmbito(); 
			Expect(36);
		} else if (StartOf(5)) {
			Instruccion();
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
		} else SynErr(71);
		if (la.kind == 24) {
			Else();
		}
	}

	void VArgumentos(Simbolo simbolo, int pos) {
		int type=undef;
		Simbolo simbolo_nuevo = null; 
		System.out.println("Entramos en VArgumentos");
		if (StartOf(11)) {
			type = VExpresion();
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
			 
			if (la.kind == 27) {
				Get();
				VArgumentos(simbolo, pos + 1);
			}
		}
	}

	int  VExpresion() {
		int  tipoDev;
		System.out.println("Entramos en VExpresion");
		tipoDev = ValorFinalExp();
		return tipoDev;
	}

	int  Expresion() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		type = Expresion2();
		type1 = Expresion1();
		if (type==type1) {
		tipoDev=type;
		} else if (type1==undef) {
			tipoDev=type;
		} else {
			SemErr("Error en Expresion");
		}
		
		return tipoDev;
	}

	void Else() {
		int type;
		Simbolo sim = new Simbolo("ELSE", 0, 0);	// borrarcuando corrigas
		
		Expect(24);
		if (la.kind == 29) {
			Get();
			tabla.NuevoAmbito(sim); 
			Cuerpo();
			tabla.CerrarAmbito(); 
			Expect(36);
		} else if (StartOf(5)) {
			Instruccion();
		} else SynErr(72);
	}

	int  Arg_io() {
		int  tipoDev;
		tipoDev=undef;
		Simbolo sim = new Simbolo("Temp",0,0);
		int posicion; 
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
				posicion = DarPosVector(sim);
			}
		} else if (la.kind == 3) {
			Get();
			tipoDev = cadena; 
		} else if (StartOf(12)) {
			tipoDev = Exp(sim);
			tipoDev = entera; 
		} else SynErr(73);
		return tipoDev;
	}

	int  DarPosVector(Simbolo sim) {
		int  posicion;
		Simbolo simbolo_nuevo = new Simbolo("temp", 0, 0);
		int tipoDev; 
		Expect(30);
		if (!(sim.Es_Vector()))
		SemErr(sim.GetNombre() + " definido en la linea " + sim.GetLine() + " columna " + sim.GetColumn() + " no es un vector");
		tipoDev = Exp(simbolo_nuevo);
		Expect(37);
		if (tipoDev != entera)
		SemErr("La posicion del vector debe ser una expresion de tipo entero");
		posicion = ((Number) simbolo_nuevo.GetValor()).intValue(); 
		return posicion;
	}

	int  Expresion2() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		type = Expresion3();
		type1 = Expresion21();
		if (type1==undef) {	// no hay segunda parte expr
		tipoDev=type;
		} else if (type1==entera) {	// Expresion con op_relacional
			if (type==type1) {
				tipoDev=entera;		// op_relacional ok!
			} else {
				SemErr("OpRelacional: Error argumentos.");
			}
		} else if (type1==bool) {	// Expresion con op_logico
			if (type==type1) {
				tipoDev=bool;	// op_logico ok!
			} else {
				SemErr("OpLogica: Error argumentos");
			}
		} else {
			SemErr("Operacion Expr2 problems"+type+" "+type1);
		}
		
		return tipoDev;
	}

	int  Expresion1() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1, type2; 
		
		if (la.kind == 44) {
			Get();
			type = Expresion();
			Expect(26);
			type1 = Expresion();
			type2 = Expresion1();
		}
		return tipoDev;
	}

	int  Expresion3() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		type = Expresion4();
		type1 = Expresion31();
		if (type1==undef) {
		tipoDev=type;
		} else if ((type==type1) && (type1==entera)) {
			tipoDev=type;
		} else {
			SemErr("Operacion Arit sobre tipos no enteros");
		}
		 
		return tipoDev;
	}

	int  Expresion21() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		if (StartOf(13)) {
			if (la.kind == 46 || la.kind == 53 || la.kind == 54) {
				Operador_Logico();
				type = Expresion3();
				type1 = Expresion21();
				if (type==bool) {
				if (type1==undef) {	// todo ok!
					tipoDev=type;
				} else if (type1==bool) {	//2Âº ok!
					tipoDev=type;
				} else {				// 2Âº arg problem
					SemErr("OpLogica: Error argumentos.");
				}
				} else {	// error en 1Âº arg
					tipoDev=type;	// lo envio para q salte el error
					SemErr("OpLogica: Error argumentos.");
				}
				
			}
		} else if (StartOf(14)) {
			if (StartOf(15)) {
				Operador_Relacional();
				type = Expresion3();
				type1 = Expresion21();
				if (type==entera) {
				if (type1==undef) {	// todo ok!
					tipoDev=type;
				} else if (type1==entera) {	//2Âº ok!
					tipoDev=type;
				} else {				// 2Âº arg problem
					SemErr("OpRelacional: Error argumentos");
				}
				} else {	// error en 1Âº arg
					tipoDev=type;	// lo envio para q salte el error
												 							SemErr("OpRelacional: Error argumentos");
				}
				
			}
		} else SynErr(74);
		return tipoDev;
	}

	void Operador_Logico() {
		if (la.kind == 54) {
			Get();
		} else if (la.kind == 53) {
			Get();
		} else if (la.kind == 46) {
			Get();
		} else SynErr(75);
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
		default: SynErr(76); break;
		}
	}

	int  ExpAritmetica() {
		int  suma;
		int numErr=errors.count;	// Numero de errores actuales
		int valor=0, valor1=0, valor2=0;		// inicializo
		suma=0;
		System.out.println("Estas en ExpArirmetica!");
		
		valor = ExpProducto();
		if (numErr == errors.count) {	// todo fue ok!
		System.out.println("Una parte vale"+valor);
		suma= valor;
		} 
		if (la.kind == 32 || la.kind == 34) {
			while (la.kind == 32 || la.kind == 34) {
				if (la.kind == 34) {
					Get();
					valor1 = ExpProducto();
					if (numErr == errors.count) {	// todo fue ok! 	
					System.out.println("sumando! a "+suma+"el nuevo "+valor1);
					//	sim.SetValor(((Number) sim.GetValor()).intValue() + valor1);
						suma= suma+valor1; 
						} 
				} else {
					System.out.println("antes de restar!");
					Get();
					valor2 = ExpProducto();
					if (numErr == errors.count) {	// todo fue ok!
					System.out.println("restando!");
					//sim.SetValor(((Number) sim.GetValor()).intValue() - valor2);
					suma= suma-valor2; 
					} 
				}
			}
		}
		System.out.println("Simbolo sale con valor:"+suma); 
		return suma;
	}

	int  ExpProducto() {
		int  total;
		int valor=0, valor1=0, valor2=1;
		total=0;
		System.out.println("Estas en expProducto"+la.val);
		
		valor = ExpCambioSigno();
		total=valor; 
		if (la.kind == 39 || la.kind == 40) {
			while (la.kind == 39 || la.kind == 40) {
				if (la.kind == 39) {
					Get();
					valor1 = ExpCambioSigno();
					System.out.println("multiplicaste!");
					total=total * valor1; 
				} else {
					Get();
					valor2 = ExpCambioSigno();
					System.out.println("dividiste!");
					total=total / valor2; 
				}
			}
		}
		System.out.println("Valor de una parte"+total); 
		return total;
	}

	int  ExpCambioSigno() {
		int  valor;
		int valor1=0;	// inicializo
		int sum=0;
		valor=0;		// Inicialiazo
		System.out.println("Entraste en camb Signo");
		
		if (la.kind == 32) {
			Get();
			valor1 = ExpCambioSigno();
			valor = -valor1; 
		} else if (la.kind == 31) {
			Get();
			int suma = ExpAritmetica();
			Expect(38);
			valor=suma;		
		} else if (la.kind == 1) {
			Get();
			if (tabla.EstaEnActual(t.val)) {	// busco si esta el simbolo
			Simbolo sim = tabla.GetSimboloRecur(t.val);
			if (sim.GetKind()==var) {	// Es var. entera
				valor = ((Number) sim.GetValor()).intValue();	// asigno el val del ident
			} else if (((sim.GetKind()==funcion) || (sim.GetKind()==metodo))	// funcion o metodo 
							&& (sim.GetTipoRetorno()==entera)) {	// Devuelve un entero
				System.out.println("NO HAY ASIGNACION TODAVIA");
				System.out.println("Identificador de una funcion o metodo que devuelve un entero, ok!");
			} else {	// sino valor entero->error"
				SemErr("Identificador no devuelve Entero!");
			}
			} else {
				SemErr("Error: Identificador no existe");
			}
			
		} else if (la.kind == 2) {
			Get();
			valor = Integer.parseInt(t.val); 
		} else if (StartOf(16)) {
			valor = ExpOperadores();
			if (valor==entera)	{
			System.out.println("es entero");
			} else {
				SemErr("No es entero!es un:"+t.val);
			}
			
		} else SynErr(77);
		return valor;
	}

	int  ExpOperadores() {
		int  tipoDev;
		tipoDev=undef;
		int type1, type;
		System.out.println("estas en expoperadores");
		
		if (StartOf(17)) {
		} else if (la.kind == 3) {
			Get();
			tipoDev=cadena;	
		} else if (la.kind == 13) {
			Get();
			tipoDev=bool;	
		} else if (la.kind == 8) {
			Get();
			tipoDev=bool;	
		} else SynErr(78);
		return tipoDev;
	}

	int  Expresion4() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1, type2;
		
		if (la.kind == 46) {
			Get();
			type = Expresion4();
			if (type==bool) {
			tipoDev=type;
			} else {
				SemErr("OpNegLogico: Error argumento.");
			}
			
		} else if (la.kind == 32) {
			Get();
			type1 = Expresion4();
			if (type1==entera) {
			tipoDev=type1;
			} else {
				SemErr("OpNegAritmetico: Error argumento.");
			}
			
		} else if (StartOf(18)) {
			type2 = Expresion5();
			tipoDev=type2; 
		} else SynErr(79);
		return tipoDev;
	}

	int  Expresion31() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		if (StartOf(19)) {
			Operador_Aritmetico();
			type = Expresion4();
			type1 = Expresion31();
			if (type1==undef) {	// No hay 2Âº parte
			if (type==entera) {	// todo ok!
				tipoDev=type;
			} else {			// NO es un tipo ENTERO
				SemErr("Operacion Arit con arg no entero");
			}
			} else {	// hay otro expresion
				if ((type==type1) && (type==entera)) {	// las dos son
					tipoDev=type;
				} else {
					SemErr("Operacion Arit sobre tipos no enteros");
				}
			}
			 
		}
		return tipoDev;
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
		} else SynErr(80);
	}

	int  Expresion5() {
		int  tipoDev;
		tipoDev=undef;
		int type1, type;
		
		switch (la.kind) {
		case 31: {
			Get();
			type1 = Expresion();
			Expect(38);
			tipoDev=type1; 
			break;
		}
		case 10: {
			Get();
			Expect(1);
			Expect(31);
			Argumentos();
			Expect(38);
			break;
		}
		case 1: {
			Get();
			if (la.kind == 28 || la.kind == 30 || la.kind == 31) {
				if (la.kind == 28) {
					Get();
					Expect(1);
					if (la.kind == 31) {
						Get();
						Argumentos();
						Expect(38);
					}
				} else if (la.kind == 31) {
					Get();
					Argumentos();
					Expect(38);
				} else {
					Get();
					type = Expresion();
					Expect(37);
					tipoDev=type;
				}
			}
			break;
		}
		case 2: {
			Get();
			tipoDev=entera;	
			break;
		}
		case 3: {
			Get();
			tipoDev=cadena;	
			break;
		}
		case 13: {
			Get();
			tipoDev=bool;	
			break;
		}
		case 8: {
			Get();
			tipoDev=bool;	
			break;
		}
		default: SynErr(81); break;
		}
		return tipoDev;
	}

	void Expresion_Entera() {
		if (la.kind == 1 || la.kind == 2) {
			if (la.kind == 2) {
				Get();
			} else {
				Get();
			}
			if (StartOf(19)) {
				Operador_Aritmetico();
				Expresion_Entera();
			}
		} else if (la.kind == 31) {
			Get();
			Expresion_Entera();
			Expect(38);
			if (StartOf(19)) {
				Operador_Aritmetico();
				Expresion_Entera();
			}
		} else SynErr(82);
	}

	int  ValorFinalExp() {
		int  tipoDev;
		tipoDev = undef;
		Simbolo simbolo = null;
		boolean esta = false;
		int Pos_Vector;
		int aux = 0; 
		if (la.kind == 1) {
			Get();
			if (!(tabla.EstaRecur(t.val)))
			{
			SemErr(t.val + " no estaba declarado previamente");
			}
			else
			{
			simbolo = tabla.GetSimboloRecur(t.val);
			tipoDev = simbolo.GetType();
			esta = true;
			}
			
			if (la.kind == 28 || la.kind == 30 || la.kind == 31) {
				if (la.kind == 31) {
					Get();
					if  (esta)
					{
					tipoDev = simbolo.GetTipoRetorno();
					} 
					VArgumentos(simbolo, aux);
					Expect(38);
				} else if (la.kind == 28) {
					Get();
					Expect(1);
					Expect(31);
					VArgumentos(simbolo, aux);
					Expect(38);
				} else {
					Get();
					Pos_Vector = DarPosVector(simbolo);
					if (Pos_Vector >= simbolo.GetTamano())
					{
						SemErr("Posicion del vector fuera de rango.");
						}
						
					Expect(37);
				}
			}
		} else if (la.kind == 2) {
			Get();
			tipoDev = entera;
		} else if (la.kind == 13) {
			Get();
			tipoDev = bool;
		} else if (la.kind == 8) {
			Get();
			tipoDev = bool;
		} else if (la.kind == 3) {
			Get();
			tipoDev = cadena;
		} else SynErr(83);
		return tipoDev;
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
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,T,x, x,T,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,T,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,T, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,T,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,T, T,x,T,x, x,x,x,T, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,T,T, T,x,x,T, x,x,T,T, x,T,x,T, T,x,x,x, x,T,T,x, x,x,x,x, T,x,T,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,T,T, T,x,x,T, x,x,T,T, x,T,x,T, T,x,x,x, x,T,T,x, x,x,x,x, T,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x},
		{x,x,x,T, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,T,T, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,T,T, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
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
			case 70: s = "invalid InstExpresion"; break;
			case 71: s = "invalid InstIfElse"; break;
			case 72: s = "invalid Else"; break;
			case 73: s = "invalid Arg_io"; break;
			case 74: s = "invalid Expresion21"; break;
			case 75: s = "invalid Operador_Logico"; break;
			case 76: s = "invalid Operador_Relacional"; break;
			case 77: s = "invalid ExpCambioSigno"; break;
			case 78: s = "invalid ExpOperadores"; break;
			case 79: s = "invalid Expresion4"; break;
			case 80: s = "invalid Operador_Aritmetico"; break;
			case 81: s = "invalid Expresion5"; break;
			case 82: s = "invalid Expresion_Entera"; break;
			case 83: s = "invalid ValorFinalExp"; break;
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
