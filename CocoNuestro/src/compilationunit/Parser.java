package compilationunit;


public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _enteros = 2;
	public static final int _cadenaCar = 3;
	public static final int _bool = 4;
	public static final int _charr = 5;
	public static final int _classs = 6;
	public static final int _false = 7;
	public static final int _intt = 8;
	public static final int _new = 9;
	public static final int _short = 10;
	public static final int _static = 11;
	public static final int _true = 12;
	public static final int _voidd = 13;
	public static final int _publicc = 14;
	public static final int _privatte = 15;
	public static final int _dospuntos_dos = 16;
	public static final int _rreturn = 17;
	public static final int _cout = 18;
	public static final int _cin = 19;
	public static final int _menor_menor = 20;
	public static final int _mayor_mayor = 21;
	public static final int _iff = 22;
	public static final int _elsse = 23;
	public static final int _main = 24;
	public static final int _dosPuntos = 25;
	public static final int _comma = 26;
	public static final int _punto = 27;
	public static final int _llave_ab = 28;
	public static final int _corchete_ab = 29;
	public static final int _parent_ab = 30;
	public static final int _op_menos = 31;
	public static final int _op_menosmenos = 32;
	public static final int _op_not = 33;
	public static final int _op_mas = 34;
	public static final int _op_masmas = 35;
	public static final int _llave_ce = 36;
	public static final int _corchete_ce = 37;
	public static final int _parent_ce = 38;
	public static final int _op_producto = 39;
	public static final int _op_division = 40;
	public static final int _op_menor = 41;
	public static final int _op_mayor = 42;
	public static final int _op_menor_igual = 43;
	public static final int _op_mayor_igual = 44;
	public static final int _op_igual = 45;
	public static final int _op_asig = 46;
	public static final int _puntoComa = 47;
	public static final int _doblesComillas = 48;
	public static final int _interrogacion = 49;
	public static final int _barra_vert = 50;
	public static final int _op_distinto = 51;
	public static final int _op_and = 52;
	public static final int _op_asig_mas = 53;
	public static final int _op_or = 54;
	public static final int maxT = 55;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	

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
	
	void CEMASMAS() {
		if (la.kind == 6) {
			DecClase();
			CEMASMAS();
		} else if (StartOf(1)) {
			Ttipo();
			if (la.kind == 24) {
				Main();
			} else if (la.kind == 1) {
				Get();
				if (la.kind == 30) {
					Subprograma();
				} else if (la.kind == 46 || la.kind == 47) {
					DecVar();
				} else if (la.kind == 16) {
					DecMetodo();
				} else SynErr(56);
				CEMASMAS();
			} else SynErr(57);
		} else SynErr(58);
	}

	void DecClase() {
		Expect(6);
		Expect(1);
		Expect(28);
		if (StartOf(1)) {
			DecCabMet();
		}
		while (la.kind == 14 || la.kind == 15 || la.kind == 47) {
			if (la.kind == 14 || la.kind == 15) {
				if (la.kind == 14) {
					Get();
				} else {
					Get();
				}
				Expect(25);
				DecCabMet();
			}
			Expect(47);
		}
		Expect(36);
		Expect(47);
	}

	void Ttipo() {
		if (la.kind == 8) {
			Get();
		} else if (la.kind == 4) {
			Get();
		} else if (la.kind == 13) {
			Get();
		} else if (la.kind == 5) {
			Get();
			if (la.kind == 39) {
				Get();
			}
		} else SynErr(59);
	}

	void Main() {
		Expect(24);
		Expect(30);
		if (StartOf(1)) {
			Parametros();
		}
		Expect(38);
		Expect(28);
		Cuerpo();
		Expect(36);
	}

	void Subprograma() {
		Expect(30);
		if (StartOf(1)) {
			Parametros();
		}
		Expect(38);
		Expect(28);
		Cuerpo();
		Expect(36);
	}

	void DecVar() {
		if (la.kind == 46) {
			Get();
			Expresion();
		}
		Expect(47);
	}

	void DecMetodo() {
		Expect(16);
		Expect(1);
		Expect(30);
		if (StartOf(1)) {
			Parametros();
		}
		Expect(38);
		Expect(28);
		Cuerpo();
		Expect(36);
	}

	void DecCabMet() {
		Ttipo();
		Expect(1);
		if (StartOf(2)) {
			while (la.kind == 26) {
				Get();
				Expect(1);
			}
		} else if (la.kind == 30) {
			Get();
			if (StartOf(1)) {
				Ttipo();
				while (la.kind == 26) {
					Get();
					Ttipo();
				}
			}
			Expect(38);
		} else SynErr(60);
	}

	void Parametros() {
		Ttipo();
		Expect(1);
		if (la.kind == 26) {
			while (la.kind == 26) {
				Get();
				Ttipo();
				Expect(1);
			}
		}
	}

	void Cuerpo() {
		while (StartOf(3)) {
			if (StartOf(4)) {
				Instruccion();
			} else {
				Ttipo();
				Expect(1);
				if (la.kind == 30) {
					Subprograma2();
				} else if (la.kind == 46 || la.kind == 47) {
					DecVar();
				} else SynErr(61);
			}
		}
	}

	void Instruccion() {
		if (la.kind == 17) {
			InstReturn();
		} else if (la.kind == 18) {
			InstCout();
		} else if (la.kind == 19) {
			InstCin();
		} else if (la.kind == 1) {
			InstExpresion();
		} else SynErr(62);
	}

	void Subprograma2() {
		Expect(30);
		Parametros();
		Expect(38);
		Expect(28);
		Cuerpo2();
		Expect(36);
	}

	void Cuerpo2() {
		while (StartOf(3)) {
			if (StartOf(4)) {
				Instruccion();
			} else {
				Ttipo();
				Expect(1);
				DecVar();
			}
		}
	}

	void Expresion() {
		Expresion2();
		Expresion1();
	}

	void InstReturn() {
		Expect(17);
		if (StartOf(5)) {
			Expresion();
		}
		Expect(47);
	}

	void InstCout() {
		Expect(18);
		while (la.kind == 20) {
			Get();
			Arg_io();
		}
		Expect(47);
	}

	void InstCin() {
		Expect(19);
		Expect(21);
		Arg_io();
		Expect(47);
	}

	void InstExpresion() {
		Expect(1);
		Expect(46);
		Expresion();
		Expect(47);
	}

	void InstCond() {
	}

	void Arg_io() {
		if (la.kind == 1) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else SynErr(63);
	}

	void Expresion2() {
		Expresion3();
		Expresion21();
	}

	void Expresion1() {
		if (la.kind == 49) {
			Get();
			Expresion();
			Expect(25);
			Expresion();
			Expresion1();
		}
	}

	void Expresion3() {
		Expresion4();
		Expresion31();
	}

	void Expresion21() {
		if (la.kind == 41) {
			Get();
			Expresion3();
			Expresion21();
		}
	}

	void Expresion4() {
		if (la.kind == 33) {
			Get();
			Expresion4();
		} else if (StartOf(6)) {
			Expresion5();
		} else SynErr(64);
	}

	void Expresion31() {
		if (la.kind == 34) {
			Get();
			Expresion4();
			Expresion31();
		}
	}

	void Expresion5() {
		switch (la.kind) {
		case 30: {
			Get();
			Expresion();
			Expect(38);
			break;
		}
		case 9: {
			Get();
			Expect(1);
			Expect(30);
			Argumentos();
			Expect(38);
			break;
		}
		case 1: {
			Get();
			if (la.kind == 27 || la.kind == 29 || la.kind == 30) {
				if (la.kind == 27) {
					Get();
					Expect(1);
					if (la.kind == 30) {
						Get();
						Argumentos();
						Expect(38);
					}
				} else if (la.kind == 30) {
					Get();
					Argumentos();
					Expect(38);
				} else {
					Get();
					Expresion();
					Expect(37);
				}
			}
			break;
		}
		case 2: {
			Get();
			break;
		}
		case 3: {
			Get();
			break;
		}
		case 12: {
			Get();
			break;
		}
		case 7: {
			Get();
			break;
		}
		default: SynErr(65); break;
		}
	}

	void Argumentos() {
		if (StartOf(5)) {
			Expresion();
			while (la.kind == 26) {
				Get();
				Argumentos();
			}
		}
	}

	void Operador() {
		if (la.kind == 34) {
			Get();
		} else if (la.kind == 31) {
			Get();
		} else if (la.kind == 39) {
			Get();
		} else if (la.kind == 40) {
			Get();
		} else SynErr(66);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		CEMASMAS();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, T,T,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,T,x,x, T,x,x,x, x,T,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,T, x,x,x,T, x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,T, x,x,x,T, x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x}

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
			case 5: s = "charr expected"; break;
			case 6: s = "classs expected"; break;
			case 7: s = "false expected"; break;
			case 8: s = "intt expected"; break;
			case 9: s = "new expected"; break;
			case 10: s = "short expected"; break;
			case 11: s = "static expected"; break;
			case 12: s = "true expected"; break;
			case 13: s = "voidd expected"; break;
			case 14: s = "publicc expected"; break;
			case 15: s = "privatte expected"; break;
			case 16: s = "dospuntos_dos expected"; break;
			case 17: s = "rreturn expected"; break;
			case 18: s = "cout expected"; break;
			case 19: s = "cin expected"; break;
			case 20: s = "menor_menor expected"; break;
			case 21: s = "mayor_mayor expected"; break;
			case 22: s = "iff expected"; break;
			case 23: s = "elsse expected"; break;
			case 24: s = "main expected"; break;
			case 25: s = "dosPuntos expected"; break;
			case 26: s = "comma expected"; break;
			case 27: s = "punto expected"; break;
			case 28: s = "llave_ab expected"; break;
			case 29: s = "corchete_ab expected"; break;
			case 30: s = "parent_ab expected"; break;
			case 31: s = "op_menos expected"; break;
			case 32: s = "op_menosmenos expected"; break;
			case 33: s = "op_not expected"; break;
			case 34: s = "op_mas expected"; break;
			case 35: s = "op_masmas expected"; break;
			case 36: s = "llave_ce expected"; break;
			case 37: s = "corchete_ce expected"; break;
			case 38: s = "parent_ce expected"; break;
			case 39: s = "op_producto expected"; break;
			case 40: s = "op_division expected"; break;
			case 41: s = "op_menor expected"; break;
			case 42: s = "op_mayor expected"; break;
			case 43: s = "op_menor_igual expected"; break;
			case 44: s = "op_mayor_igual expected"; break;
			case 45: s = "op_igual expected"; break;
			case 46: s = "op_asig expected"; break;
			case 47: s = "puntoComa expected"; break;
			case 48: s = "doblesComillas expected"; break;
			case 49: s = "interrogacion expected"; break;
			case 50: s = "barra_vert expected"; break;
			case 51: s = "op_distinto expected"; break;
			case 52: s = "op_and expected"; break;
			case 53: s = "op_asig_mas expected"; break;
			case 54: s = "op_or expected"; break;
			case 55: s = "??? expected"; break;
			case 56: s = "invalid CEMASMAS"; break;
			case 57: s = "invalid CEMASMAS"; break;
			case 58: s = "invalid CEMASMAS"; break;
			case 59: s = "invalid Ttipo"; break;
			case 60: s = "invalid DecCabMet"; break;
			case 61: s = "invalid Cuerpo"; break;
			case 62: s = "invalid Instruccion"; break;
			case 63: s = "invalid Arg_io"; break;
			case 64: s = "invalid Expresion4"; break;
			case 65: s = "invalid Expresion5"; break;
			case 66: s = "invalid Operador"; break;
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
