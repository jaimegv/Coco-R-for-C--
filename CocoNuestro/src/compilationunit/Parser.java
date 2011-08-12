package compilationunit;

public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _enteros = 2;
	public static final int _cadenaCar = 3;
	public static final int _bool = 4;
	public static final int _boool = 5;
	public static final int _charr = 6;
	public static final int _classs = 7;
	public static final int _false = 8;
	public static final int _intt = 9;
	public static final int _new = 10;
	public static final int _short = 11;
	public static final int _static = 12;
	public static final int _true = 13;
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

	final int undef=0, entera=1, bool=2, cadena=3;
	// DeclaraciÃ³n de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3, parametro=4;

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
		CEMASMAS(tabla);
	}

	void CEMASMAS(Tablas tabla) {
		int type=undef; 
		int type1;
		
		if (la.kind == 7 || la.kind == 15 || la.kind == 16) {
			DecClase();
			CEMASMAS(tabla);
		} else if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
			} else {
				Get();
			}
			if (la.kind == 25) {
				Main();
			} else if (la.kind == 1) {
				Get();
				Simbolo simbolo = new Simbolo(t.val, type, 0);
				tabla.InsertarEnActual(simbolo);
				
				if (la.kind == 31) {
					Subprograma(simbolo);
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
					System.out.println("El valor del simbolo es:"+simbolo.GetValor()); 
					
				} else if (la.kind == 17) {
					DecMetodo();
				} else SynErr(61);
				CEMASMAS(tabla);
			} else SynErr(62);
		} else SynErr(63);
	}

	void DecClase() {
		int type; 
		if (la.kind == 15 || la.kind == 16) {
			if (la.kind == 15) {
				Get();
			} else {
				Get();
			}
		}
		Expect(7);
		Expect(1);
		Expect(29);
		Cuerpo_Clase();
		if (la.kind == 15 || la.kind == 16) {
			if (la.kind == 15) {
				Get();
				Expect(26);
				Cuerpo_Clase();
				if (la.kind == 16) {
					Get();
					Expect(26);
					Cuerpo_Clase();
				}
			} else {
				Get();
				Expect(26);
				Cuerpo_Clase();
				if (la.kind == 15) {
					Get();
					Expect(26);
					Cuerpo_Clase();
				}
			}
		}
		Expect(36);
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
		} else SynErr(64);
		return type;
	}

	void Main() {
		Simbolo simbolo = new Simbolo("main",0,funcion);
		Expect(25);
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
		tabla.NuevoAmbito();
		simbolo.SetKind (funcion);  
		Expect(31);
		if (StartOf(1)) {
			Parametros(simbolo);
		}
		Expect(38);
		Expect(29);
		Cuerpo();
		Expect(36);
	}

	int  DecVar(Simbolo simbolo) {
		int  type;
		int type1=undef;
		System.out.println("Estas en DecVar");
		
		if (la.kind == 41) {
			Get();
			type1 = expAritmetica(simbolo);
			System.out.println("Variable inicializada a " + simbolo.GetValor()); 
		}
		Expect(42);
		type=type1;
		
		return type;
	}

	void DecMetodo() {
		Simbolo sim = new Simbolo("no def", 0, 0);
		Expect(17);
		Expect(1);
		Expect(31);
		if (StartOf(1)) {
			Parametros(sim);
		}
		Expect(38);
		Expect(29);
		Cuerpo();
		Expect(36);
	}

	void Cuerpo_Clase() {
		int type;
		Simbolo sim = new Simbolo(t.val, 0, 0);
		if (StartOf(1)) {
			while (StartOf(1)) {
				if (StartOf(2)) {
					type = Ttipo();
					Expect(1);
					if (la.kind == 41 || la.kind == 42) {
						type = DecVar(sim);
					} else if (la.kind == 31) {
						DecCabMet();
					} else SynErr(65);
				} else {
					Get();
					Expect(1);
					DecCabMet();
				}
			}
		}
	}

	void DecCabMet() {
		int type; 
		Expect(31);
		Param_Cab();
		Expect(38);
		Expect(42);
	}

	void Param_Cab() {
		int type; 
		if (StartOf(1)) {
			if (StartOf(2)) {
				type = Ttipo();
				if (la.kind == 30) {
					Vector();
				}
				if (la.kind == 27) {
					while (la.kind == 27) {
						Get();
						type = Ttipo();
						if (la.kind == 30) {
							Vector();
						}
					}
				}
			} else {
				Get();
			}
		}
	}

	void Vector() {
		Expect(30);
		if (la.kind == 1 || la.kind == 2 || la.kind == 31) {
			Expresion_Entera();
		}
		Expect(37);
	}

	void Parametros(Simbolo simbolo_nombre_funcion) {
		int type; 
		if (StartOf(2)) {
			type = Ttipo();
			Expect(1);
			Simbolo simbolo_parametro = new Simbolo(t.val, type, parametro);
			simbolo_nombre_funcion.AnadirParametro(simbolo_parametro);
			tabla.InsertarEnActual(simbolo_parametro); 
			if (la.kind == 30) {
				Vector();
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
						Vector();
					}
				}
			}
		} else if (la.kind == 14) {
			Get();
		} else SynErr(66);
	}

	void Cuerpo() {
		int type;
		Simbolo sim = new Simbolo(t.val, 0, 0);// borrar cuando sigas
		
		while (StartOf(3)) {
			if (StartOf(4)) {
				Instruccion();
			} else {
				if (StartOf(2)) {
					type = Ttipo();
				} else {
					Get();
				}
				Expect(1);
				if (la.kind == 31) {
					Subprograma(sim);
				} else if (la.kind == 30 || la.kind == 41 || la.kind == 42) {
					if (la.kind == 30) {
						Vector();
					}
					type = DecVar(sim);
					if (la.kind == 41) {
						Get();
						if (la.kind == 1) {
							Get();
						} else if (la.kind == 2) {
							Get();
						} else if (la.kind == 3) {
							Get();
						} else SynErr(67);
					}
				} else SynErr(68);
			}
		}
	}

	void Instruccion() {
		int type; 
		if (la.kind == 18) {
			type  = InstReturn();
		} else if (la.kind == 19) {
			InstCout();
		} else if (la.kind == 20) {
			InstCin();
		} else if (la.kind == 1) {
			InstExpresion();
		} else if (la.kind == 23) {
			InstIfElse();
		} else SynErr(69);
	}

	int  expAritmetica(Simbolo sim) {
		int  tipoDev;
		tipoDev=undef;
		int tipo, tipo1;
		
		tipo = expProducto(sim);
		if (la.kind == 39 || la.kind == 40) {
			while (la.kind == 39 || la.kind == 40) {
				if (la.kind == 39) {
					Get();
				} else {
					Get();
				}
				tipo1 = expProducto(sim);
			}
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
			if (StartOf(5)) {
				Operador_Aritmetico();
				Expresion_Entera();
			}
		} else if (la.kind == 31) {
			Get();
			Expresion_Entera();
			Expect(38);
			if (StartOf(5)) {
				Operador_Aritmetico();
				Expresion_Entera();
			}
		} else SynErr(70);
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
				while (StartOf(6)) {
					Argumentos();
				}
				Expect(38);
			}
		}
	}

	void Argumentos() {
		int type=undef; 
		if (StartOf(7)) {
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
		Expect(18);
		if (StartOf(7)) {
			type = Expresion();
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

	void InstExpresion() {
		int type=undef; 
		Expect(1);
		if (la.kind == 28) {
			Get();
			Expect(1);
		}
		if (la.kind == 31) {
			Get();
			Argumentos();
			Expect(38);
			Expect(42);
		} else if (StartOf(8)) {
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
			type = Expresion();
			Expect(42);
		} else SynErr(71);
	}

	void InstIfElse() {
		int type, type1;
		Simbolo sim = new Simbolo(t.val, 0, 0);
		Simbolo simbolo = new Simbolo(t.val, 0, 0);	// borrarcuando corrigas
		
		Expect(23);
		type = Expresion();
		if (type != bool) {SemErr("La condicion de un if debe ser logica");} 
		if (la.kind == 29) {
			Get();
			Cuerpo();
			Expect(36);
		} else if (StartOf(9)) {
			if (StartOf(4)) {
				Instruccion();
			} else {
				type = Ttipo();
				Expect(1);
				if (la.kind == 31) {
					Subprograma(simbolo);
				} else if (la.kind == 30 || la.kind == 41 || la.kind == 42) {
					if (la.kind == 30) {
						Vector();
					}
					type = DecVar(sim);
					if (la.kind == 41) {
						Get();
						if (la.kind == 1) {
							Get();
						} else if (la.kind == 2) {
							Get();
						} else if (la.kind == 3) {
							Get();
						} else SynErr(72);
					}
				} else SynErr(73);
			}
		} else SynErr(74);
		if (la.kind == 24) {
			Else();
		}
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
		Simbolo sim = new Simbolo(t.val, 0, 0);	// borrarcuando corrigas
		
		Expect(24);
		if (la.kind == 29) {
			Get();
			Cuerpo();
			Expect(36);
		} else if (StartOf(9)) {
			if (StartOf(4)) {
				Instruccion();
			} else {
				type = Ttipo();
				Expect(1);
				if (la.kind == 31) {
					Subprograma(sim);
				} else if (la.kind == 30 || la.kind == 41 || la.kind == 42) {
					if (la.kind == 30) {
						Vector();
					}
					type = DecVar(sim);
					if (la.kind == 41) {
						Get();
						if (la.kind == 1) {
							Get();
						} else if (la.kind == 2) {
							Get();
						} else if (la.kind == 3) {
							Get();
						} else SynErr(75);
					}
				} else SynErr(76);
			}
		} else SynErr(77);
	}

	int  Arg_io() {
		int  tipoDev;
		tipoDev=undef; 
		if (la.kind == 1) {
			Get();
			if (la.kind == 30) {
				Vector();
			}
		} else if (la.kind == 3) {
			Get();
			tipoDev = cadena; 
		} else if (la.kind == 21 || la.kind == 42) {
		} else SynErr(78);
		return tipoDev;
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
		
		if (StartOf(10)) {
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
		} else if (StartOf(11)) {
			if (StartOf(12)) {
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
		} else SynErr(79);
		return tipoDev;
	}

	void Operador_Logico() {
		if (la.kind == 54) {
			Get();
		} else if (la.kind == 53) {
			Get();
		} else if (la.kind == 46) {
			Get();
		} else SynErr(80);
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
		default: SynErr(81); break;
		}
	}

	int  expProducto(Simbolo sim) {
		int  tipoDev;
		tipoDev=undef;
		int tipo, tipo1;
		
		tipo = expCambioSigno(sim);
		if (la.kind == 39 || la.kind == 40) {
			while (la.kind == 39 || la.kind == 40) {
				if (la.kind == 39) {
					Get();
				} else {
					Get();
				}
				tipo1 = expCambioSigno(sim);
				
			}
		}
		return tipoDev;
	}

	int  expCambioSigno(Simbolo sim) {
		int  tipoDev;
		tipoDev=undef;
		
		Expect(2);
		sim.SetValor(Integer.parseInt(t.val));
		//			t.val=Integer.parseInt(t.val); 	
		
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
			
		} else if (StartOf(13)) {
			type2 = Expresion5();
			tipoDev=type2; 
		} else SynErr(82);
		return tipoDev;
	}

	int  Expresion31() {
		int  tipoDev;
		tipoDev=undef;
		int type, type1;
		
		if (StartOf(5)) {
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
		} else SynErr(83);
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
		default: SynErr(84); break;
		}
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
		{x,x,x,x, T,T,T,x, x,T,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, T,T,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,T,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x},
		{x,T,x,x, T,T,T,x, x,T,x,x, x,x,x,x, x,x,T,T, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, T,T,T,x, x,T,x,x, x,x,T,T, T,x,x,T, x,x,T,T, x,T,x,T, T,x,x,x, x,T,T,x, x,x,T,x, T,x,T,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, T,T,T,x, x,T,x,x, x,x,T,T, T,x,x,T, x,x,T,T, x,T,x,T, T,x,x,x, x,T,T,x, x,x,T,x, T,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x}

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
			case 8: s = "false expected"; break;
			case 9: s = "intt expected"; break;
			case 10: s = "new expected"; break;
			case 11: s = "short expected"; break;
			case 12: s = "static expected"; break;
			case 13: s = "true expected"; break;
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
			case 70: s = "invalid Expresion_Entera"; break;
			case 71: s = "invalid InstExpresion"; break;
			case 72: s = "invalid InstIfElse"; break;
			case 73: s = "invalid InstIfElse"; break;
			case 74: s = "invalid InstIfElse"; break;
			case 75: s = "invalid Else"; break;
			case 76: s = "invalid Else"; break;
			case 77: s = "invalid Else"; break;
			case 78: s = "invalid Arg_io"; break;
			case 79: s = "invalid Expresion21"; break;
			case 80: s = "invalid Operador_Logico"; break;
			case 81: s = "invalid Operador_Relacional"; break;
			case 82: s = "invalid Expresion4"; break;
			case 83: s = "invalid Operador_Aritmetico"; break;
			case 84: s = "invalid Expresion5"; break;
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
