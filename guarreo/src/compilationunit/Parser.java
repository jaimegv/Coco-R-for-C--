package compilationunit;



import java.io.*;
import java.util.*;


public class Parser {
    
    final int _EOF = 0;
    final int _ident = 1;
    final int _enteros = 2;
    final int _cadenaCar = 3;
    final int _boolean = 4;
    final int _char = 5;
    final int _class = 6;
    final int _false = 7;
    final int _int = 8;
    final int _new = 9;
    final int _short = 10;
    final int _static = 11;
    final int _true = 12;
    final int _void = 13;
    final int _dosPuntos = 14;
    final int _comma = 15;
    final int _punto = 16;
    final int _llaveIzda = 17;
    final int _corcheteIzda = 18;
    final int _parentesisIzda = 19;
    final int _menos = 20;
    final int _not = 21;
    final int _mas = 22;
    final int _llaveDer = 23;
    final int _corcheteDer = 24;
    final int _parentesisDer = 25;
    final int _multiplicacion = 26;
    final int _div = 27;
    final int _menor = 28;
    final int _mayor = 29;
    final int _igual = 30;
    final int _puntoComa = 31;
    final int _doblesComillas = 32;
    final int _interrogacion = 33;
    final int maxT = 41;

    final boolean T = true;
    final boolean x = false;
    final int minErrDist = 2;

    public  Token token;    // last recognized token   /* pdt */
    public Token la;       // lookahead token
    int errDist = minErrDist;

    TablaSimbolos tablasimbolos = new TablaSimbolos();
    Simbolo simbolo = new Simbolo();
    Simbolo simbolo_aux = new Simbolo();
    Simbolo simbolo_aux2 = new Simbolo();
    Argumento argumento;
    LinkedList<Argumento> colaArgumento = new LinkedList<Argumento> ();
    int numargmetodo;
    int numargmetodo_aux;
    int aux = 0;
    java.io.PrintStream impresion = null;
    Tercetos tercetos = new Tercetos();
    Stack<String> pila_temp = new Stack();
    Token next;
    Token next2;
    LinkedList<tupla_Tercetos> colaTercetos = new LinkedList<tupla_Tercetos> ();
    LinkedList<tupla_Tercetos> colaMain = new LinkedList<tupla_Tercetos> ();
    String terceto_actual;
    String clase_actual = "";
    String metodo_actual = "";
     String identificador_asigna; // Variable global que nos va a servir para coger la parte izda de una asignacion
    String lugar_expresion; // Variable global que se utiliza para obtener el lugar de una expresion     
     Boolean esta_en_main = false; //para saber si estamos compilando el metodo main

    GCF codigoFinal;
    String ficherosalida;

    char tipo_asig = '-';
    int num_args;
    boolean vengode_decl_corch = false;
    boolean vengode_decl_parent = false;
    boolean vengode_arg_declaracion = false;
    boolean estoy_for = false;
    boolean hay_return = false;
    boolean es_constructor = false;
    boolean condicional = false;
    
    boolean problema_del_for = false;
    tupla_Tercetos terceto_for;

    // Variable booleana para indicar si se trata de una asignacion del tipo z = 1;

    boolean tiene_valor_asignacion = false;
    int valor;                 // Valor que devuelve la parte dcha de la asignacion
    
    void SynErr (int n) {
        if (errDist >= minErrDist) Errors.SynErr(la.line, la.col, n);
        errDist = 0;
    }

    public  void SemErr (String msg) {
        if (errDist >= minErrDist) Errors.Error(token.line, token.col, msg); /* pdt */
        errDist = 0;
    }

    public  void SemError (String msg) {
        if (errDist >= minErrDist) Errors.Error(token.line, token.col, msg); /* pdt */
        errDist = 0;
    }

    public  void Warning (String msg) { /* pdt */
            if (errDist >= minErrDist) Errors.Warn(token.line, token.col, msg);
            errDist = 0;
    }

    public  boolean Successful() { /* pdt */
            return Errors.count == 0;
    }

    public  String LexString() { /* pdt */
            return token.val;
    }

    public  String LookAheadString() { /* pdt */
            return la.val;
    }


    public Simbolo Copia (Simbolo simb){
        Simbolo simbolo_aux3 = new Simbolo();
        simbolo_aux3.setId(simb.getId());
        simbolo_aux3.setTipo(simb.getTipo());
        simbolo_aux3.setCol(simb.getColumna());
        simbolo_aux3.setLinea(simb.getLinea());
        simbolo_aux3.setTipoRetorno(simb.getTipoRetorno());
        simbolo_aux3.setPublico(simb.getPublico());
        simbolo_aux3.setClaseRelacionada(simb.getClaseRelacionada());
        simbolo_aux3.setColaArgumentos(simb.getColaArgumentos());
        simbolo_aux3.setNumArgMetodo(simb.getNumArgMetodo());
        simbolo_aux3.setTipoEnVector(simb.getTipoEnVector());
        simbolo_aux3.setTamano(simb.getTamano());
        simbolo_aux3.setPos(simb.getPosicion());
        simbolo_aux3.setInicializado(simb.getInicializado());
        return simbolo_aux3;
    }
    
    void Esperado (int n){
        Expect(n);
        //Para cargar un nuevo token se usa este metodo
        //y dado que sólo hay un simbolo (global), este metodo se encarga de
        //llenar de basura sus campos para que no se lean valores del token anterior.

        simbolo.setPos(token.pos);     // token position in the source text (starting at 0)
        simbolo.setCol(token.col);     // token column (starting at 0)
        simbolo.setLinea(token.line);    // token line (starting at 1)
        simbolo.setId(token.val);
        simbolo.setClaseRelacionada("");
        simbolo.setColaArgumentos(null);
        simbolo.setNumArgMetodo(0);
        simbolo.setPublico(false);
        simbolo.setTamano(0);
        simbolo.setTipo('-');
        simbolo.setTipoEnVector('-');
        simbolo.setTipoRetorno('-');
        simbolo.setInicializado(false);

    }
    
    void Inicializa_aux(){
        
        try{
            simbolo_aux.setPos(0);     // token position in the source text (starting at 0)
            simbolo_aux.setCol(0);     // token column (starting at 0)
            simbolo_aux.setLinea(0);    // token line (starting at 1)
            simbolo_aux.setId("");
            simbolo_aux.setClaseRelacionada("");
            simbolo_aux.setColaArgumentos(null);
            simbolo_aux.setNumArgMetodo(0);
            simbolo_aux.setPublico(false);
            simbolo_aux.setTamano(0);
            simbolo_aux.setTipo('-');
            simbolo_aux.setTipoEnVector('-');
            simbolo_aux.setTipoRetorno('-');
            simbolo_aux.setInicializado(false);
        }catch(NullPointerException e){
            simbolo_aux = new Simbolo();
        }

    }
    
    
    void Get () {
        for (;;) {
            token = la; /* pdt */
            la = Scanner.Scan();
            if (la.kind <= maxT) { ++errDist; break; }

            la = token; /* pdt */
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
        boolean[] s = new boolean[maxT+1];
        if (la.kind == n) { Get(); return true; }
        else if (StartOf(repFol)) return false;
        else {
                for (int i=0; i <= maxT; i++) {
                        s[i] = set[syFol][i] || set[repFol][i] || set[0][i];
                }
                SynErr(n);
                while (!s[la.kind]) Get();
                return StartOf(syFol);
        }
    }
    
    void compilationunit() {
        while (la.kind == 6) {
                Clases();
        }
    }
    
    void Clases() {
        Esperado(6);
        Esperado(1);
        simbolo.setTipo('x');
        if (tablasimbolos.t_actual != tablasimbolos.abuelo) SemError("Declaración de clase anidada");
        Simbolo simb = new Simbolo();
        simb = Copia(simbolo);
        int existe = tablasimbolos.InsertarSimbolo(simb, simb.getId());
        if (existe != 0) SemError("Nombre de clase repetido");
        tablasimbolos.CrearTablaPadre(simbolo.getId());
        clase_actual = simbolo.getId();
        Esperado(17);
        Miembros();
        Esperado(23);
        tablasimbolos.t_actual = tablasimbolos.abuelo;
    }
    
    void TiposSimples() {
        if (la.kind == 8) {
            simbolo_aux.setTipo('e');
            simbolo_aux.setTipoEnVector('e');
            simbolo_aux.setTipoRetorno('e');
            simbolo_aux.setTamano(1);
            Get();
        } else if (la.kind == 4) {
            simbolo_aux.setTipo('b');
            simbolo_aux.setTipoEnVector('b');
            simbolo_aux.setTipoRetorno('b');
            simbolo_aux.setTamano(1);
            Get();
        } else if (la.kind == 13) {
            simbolo_aux.setTipoRetorno('n');
            Get();
        } else SynErr(42);
    }

//    Declaraciones = ["public" | "private"] TiposSimples (ident | "main") [";" | "["enteros"]" [";"] | "(" ["void" |ArgumentosDeclaracion] ")""{"Miembros"}"]

    void Declaraciones() {
        tupla_Tercetos tupla;
             
        // ["public" | "private"]
        Inicializa_aux();
        if (la.kind == 34 || la.kind == 35) {
            if (la.kind == 34) {
             simbolo_aux.setPublico(true);
             Get();
            }
            else {
             Get();
            }
        }
        TiposSimples();

        // ident || main
        // 1º ident
        
        if (la.kind == 1) {
            simbolo_aux.setId(la.val);
            simbolo_aux.setCol(la.col);
            simbolo_aux.setPos(la.pos);
            simbolo_aux.setLinea(la.line);
            Get();
        
            // main
        } else if (la.kind == 36) {
            simbolo_aux.setId(la.val);
            simbolo_aux.setCol(la.col);
            simbolo_aux.setPos(la.pos);
            simbolo_aux.setLinea(la.line);
            if (!simbolo_aux.getPublico())
                SemErr("Falta modificador \"public\" en metodo main");
            tablasimbolos.setExisteMain(true);
            Get();
                    

        } else SynErr(43);

        // 31 = ";" || 19 = "(" || 18 = "" "["
        
        if (la.kind == 18 || la.kind == 19 || la.kind == 31) {
            
            // Es un ";" por lo tanto es una simple declaracion

            if (la.kind == 31) {
                simbolo_aux.setTipoEnVector('-');
                simbolo_aux.setTipoRetorno('-');
                if (simbolo_aux.getId() == "main")
                    SynErr(25);
                Simbolo simb = new Simbolo();
                simb = Copia(simbolo_aux);

                int existe = tablasimbolos.InsertarSimbolo(simb, simb.getId());
                if (existe != 0) SemErr("Ya existe el identificador");
                Get();

                // Es un "[" por lo tanto se trata de la declaracion de un vector

            }else if (la.kind == 18) {
                simbolo_aux.setTipoRetorno('-');
                simbolo_aux.setTipo('v');
                if (simbolo_aux.getId() == "main")
                    SynErr(25);
                Get();
                Esperado(2);
                if (!token.val.equals("[") ){
                    int entero = Integer.valueOf(token.val).intValue();
                    if (entero < 0)
                        SemError("Imposible crear vector de tamaño negativo");
                    simbolo_aux.setNumArgMetodo(entero);
                    simbolo_aux.setTamano(entero * simbolo_aux.getTamano());
                }
                Esperado(24);
                Esperado(31);
                Simbolo simb = new Simbolo();
                simb = Copia(simbolo_aux);

                // Ha terminado de declarar el vector, ahora comprueba si ya existe

                int existe = tablasimbolos.InsertarSimbolo(simb, simb.getId());
                if (existe != 0) SemErr("Ya existe el identitificador");
            } else {
                hay_return = false;
                Get();

                // Si no es ";", ni es "[", se trata de la declaracion de un método

                metodo_actual = simbolo_aux.getId();
                simbolo_aux.setTipo('m');
                simbolo_aux.setTamano(0);
                simbolo_aux.setTipoEnVector('-');
                //generamos el terceto para insertar etiqueta si no es el metodo main
                if (!metodo_actual.equals("main")){
                    terceto_actual = tercetos.EtiquetaMetodo(simbolo_aux.getId());
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    colaTercetos.add(tupla);
                }
                

                if (StartOf(1)) {

                   // Argumentos del metodo = void
                   if (la.kind == 13) {
                       if (!simbolo_aux.getId().equals("main")){
                           SynErr(36);
                       }
                       Get();
                   }
                   else if (la.kind == 25)
                        simbolo_aux.setNumArgMetodo(0);
                   else {
                        Simbolo simbolo_aux3 = new Simbolo();
                        simbolo_aux3.setId(simbolo_aux.getId());
                        simbolo_aux3.setTipo(simbolo_aux.getTipo());
                        simbolo_aux3.setCol(simbolo_aux.getColumna());
                        simbolo_aux3.setLinea(simbolo_aux.getLinea());
                        simbolo_aux3.setTipoRetorno(simbolo_aux.getTipoRetorno());
                        simbolo_aux3.setPublico(simbolo_aux.getPublico());
                        simbolo_aux.setNumArgMetodo(0);
                        colaArgumento.clear();
                        numargmetodo = 0;
                        ArgumentosDeclaracion();
                        simbolo_aux.setId(simbolo_aux3.getId());
                        simbolo_aux.setTipo(simbolo_aux3.getTipo());
                        simbolo_aux.setCol(simbolo_aux3.getColumna());
                        simbolo_aux.setLinea(simbolo_aux3.getLinea());
                        simbolo_aux.setTipoRetorno(simbolo_aux3.getTipoRetorno());

                        simbolo_aux.setPublico(simbolo_aux3.getPublico());
                        simbolo_aux.setTipo('m');

                   }
                }
                Simbolo simb = new Simbolo();
                simb = Copia(simbolo_aux);
                simb.setTamano(0);

                int existe1 = tablasimbolos.InsertarSimbolo(simb, simb.getId());
                if (existe1 != 0)
                    SemErr("Ya existe un metodo con ese identificador en esta clase");
                tablasimbolos.CrearTablaHijo(simb.getId());
                int args = simb.getNumArgMetodo();
                Queue colaArgumento = simb.getColaArgumentos();
                Argumento arg;
                Object arrayArg[] = null;
                if (args > 0)
                    arrayArg = colaArgumento.toArray();
                int i = 0;
                while (args > 0){
                    arg = (Argumento) arrayArg[i];
                    i = i+1;
                    Inicializa_aux();
                    simbolo_aux.setTipo(arg.getTipo());
                    if (arg.getTipo()=='e') simbolo_aux.setTamano(1);
                    if (arg.getTipo()=='b') simbolo_aux.setTamano(1);
                    if (arg.getTipo()=='v') {
                       int X = simbolo_aux.getNumArgMetodo();
                       int Y = simbolo_aux.getTipoEnVector();
                       if (arg.getTipo()=='e') simbolo_aux.setTamano(1 * X);
                       else if (arg.getTipo()=='b') simbolo_aux.setTamano(X);
                    }
                    simbolo_aux.setId(arg.getNombre());
                    Simbolo simb2 = new Simbolo();
                    simb2 = Copia(simbolo_aux);

                    int existe = tablasimbolos.InsertarSimbolo(simb2, simb2.getId());
                    if (existe != 0) SemErr("Existen dos parámetros con el mismo identificador");
                    args = args - 1;
                }
                Esperado(25);
                Esperado(17);
                Miembros();

                
                if ((hay_return == false)&&(simb.getTipoRetorno()!='n')){
                    SemErr("El metodo debe contener al menos un \"return\".");
                }
                
                
                Esperado(23);
                
                //Ya hemos cogido la llave de cierre asi que generamos el terceto que termina el main
                if (metodo_actual.equals("main")){
                    terceto_actual = tercetos.termina_Main();
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    colaMain.add(tupla);
                }
                tablasimbolos.t_actual = tablasimbolos.t_actual.getSuperior();
            }
        }
    }
    
    void DeclaracionesArgumentos(){
        TiposSimples();
        char typo = simbolo_aux.getTipo();
        Esperado(1);
        argumento = new Argumento(typo, token.val);
        colaArgumento.offer(argumento);
        simbolo_aux.setColaArgumentos(colaArgumento);
        simbolo_aux.setNumArgMetodo(simbolo_aux.getNumArgMetodo() + 1);
        numargmetodo = simbolo_aux.getNumArgMetodo() + 1;
    }

    void ArgumentosDeclaracion() {
        if (StartOf(1)) {
                DeclaracionesArgumentos();
                while (la.kind == 15) {
                        Get();
                        ArgumentosDeclaracion();
                }
        }
    }
    
    void Miembros() {
        while (StartOf(2)) {
                if (StartOf(1)) {
                        Declaraciones();
                } else {
                        Sentencias();
                }
        }
    }

//    Asignaciones = ident ["." ident ["("Argumentos")" ";"] | ident ";" | "["Expresion"]" | "("[ArgumentosDeclaracion | Argumentos]")"["{"Miembros"}" |";"]] ALLMetodo
    
    void Asignaciones(){
        int num_arg_fi, fi;
        tupla_id tup;
        tupla_Tercetos tup_terc;
        String parte_izqda;
        LinkedList<Argumento> cola_arg_aux = new LinkedList<Argumento> ();
        String argumentos [];
        String ident;
        boolean es_objeto = false;
        boolean es_metodo = false;

        Inicializa_aux();
        Esperado(1);
             
        Simbolo asigna_simbol = new Simbolo();
        asigna_simbol = tablasimbolos.Buscar(simbolo.getId());
        if (asigna_simbol == null && (la.kind != 1)){
            SemErr("El identificador es desconocido");
            return;
        }
        else if (asigna_simbol != null){
            tipo_asig = asigna_simbol.getTipo();
            identificador_asigna = asigna_simbol.getId();
        }
        
        // Aqui se lee el primer identificador
        ident = la.val;
        
             if (StartOf(3)) {
                 if (la.kind == 16) {
                     if (asigna_simbol == null){
                        SemErr("El identificador no se corresponde con ningún objeto declarado");
                     }
                     if (asigna_simbol.getTipo() != 'o') {
                        SemError("Se esperaba una variable tipo objeto");
                     }
                     String clase_asociada = asigna_simbol.getClaseRelacionada();
                     TablaSimb tio;
                    tio = tablasimbolos.BuscarTio(clase_asociada);
                    if (tio== null) SemError("No existe la clase relacionada");
                    Get();
                    Esperado(1);
                    ident = ident + "." + la.val;
                    asigna_simbol = tablasimbolos.BuscarEn(tio, simbolo.getId());
                    if (asigna_simbol == null) SemErr("No existe ningún atributo o metodo en la clase "+clase_asociada+" con ese identificador");
                    if (la.kind == 19 || la.kind == 31) {
                        if (la.kind == 19) {
                            if (asigna_simbol == null){
                                SemError("ERROR: No se ha encontrado el símbolo");
                                return;
                            }
                            if (asigna_simbol.getTipo() != 'm') SemErr("Sólo un método puede llevar argumentos");
                            //if (tipo_asig != 'm') SemErr("Sólo un método puede llevar argumentos");
                            else{
                                tipo_asig = asigna_simbol.getTipoRetorno();
                                if (asigna_simbol.getColaArgumentos() != null)
                                    colaArgumento = asigna_simbol.getColaArgumentos();
                                cola_arg_aux = asigna_simbol.getColaArgumentos();
                            }
                            Get();
                            Argumentos();
                            Esperado(25);
                            if (la.kind == 31){
                                Esperado(31);
                                num_arg_fi = asigna_simbol.getNumArgMetodo();
                        /* Convierte los elementos de la cola en elementos de un String
                         * para poder pasarlos por el terceto */
                                argumentos = new String [num_arg_fi];
                                Argumento arg;
                                for (fi = 0; fi > num_arg_fi; fi++){
                                    arg = cola_arg_aux.removeFirst();
                                    argumentos [fi] = arg.getNombre().toString();
                                }
                                ident = ident + argumentos;
                                identificador_asigna = ident;
                                
                            }
                            else
                                if (la.kind != 15)
                                    SemErr("Se esperaba ;");
                        }
                    }
                    if (la.kind == 31){
                        tipo_asig = asigna_simbol.getTipo();
                        identificador_asigna = asigna_simbol.getId();
                        Esperado(31);
                    }
                    
                    if (la.kind == 30){
                        if (asigna_simbol != null)
                            tipo_asig = asigna_simbol.getTipo();
                    }
                 }
                 else if (la.kind == 1) { //declaracion de objetos
                     es_objeto = true;
                     TablaSimb tio;
                     tio = tablasimbolos.BuscarTio(token.val);
                     if (tio == null) SemErr("No existe ninguna clase declarada con ese identificador");
                     else{
                         Simbolo simb = new Simbolo(la.val, 'o',la.line);
                         simb.setCol(la.col);
                         simb.setPos(la.pos);
                         simb.setClaseRelacionada(token.val);
                         simb.setTamano(1);
                         int existe = tablasimbolos.InsertarSimbolo(simb, la.val);
                         if (existe != 0) SemErr("Existe otro elemento con el mismo identificador");
                         Get();
                         Esperado(31);
                     }
                     
                    // Vectooorrrrrrrrrrrrrrrrrrrrrrr
                 }else if (la.kind == 18) {
                     if (asigna_simbol== null) SemErr("No existe ningún vector con ese identificador");
                     Get();
                     tupla_id c;
                     c = Expresion();
                     if (c.getTipo() != 'e') SemError("La expresion debe ser matemática");
                     tipo_asig = asigna_simbol.getTipoEnVector();
                     identificador_asigna = asigna_simbol.getId()+"["+c.getId()+"]";
                     Esperado(24);
                 }
                 else if ((la.kind == 19) || (la.kind == 31)) {
                     tipo_asig = asigna_simbol.getTipo();
                     if (asigna_simbol.getTipo()=='x'){
                         metodo_actual = asigna_simbol.getId();
                         es_constructor = true;
                         asigna_simbol.setTipo('m');
                     }
                     if ((la.kind == 19)&&(es_constructor == false)) {
                         if (asigna_simbol.getTipo() != 'm') SemErr("Sólo un método puede llevar argumentos");
                    //if (tipo_asig != 'm') SemErr("Sólo un método puede llevar argumentos");
                         else{
                             tipo_asig = asigna_simbol.getTipoRetorno();
                             if (asigna_simbol.getColaArgumentos() != null)
                                 colaArgumento = asigna_simbol.getColaArgumentos();
                             numargmetodo = asigna_simbol.getNumArgMetodo();
                             numargmetodo_aux = numargmetodo;
                             aux = 0;
                         }
                         
                         Get();
                         
                         //generamos el terceto de la llamada a la funcion
                         terceto_actual = tercetos.funcion_en_terceto(simbolo.getId(), numargmetodo);
                         tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                         if (metodo_actual.equals("main"))
                            colaMain.add(tup_terc);
                         else
                            colaTercetos.add(tup_terc);
                         
                         Argumentos();
                         Esperado(25);
                         if (la.kind == 31)
                             Esperado(31);
                         else
                             if (la.kind != 15)
                                 SemErr("Se esperaba ;");
                         
                         es_metodo = true;
                     }
                     
                     if ((la.kind==19)&&(es_constructor == true)){
                         tipo_asig = 'o';
                         asigna_simbol.setLinea(token.line);
                         asigna_simbol.setCol(token.col);
                         asigna_simbol.setPos(token.pos);
                         Get();
                         simbolo_aux.setNumArgMetodo(0);
                         colaArgumento.clear();
                         numargmetodo = 0;
                         ArgumentosDeclaracion();
                         LinkedList<Argumento> colaArgumento_aux = new LinkedList<Argumento> ();
                         int numargmetodo_aux = simbolo_aux.getNumArgMetodo();
                         colaArgumento_aux = simbolo_aux.getColaArgumentos();
                         asigna_simbol.setColaArgumentos(colaArgumento_aux);
                         asigna_simbol.setNumArgMetodo(numargmetodo_aux);
                         asigna_simbol.setTamano(0);
                         
                         int existe1 = tablasimbolos.InsertarSimbolo(asigna_simbol, asigna_simbol.getId());
                         if (existe1 != 0)
                             SemErr("Ya existe un metodo con ese identificador en esta clase");
                         tablasimbolos.CrearTablaHijo(asigna_simbol.getId());
                         int args = asigna_simbol.getNumArgMetodo();
                         LinkedList colaArgumento = new  LinkedList<Argumento> ();
                         colaArgumento = asigna_simbol.getColaArgumentos();
                         simbolo_aux = Copia(asigna_simbol);
                         Argumento arg;
                         Object arrayArg[];
                         arrayArg = colaArgumento.toArray();
                         
                         int i = 0;
                         while (args > 0){
                             arg = (Argumento) arrayArg[i];
                             i = i+1;
                             Inicializa_aux();
                             simbolo_aux.setTipo(arg.getTipo());
                             if (arg.getTipo()=='e') simbolo_aux.setTamano(1);
                             if (arg.getTipo()=='b') simbolo_aux.setTamano(1);
                             if (arg.getTipo()=='v') {
                                 int X = simbolo_aux.getNumArgMetodo();
                                 int Y = simbolo_aux.getTipoEnVector();
                                 if (arg.getTipo()=='e') simbolo_aux.setTamano(1 * X);
                                 else if (arg.getTipo()=='b') simbolo_aux.setTamano(X);
                             }
                             simbolo_aux.setId(arg.getNombre());
                             Simbolo simb2 = new Simbolo();
                             simb2 = Copia(simbolo_aux);
                             
                             int existe = tablasimbolos.InsertarSimbolo(simb2, simb2.getId());
                             if (existe != 0) SemErr("Existen dos parámetros con el mismo identificador");
                             args = args - 1;
                         }
                         
                         Esperado(25);
                         Esperado(17);
                         Miembros();
                         Esperado(23);
                         tablasimbolos.t_actual = tablasimbolos.t_actual.getSuperior();
                         es_constructor = false;
                     }
                     
                 }else if (la.kind == 25) {
                 
                     Get();
                     if (StartOf(4)) {
                         if (StartOf(5)) {
                             ArgumentosDeclaracion();
                         }else {
                             Argumentos();
                         }
                     }
                     Esperado(25);
                     if (la.kind == 17 || la.kind == 31) {
                         if (la.kind == 17) {
                             Get();
                             Miembros();
                             Esperado(23);
                         }else {
                             Get();
                         }
                     }
                 }
             }
             tup = ALLMetodo();
             
             if (!es_objeto && !es_metodo){
                 terceto_actual = tercetos.asignacion(identificador_asigna, tup.getId());
                 tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual); 
                 if (!problema_del_for){
                     if (metodo_actual.equals("main"))
                         colaMain.add(tup_terc);
                     else
                         colaTercetos.add(tup_terc);
                 }
                 else
                     terceto_for = tup_terc;
             }
             es_objeto = false;
             es_metodo = false;
    }
    
    public void Argumentos() {
        tupla_id tupla;
        tupla_Tercetos tup_terc;
             
        if (colaArgumento == null) {
            if (numargmetodo != 0)
                SemError("cola de argumentos vacía");
            return ;
            
        }else{
             argumento = colaArgumento.poll();
             numargmetodo = numargmetodo - 1;

             if (aux>numargmetodo_aux) SemError("Número de argumentos incorrecto");
             aux = aux + 1;
             if (StartOf(6)) {
                 //expresion debe comprobar que si argumento no es vacio
        //su tipo de retorno debe coincidir con el tipo del argumento.
                 tupla = Expresion();
                 //realizamos el param
                 terceto_actual = tercetos.parametro(tupla.getId());
                 tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                     colaMain.add(tup_terc);
                 else
                     colaTercetos.add(tup_terc);
                 if (tupla.getTipo() != argumento.getTipo()) SemError("Tipo de argumento incorrecto");
                 while (la.kind == 15) {
                     Get();
                     Argumentos();
                 }
                 if ((numargmetodo >0)&&(la.kind != 15)) SemError("Número de argumentos incorrecto");
             }else if (numargmetodo > 0) SemError("Número de argumentos incorrecto");

        }

    }
    
    tupla_id ALLMetodo() {
        String arg1;
        tupla_id tup = null;
            
         if (la.kind == 30) {
             tupla_Tercetos tupla;
             Get();
             tup = Expresion();
             arg1 = tup.getId();

             if ((tup.getTipo() == 'e') && (tiene_valor_asignacion == true)&& (tablasimbolos.existeId(identificador_asigna))) {
                 
                 //no entra nunca porque tiene_valor_asignacion siempre esta a false
                 terceto_actual = tercetos.asignacion(arg1, identificador_asigna);
                 tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                     colaMain.add(tupla);
                 else
                     colaTercetos.add(tupla);
             }
             if (tipo_asig != tup.getTipo()) SemError("Tipos incompatibles en la asignacion");

             if (estoy_for != true)
                 Esperado(31);
         }
        return tup;
    }
    
    void ES() {
        if (la.kind == 37) {
                Escribir();
        } else if (la.kind == 38) {
                Leer();
        } else SynErr(44);
    }
    
    void Escribir() {
         tupla_id tupla;
         tupla_Tercetos tup_terc;
         
         Esperado(37);
         Esperado(19);
         
         if (la.kind == 25){
                 terceto_actual = tercetos.putSaltoLinea();
                 tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                    colaMain.add(tup_terc);
                 else
                    colaTercetos.add(tup_terc);
         }
         
         else if (StartOf(6)) {
             if (la.kind == 3) {  //caso de Print ("hola")
                 terceto_actual = tercetos.putCadena(la.val);
                 tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                     colaMain.add(tup_terc);
                 else
                     colaTercetos.add(tup_terc);
                 Get();
                         
             }else { //caso de Print(expresion)
                 tupla = Expresion();
                 if (tupla.getTipo() != 'e') SemError("Error en print, el tipo de la expresión es incorrecto");
                 else{
                    terceto_actual = tercetos.putExpresion(tupla.getId());
                    tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    if (metodo_actual.equals("main"))
                        colaMain.add(tup_terc);
                    else
                        colaTercetos.add(tup_terc); 
                 } 
             }
                 
                 
         }
         
         Esperado(25); //hemos leido el parentesis de cierre
         Esperado(31);
    }
    
    void Leer() {
         tupla_id tupla;
         tupla_Tercetos tup_terc;
         Esperado(38);
         Esperado(19);
         tupla = Expresion();
         if (tupla.getTipo() != 'e') SemError("Error en read, tipo de expresión incorrecto");
         else{
             terceto_actual = tercetos.readExpresion(tupla.getId());
             tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
             if (metodo_actual.equals("main"))
                colaMain.add(tup_terc);
             else
                colaTercetos.add(tup_terc);
         }
         Esperado(25);
         Esperado(31);
    }
    
    void Retorno() {
        boolean vengo_exp = false;
        String ident;
        tupla_id tupla;
        tupla_Tercetos tuplaTer = new tupla_Tercetos ("","","");
        String retorno;
        boolean return_con_op = false;
        
        Esperado(39);
        hay_return = true;
        if (StartOf(6)) {
            vengo_exp = true;
            tupla = Expresion();
                 
            /* Una vez que lee la Expresion puede realizar el código intermedio
            *  E -> return id;
            */
            return_con_op = true;  
            
            ident = tupla.getId();
            if (tablasimbolos.existeId(ident)== true) {
                //lugar_expresion = tercetos.darTemporal();
                terceto_actual = tercetos.retorno(ident);
                retorno = terceto_actual;
                if (!metodo_actual.equals("main")){
                    tuplaTer = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                    colaTercetos.add(tuplaTer);
                }
//                terceto_actual = tercetos.asignacion(lugar_expresion,retorno);
//                if (!metodo_actual.equals("main")){
//                    tuplaTer = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
//                    colaTercetos.add(tuplaTer);
//                }     
            }
        }
        if (!return_con_op){
            terceto_actual = tercetos.retornoSinOp();
            retorno = terceto_actual;
            if (!metodo_actual.equals("main")){
                tuplaTer = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                colaTercetos.add(tuplaTer);
            }
        }
        return_con_op = false;
        Esperado(31);
    }
    
    void For() {
        String etiqueta_inicio, etiqueta_sigue_for, etiqueta_fin;
        tupla_id tupla;
        tupla_Tercetos tup_terc;
        Esperado(40);
        Esperado(19);
        Asignaciones();

        etiqueta_inicio = tercetos.darEtiqueta();
        etiqueta_sigue_for = tercetos.darEtiqueta();
        etiqueta_fin = tercetos.darEtiqueta();
        
        // creamos el terceto para insertar la etiqueta de inicio del for
        terceto_actual = tercetos.InsertarEtiqueta(etiqueta_inicio);
        tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
        if (metodo_actual.equals("main"))
            colaMain.add(tup_terc);
        else
            colaTercetos.add(tup_terc);
        
        tupla = Expresion(); //leemos la condicion
        if (tupla.getTipo() != 'b') SemError("Se esperaba expresion booleana en el for");
        Esperado(31);
//      asignaciones parece haber avanzado mas de lo debido

        //ahora creamos el terceto para ver si la condicion se cumple
        terceto_actual = tercetos.saltoCondicional(tupla.getId(), etiqueta_sigue_for);
        tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
        if (metodo_actual.equals("main"))
            colaMain.add(tup_terc);
        else
            colaTercetos.add(tup_terc);
        
        //salto incondicional a fin si no se cumple
        terceto_actual = tercetos.saltoIncondicional(etiqueta_fin);
        tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
        if (metodo_actual.equals("main"))
            colaMain.add(tup_terc);
        else
            colaTercetos.add(tup_terc);
        
        estoy_for = true;
        
        //terceto para la etiqueta_sigue_for
        terceto_actual = tercetos.InsertarEtiqueta(etiqueta_sigue_for);
        tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
        if (metodo_actual.equals("main"))
            colaMain.add(tup_terc);
        else
            colaTercetos.add(tup_terc);
        
        problema_del_for = true;
        Asignaciones();
        problema_del_for = false;
        
        estoy_for = false;
        Esperado(25);
        if (la.kind == 17) {
                Get();
                while (StartOf(7)) {
                    Sentencias();
                }
                //añadimos el terceto de la asignacion
                if (metodo_actual.equals("main"))
                    colaMain.add(terceto_for);
                else
                    colaTercetos.add(terceto_for);
                
                //etiqueta para volver a la etiqueta_sigue_for
                terceto_actual = tercetos.saltoIncondicional(etiqueta_inicio);
                tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                if (metodo_actual.equals("main"))
                    colaMain.add(tup_terc);
                else
                    colaTercetos.add(tup_terc);
                
                Esperado(23);
                
        } else if (StartOf(7)) {
                Sentencias();
                
                //añadimos el terceto de la asignacion
                if (metodo_actual.equals("main"))
                    colaMain.add(terceto_for);
                else
                    colaTercetos.add(terceto_for);
                
                //etiqueta para volver a la etiqueta_sigue_for
                terceto_actual = tercetos.saltoIncondicional(etiqueta_inicio);
                tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                if (metodo_actual.equals("main"))
                    colaMain.add(tup_terc);
                else
                    colaTercetos.add(tup_terc);
                
                
                
        } else SynErr(45);
        
        //ponemos la etiqueta del fin del for
        terceto_actual = tercetos.InsertarEtiqueta(etiqueta_fin);
        tup_terc = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
        if (metodo_actual.equals("main"))
            colaMain.add(tup_terc);
        else
            colaTercetos.add(tup_terc);
    }
    
    void Sentencias() {
        if (la.kind == 1) {
                Asignaciones();
        } else if (la.kind == 39) {
                Retorno();
        } else if (la.kind == 37 || la.kind == 38) {
                ES();
        } else if (la.kind == 40) {
                For();
        } else SynErr(46);
    }

     //  char Expresion() {
//        char c1,c2;
//              c2 = Expresion2();
//              c1 = Expresion1();
//        if (c1=='-') {
//            return c2;
//        }
//        if (c1!= c2) SemErr("Tipos incompatibles en expresion");
//        return c1;
//      }


     tupla_id Expresion() {
         String lugar_expresion;
         String etiqueta_verdad, etiqueta_falso, sigue;          
         tupla_Tercetos tupla = new tupla_Tercetos ("","","");
         Simbolo simba;

         if (la.line == 142){
             int x;
             x = 233;
         }
         tupla_id c = new tupla_id("",'-');
         if ((!condicional)&&(((la.next.kind == 28)&&(la.next.next.next.kind == 33))
            ||(la.next.kind == 33))){
             
             tupla_id c1;
             condicional = true;
             c1 = Expresion();
             if (c1.getTipo() != 'b') SemError("Se esperaba una expresión booleana");
             
             // Se ha leido ? Expresion, por lo tanto ha de emitir el salto condicional
             
             lugar_expresion = tercetos.darTemporal();
             etiqueta_verdad = tercetos.darEtiqueta();
             sigue = tercetos.darEtiqueta();
             
             
                          
             terceto_actual = tercetos.saltoCondicional(c1.getId(), etiqueta_verdad);  //hacemos el if
             tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
             if (metodo_actual.equals("main"))
                 colaMain.add(tupla);
             else
                 colaTercetos.add(tupla);
             
             Esperado(33);
             tupla_id c2;
             c2 = Expresion();
             
             // Se ha leido la segunda expresion
             
             Esperado(14);
             tupla_id c3;
             c3 = Expresion();
             
             // Se ha leido la última expresion

             if (c3.getTipo() == '-') {
                    SemError("Expresion incorrecta");   
                    condicional = false;
             }
             else {
                 terceto_actual = tercetos.asignacion(lugar_expresion, c3.getId());
                 tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                    colaMain.add(tupla);
                 else
                    colaTercetos.add(tupla);
                 // saltamos a la siguiente sentencia
                 terceto_actual = tercetos.saltoIncondicional(sigue);
                 tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                 if (metodo_actual.equals("main"))
                    colaMain.add(tupla);
                 else
                    colaTercetos.add(tupla);
                 //creamos la etiqueta
                 terceto_actual = tercetos.InsertarEtiqueta(etiqueta_verdad);
                 tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                 // metemos el terceto en la cola correspondiente
                 if (metodo_actual.equals("main"))
                    colaMain.add(tupla);
                 else
                    colaTercetos.add(tupla);
                 terceto_actual = tercetos.asignacion(lugar_expresion, c2.getId());
                 tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                 // metemos el terceto en la cola correspondiente
                 if (metodo_actual.equals("main"))
                    colaMain.add(tupla);
                 else
                    colaTercetos.add(tupla);
                 // creamos la etiqueta "sigue"
                 terceto_actual = tercetos.InsertarEtiqueta(sigue);
                 tupla = new tupla_Tercetos (clase_actual, metodo_actual, terceto_actual);
                 // metemos el terceto en la cola correspondiente
                 if (metodo_actual.equals("main"))
                    colaMain.add(tupla);
                 else
                    colaTercetos.add(tupla);
                 
                 c.setId(lugar_expresion);
                 c.setTipo(c3.getTipo());
                 
                 //guardamos el temporal creado en la tabla de simbolos
                 simba = new Simbolo (lugar_expresion,c3.getTipo());
                 simba.setTamano(1);
                 tablasimbolos.InsertarSimbolo(simba, lugar_expresion);

             }
             return c;
         }else if (StartOf(2)) {
             next = la.next;
             c = Expresion1();
             if (c.getTipo() == '-') SemError("Expresion incorrecta");
             return c;
         }
         else if (la.kind == 19){
             Get();
             c = Expresion();
             Esperado(25);
            //Esperado(31);
            return c;
        }
//        else SynErr(44);
        else{
             next = la.next;
             c = Expresion1();
             return c;
        }
//         return new tupla_id("",'-');
        }


   tupla_id Expresion1() {

       if (la.line==72){
           int x;
           x = 123456;
       }
       tupla_id c1 = new tupla_id("",'-');
       tupla_Tercetos tupla = new tupla_Tercetos ("","","");
       Simbolo simba;
       String arg1, arg2, temporal;
       if ((next !=null)&&(next.kind==28)){
           next = null;
           c1 = Expresion1();
           if (c1.getTipo() != 'e') SemError("Se esperaba una expresión entera");
           Esperado(28);
           tupla_id c2;
           next2 = next;
           c2 = Expresion2();
           if (c2.getTipo() != 'e') SemError("Se esperaba una expresión entera");
           temporal = tercetos.darTemporal();
           terceto_actual = tercetos.operacionBinaria(c1.getId(), c2.getId(), "<", temporal);
           tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
           if (metodo_actual.equals("main"))
                colaMain.add(tupla);
           else
                colaTercetos.add(tupla);
            
            // Se mete el temporal en la Tabla de Símbolos

            simba = new Simbolo (temporal, 'b');
            simba.setTamano(1);
            tablasimbolos.InsertarSimbolo(simba, temporal);

            return new tupla_id(temporal,'b');
       }
       if (StartOf(2)) {
            tupla_id c2;
            next2 = la.next;
            c2 = Expresion2();
            return c2;
       } else if (StartOf(2)) {
            c1 = Expresion1();
            arg1 = c1.getId();
            if (c1.getTipo() != 'e') SemError("Se esperaba una expresión entera");
                Esperado(28);
            tupla_id c2;
            c2 = Expresion2();
            arg2 = c2.getId();
            if (c2.getTipo() != 'e') SemError("Se esperaba una expresión entera");
            return new tupla_id("",'b');
       } else {
//           SynErr(48);
           next2 = next;
           c1 = Expresion2();
           return c1;
        }
   }
   
   tupla_id Expresion2() {
        // c2 = arg1
        // c3 = arg2

        tupla_id c2, c3;
        String arg1, arg2, temporal;
        Simbolo simba;
        tupla_Tercetos tupla = new tupla_Tercetos ("","","");
        temporal = tercetos.darTemporal();

        if ((next2!=null)&&(next2.kind==22)){
            next2 = null;
            c2 = Expresion2();
            if (c2.getTipo() != 'e') SemError("Se esperaba una expresión entera");
            Esperado(22);
            c3 = Expresion3();
            if (c3.getTipo() != 'e') SemError("Se esperaba una expresión entera");
            terceto_actual = tercetos.operacionBinaria(c2.getId(), c3.getId(), "+", temporal);
            tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
            if (metodo_actual.equals("main"))
                colaMain.add(tupla);
            else
                colaTercetos.add(tupla);
             // Se mete el temporal en la Tabla de Símbolos
            simba = new Simbolo (temporal, 'e');
            simba.setTamano(1);
            tablasimbolos.InsertarSimbolo(simba, temporal);
            return new tupla_id(temporal,'e');
        }
        if (StartOf(2)) {
            c3 = Expresion3();
            return c3;
        } else  if (StartOf(2)) {
            c2 = Expresion2();
            arg1 = c2.getId();
            if (c2.getTipo() != 'e') SemError("Se esperaba una expresión entera");
            Esperado(22);
            c3 = Expresion3();
            arg2 = c3.getId();
            if (c3.getTipo() != 'e') SemError("Se esperaba una expresión entera");
            temporal = tercetos.darTemporal();
            terceto_actual = tercetos.operacionBinaria(arg1, arg2, "+", temporal);
            tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
            if (metodo_actual.equals("main"))
                colaMain.add(tupla);
            else
                colaTercetos.add(tupla);
             // Se mete el temporal en la Tabla de Símbolos
            simba = new Simbolo (temporal, 'e');
            simba.setTamano(1);
            tablasimbolos.InsertarSimbolo(simba, temporal);
            return new tupla_id("temporal",'e');
        } else {
//            SynErr(49);
           c3 = Expresion3();
           return c3;
        }
   }
   
   tupla_id Expresion3() {
        tupla_id c3, c4;
        String arg3;
        String temporal;
        tupla_Tercetos tupla;
        Simbolo simba;
        
        if (la.kind == 21) {
            Get();
            c3 = Expresion3();
            arg3 = c3.getId();
            temporal = tercetos.darTemporal();
            if (c3.getTipo() != 'b') SemError("Se esperaba expresión booleana");
            terceto_actual = tercetos.not(arg3, temporal);
            tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
            if (metodo_actual.equals("main"))
                colaMain.add(tupla);
            else
                colaTercetos.add(tupla);
            //guardamos el temporal en la tabla de simbolos
            simba = new Simbolo (temporal,c3.getTipo());
            simba.setTamano(1);
            tablasimbolos.InsertarSimbolo(simba, temporal);
            return new tupla_id(temporal,'b');
        } else if (la.kind != 21) {
            c4 = Expresion4();
            return c4;
        } else SynErr(50);
        return new tupla_id("",'-');
   }
   
   tupla_id Expresion4() {
       tupla_id c4 = new tupla_id("",'-');
        tupla_Tercetos tupla = new tupla_Tercetos ("","","");
        String terceto_argumentos [];
        Simbolo simba;
        LinkedList<Argumento> cola_arg_aux = new LinkedList<Argumento> ();
        String temporal, etiqueta1, etiqueta2;
        int fi, num_arg_fi;
        String argumentos [];
            
            switch (la.kind) {
            //Parentesis izquierda.
                case 19: {
                    Get();
                    c4 = Expresion();
                    Esperado(25);
                    break;
                }
        //Constructor
        // Bh = new Bicicleta();

                case 9: {
                    c4 = new tupla_id("",'o');
                    Get();
                    
                    terceto_actual = tercetos.constructor(simbolo.getId());
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    if (metodo_actual.equals("main"))
                        colaMain.add(tupla);
                    else
                        colaTercetos.add(tupla);
                    
                    Esperado(1);
                    c4.setId(simbolo.getId());
                    Simbolo expr_simbol = new Simbolo();
                    expr_simbol = tablasimbolos.Buscar(simbolo.getId());
                    if (expr_simbol != null) SemError("Mal uso del constructor");
                    if (expr_simbol == null){
                        TablaSimb tio;
                        tio = tablasimbolos.BuscarTio(simbolo.getId());
                        if (tio == null) SemError("No existe la clase "+simbolo.getId());
                        expr_simbol = tablasimbolos.BuscarEn(tio, simbolo.getId());//Busco el constructor en la clase.
                        if (expr_simbol==null) SemError("El constructor no esta declarado");
                        if (expr_simbol.getTipo()!='m') SemError("El constructor debe ser un método");
                    }
                    Esperado(19);
                    if (expr_simbol.getColaArgumentos() != null)
                    colaArgumento = expr_simbol.getColaArgumentos();
                    numargmetodo = expr_simbol.getNumArgMetodo();
                    numargmetodo_aux = numargmetodo;
                    aux = 0;
                    if (numargmetodo != 0)
                                Argumentos();
                                Esperado(25);
                                break;
                }
        //Identificador
        // num; || metodo(x,y) || vect[5] || bh.freno(0) || bh.num_ruedas
        // ident; || ident() || ident[] || ident.ident() || ident.ident ||
                case 1: {
                    String ident = la.val;
                    simbolo.setId(la.val);
                    Simbolo expr_simbol = new Simbolo();
                    expr_simbol = tablasimbolos.Buscar(simbolo.getId());
                    if (expr_simbol==null) SemError("Variable no declarada");
                    Get();
                    if (expr_simbol!=null){
                        c4.setTipo(expr_simbol.getTipo());
                        c4.setId(ident);
                    }
                    if (la.kind == 16 || la.kind == 18 || la.kind == 19) {
                        // Caso: ident.ident [(Argumentos)]
                        if (la.kind == 16) {
                            Get();
                            Esperado(1);
                            ident = ident + "." + la.val;
                            TablaSimb tio;
                            tio = tablasimbolos.BuscarTio(expr_simbol.getClaseRelacionada());
                            if (tio == null) SemError("No se encontró la clase relacionada");
                            expr_simbol = tablasimbolos.BuscarEn(tio, simbolo.getId());
                            if (expr_simbol == null) SemError("No se encontró el atributo o método en la clase relacionada");
                            //dentro de un objeto -> Parentesis izquierda.
                            // caso ident.indent (Argumentos)
                            /* Creo una cola auxiliar para copiar todos los argumentos
                             * Se recorre con un for la cola auxiliar
                             * Los argumentos se pasan a una lista
                             * */
                            if (la.kind == 19) {
                                if (expr_simbol.getTipo()!= 'm') SemError("Se esperaba un método");
                                c4.setTipo(expr_simbol.getTipoRetorno()) ;
                                Get();
                                if (expr_simbol.getColaArgumentos() != null)
                                    colaArgumento = expr_simbol.getColaArgumentos();
                                cola_arg_aux = expr_simbol.getColaArgumentos();
                                numargmetodo = expr_simbol.getNumArgMetodo();
                                numargmetodo_aux = numargmetodo;
                                num_arg_fi = numargmetodo;
                                aux = 0;
                                Argumentos();
                                Esperado(25);

                                /* Convierte los elementos de la cola en elementos de un String
                                 * para poder pasarlos por el terceto */

                                argumentos = new String [num_arg_fi];
                                Argumento arg;
                                for (fi = 0; fi > num_arg_fi; fi++){
                                    arg = cola_arg_aux.removeFirst();
                                    argumentos [fi] = arg.getNombre().toString();
                                }

                                etiqueta1 = tercetos.darEtiqueta();
                                terceto_argumentos = tercetos.call(ident, num_arg_fi, argumentos, etiqueta1);

// ******************** NOTA ******************************* //

// No he metido el terceto con los argumentos en la cola de tercetos porque lo que quiero almacenar es el terceto_actual

                                etiqueta2 = tercetos.darEtiqueta();
//                                terceto_actual = tercetos.funcion_en_terceto(ident, num_arg_fi, terceto_argumentos, etiqueta2);
                                terceto_actual = tercetos.funcion_en_terceto(ident, num_arg_fi);
                                tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                                if (metodo_actual.equals("main"))
                                    colaMain.add(tupla);
                                else
                                    colaTercetos.add(tupla);
                            }
                            else
                                c4.setTipo( expr_simbol.getTipo());
                            c4.setId(ident);
                            
                    //parentesis izquierda
                        // caso:  metodo(x,y)
                        } else if (la.kind == 19) {
                            Get();
                            if (expr_simbol.getTipo()!='m') SemError("Se esperaba un método");
                            if (expr_simbol.getColaArgumentos() != null)
                            colaArgumento = expr_simbol.getColaArgumentos();
                            cola_arg_aux = expr_simbol.getColaArgumentos();
                            numargmetodo = expr_simbol.getNumArgMetodo();
                            numargmetodo_aux = numargmetodo;
                            num_arg_fi = numargmetodo;
                            aux = 0;
                            argumentos = new String [num_arg_fi];
                            Argumentos();
                            c4.setTipo(expr_simbol.getTipoRetorno());
                            Esperado(25);

                    /* Convierte los elementos de la cola en elementos de un String
                         * para poder pasarlos por el terceto */
                            
                            argumentos = new String [num_arg_fi];
                            Argumento arg;
                            for (fi = 0; fi < num_arg_fi; fi++){
                                arg = cola_arg_aux.removeFirst();
                                argumentos [fi] = arg.getNombre().toString();
                                
                            }
                            etiqueta1 = tercetos.darEtiqueta();
//                            terceto_argumentos = tercetos.call(ident, num_arg_fi, argumentos, etiqueta1);
                            etiqueta2 = tercetos.darEtiqueta();
//                            terceto_actual = tercetos.funcion_en_terceto(ident, num_arg_fi, terceto_argumentos, etiqueta2);
                            terceto_actual = tercetos.funcion_en_terceto(ident, num_arg_fi);
                            tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                            if (metodo_actual.equals("main"))
                                colaMain.add(tupla);
                            else
                                colaTercetos.add(tupla);
                            //creamos un terceto para meter el res de la funcion en el temporal
                            temporal = tercetos.darTemporal();  //generamos el temporal
                            //guardamos el temporal
                            simba = new Simbolo (temporal,expr_simbol.getTipoRetorno());
                            simba.setTamano(1);
                            tablasimbolos.InsertarSimbolo(simba, temporal);
                            //generamos el terceto
                            terceto_actual = tercetos.guardar_res(temporal);
                            tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                            if (metodo_actual.equals("main"))
                                colaMain.add(tupla);
                            else
                                colaTercetos.add(tupla);
                            c4.setId(temporal);

                        }
                        else {
                            //estamos en el caso del vector
                            if (expr_simbol.getTipo() != 'v') SemError("Se esperaba un vector");
                            Get();
                            tupla_id c_aux;
                            c_aux = Expresion();
                            if (c_aux.getTipo() != 'e') SemError("Se esperaba un entero");
                            c4.setTipo(expr_simbol.getTipoEnVector());
                            c4.setId(ident + "[" + c_aux.getId() + "]");
                            Esperado(24);
                        }
                    }
                    else {

                /* Ha leido solamente ident, se trata de una Acción del tipo
                    E -> id
                 *          id.lugar := busca_id (id.entrada);
                 *          E.lugar := id.lugar;
                 * */
                        
                        if (tablasimbolos.existeId(ident) == true) {
                            lugar_expresion = "Lugar en el que se encuentra el identificador";
                        }
                        else {
                            SemErr ("El identificador no existe, no puede ser asignado");
                        }
                    }
                    break;
                }
        

                case 2: {
                    //Aqui se generan temporales para enteros
                    temporal = tercetos.darTemporal();
                    //creamos un terceto que meta el valor del entero en el temporal
                    terceto_actual = tercetos.da_valor_temp(temporal, la.val);
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    if (metodo_actual.equals("main"))
                        colaMain.add(tupla);
                    else
                        colaTercetos.add(tupla);
                    c4.setId(temporal);
                    c4.setTipo('e');
                    Get();
                    // Se mete el temporal en la Tabla de Símbolos

                    simba = new Simbolo (c4.getId(),c4.getTipo());
                    simba.setTamano(1);
                    tablasimbolos.InsertarSimbolo(simba, temporal);
                    break;
                }
                case 3: {
                    temporal = tercetos.darTemporal();
                    c4.setId(temporal);
                    c4.setTipo('s');
                    Get();
                    // Se mete el temporal en la Tabla de Símbolos

                    simba = new Simbolo (c4.getId(),c4.getTipo());
                    simba.setTamano(1);
                    tablasimbolos.InsertarSimbolo(simba, temporal);
                    break;
                }
                // case "true"
                //hacemos representacion numerica asi q metemos un "1"
                case 12: {
                     //Aqui se genera el temporal
                    temporal = tercetos.darTemporal();
                    //creamos un terceto que meta el valor del booleanos en el temporal
                    terceto_actual = tercetos.da_valor_temp(temporal, String.valueOf(1));
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    if (metodo_actual.equals("main"))
                        colaMain.add(tupla);
                    else
                        colaTercetos.add(tupla);
                    c4.setId(temporal);
                    c4.setTipo('b');
                    Get();

           // Se mete el temporal en la Tabla de Símbolos
                    
                    simba = new Simbolo (c4.getId(),c4.getTipo());
                    simba.setTamano(1);
                    tablasimbolos.InsertarSimbolo(simba, temporal);
                    break;
                }
                // case "false"
                // metemos un "0"
                case 7: {
                    //Aqui se genera el temporal
                    temporal = tercetos.darTemporal();
                    //creamos un terceto que meta el valor del booleanos en el temporal
                    terceto_actual = tercetos.da_valor_temp(temporal, String.valueOf(0));
                    tupla = new tupla_Tercetos(clase_actual, metodo_actual, terceto_actual);
                    if (metodo_actual.equals("main"))
                        colaMain.add(tupla);
                    else
                        colaTercetos.add(tupla);
                    c4.setId(temporal);
                    c4.setTipo('b');
                    Get();

           // Se mete el temporal en la Tabla de Símbolos
                    
                    simba = new Simbolo (c4.getId(),c4.getTipo());
                    simba.setTamano(1);
                    tablasimbolos.InsertarSimbolo(simba, temporal);

                    break;
                }
                default: SynErr(51); break;
            }
        return c4;
   }
   
   
   public void Parse() {
        la = new Token();
        la.val = "";
        Get();
        compilationunit();
        ImprimeTablaSimbolos();
        ficherosalida = "/home/pirois/Escritorio/aki";
        codigoFinal = new GCF (colaTercetos, colaMain, tablasimbolos, ficherosalida);
        Esperado(0);

   }
   
   private  boolean[][] set = {
                {T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
                {x,x,x,x, T,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x},
                {x,T,x,x, T,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,T,T,T, T,x,x},
                {x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
                {x,T,T,T, T,x,x,T, T,T,x,x, T,T,x,x, x,x,x,T, x,T,x,x, x,T,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x},
                {x,x,x,x, T,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x},
                {x,T,T,T, x,x,x,T, x,T,x,x, T,x,x,x, x,x,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
                {x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x},
                {x,T,T,T, x,x,x,T, x,T,x,x, T,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x}

   };


   public void ImprimeTablaSimbolos(){
       try{
    impresion=new PrintStream( new java.io.FileOutputStream("C:\\Documents and Settings\\Esther SM\\Escritorio\\txts\\tablaimpresa.txt"));
    }catch(FileNotFoundException e){
          System.err.println("Nombre de archivo desconocido");
      }

    ImprimeAbuelo();
    //System.out.println("\n");
     //System.out.println("\n");
     impresion.println("");
     impresion.println("");
    ImprimePadres();
     //System.out.println("");
      //System.out.println("");
      impresion.println("");
      impresion.println("");
    ImprimeHijos();
     System.out.println("");
      System.out.println("");
      impresion.println("");
      impresion.println("");
    impresion.close();
   }
   
   public void ImprimeAbuelo(){
        //System.out.println("++++++++++++++TABLA ABUELO++++++++++++++");
        impresion.println("++++++++++++++TABLA ABUELO++++++++++++++");
        //System.out.println("- Desplazamiento total : " + tablasimbolos.abuelo.getDesplazamiento());
        impresion.println("- Desplazamiento total : " + tablasimbolos.abuelo.getDesplazamiento());

        int index = 0;
        //System.out.println("Contiene las siguientes entradas ");
        impresion.println("Contiene las siguientes entradas ");
        HashMap aux = (HashMap) tablasimbolos.abuelo.getTabla();
        ImprimeTabla(aux);

   }
   
   public void ImprimeTabla(HashMap tabla){
        //System.out.println("Simbolos.......");
        impresion.println("Simbolos.......");
        //System.out.println("-----------------------------------------");
        impresion.println("-----------------------------------------");


        Iterator it;
        it = tabla.values().iterator();

        while(it.hasNext()){
                Simbolo s = (Simbolo)it.next();
                ImprimeSimbolo(s);}
   }
   
   public void ImprimePadres(){
        //System.out.println("************************************************************ ");
        impresion.println("************************************************************ ");
        //System.out.println("***********Imprimiendo las tablas padre (clases)************ ");
        impresion.println("***********Imprimiendo las tablas padre (clases)************ ");
        //System.out.println("************************************************************ ");
        impresion.println("************************************************************ ");

        Iterator it_padres;
        it_padres = tablasimbolos.padres.iterator();
        while (it_padres.hasNext()){
            TablaSimb tabla = (TablaSimb) it_padres.next();
            //System.out.println("xxxxxxxxxxxxxxxxxxx  Imprimiendo la tabla [" +tabla.getNombre()+"]  xxxxxxxxxxxxxxxxxxx");
             impresion.println("xxxxxxxxxxxxxxxxxxx  Imprimiendo la tabla [" +tabla.getNombre()+"]  xxxxxxxxxxxxxxxxxxx");
            //System.out.println("Desplazamiento total: " + tabla.getDesplazamiento());
            impresion.println("Desplazamiento total: " + tabla.getDesplazamiento());
            //System.out.println("Tabla superior: " + tabla.getSuperior().getNombre());
            impresion.println("Tabla superior: " + tabla.getSuperior().getNombre());
            ImprimeTabla(tabla.getTabla());
             //System.out.println("");
             impresion.println("");
             //System.out.println("");
             impresion.println("");

        }
   }

public void ImprimeHijos(){
    //System.out.println("************************************************************ ");
    impresion.println("************************************************************ ");
    //System.out.println("***********Imprimiendo las tablas hijo (métodos)************ ");
    impresion.println("***********Imprimiendo las tablas hijo (métodos)************ ");
    //System.out.println("************************************************************ ");
    impresion.println("************************************************************ ");

    Iterator it_hijos;
    it_hijos = tablasimbolos.hijos.iterator();
    while (it_hijos.hasNext()){
        TablaSimb tabla = (TablaSimb) it_hijos.next();
        //System.out.println("xxxxxxxxxxxxxxxxxxx  Imprimiendo la tabla [" +tabla.getNombre()+"]  xxxxxxxxxxxxxxxxxxx");
         impresion.println("xxxxxxxxxxxxxxxxxxx  Imprimiendo la tabla [" +tabla.getNombre()+"]  xxxxxxxxxxxxxxxxxxx");
        //System.out.println("Desplazamiento total: " + tabla.getDesplazamiento());
        impresion.println("Desplazamiento total: " + tabla.getDesplazamiento());
        //System.out.println("Tabla superior: " + tabla.getSuperior().getNombre());
        impresion.println("Tabla superior: " + tabla.getSuperior().getNombre());
        ImprimeTabla(tabla.getTabla());
         //System.out.println("");
         impresion.println("");
         //System.out.println("");
         impresion.println("");

    }
}

/*'e' entero, 'c' constante entera, 's' constante cadena,
 *'v' vector,'m' metodo,'p' palabra reservada, 'n' void,
 * 'x' clase, 'b' boolean, 'a' operador aritmetico,
 * 'l' operador logico, 'j' operador booleano, 'o' tipo objeto.
 */
public String mostrarTipo(char tipo){
        switch(tipo){
            case 'b':return "boolean";
            case 'c':return "constante entera";
            case 'e':return "entero";
            case 's':return "cadena de caracteres";
            case 'v':return "vector";
            case 'm':return "método";
            case 'p':return "palabra reservada";
            case 'n':return "void";
            case 'x':return "clase";
            case 'o':return "objeto";

            default:return "ERROR : SIMBOLO CON TIPO NO CONTEMPLADO";
        }
    }


public void ImprimeSimbolo(Simbolo simbol){

    //System.out.println("Simbolo:  " + simbol.getId());
    impresion.println("Simbolo:  " + simbol.getId());
    //System.out.println("Tipo:  " + mostrarTipo(simbol.getTipo()));
    impresion.println("Tipo:  " + mostrarTipo(simbol.getTipo()));
    //System.out.println("Linea:  " + simbol.getLinea());
    impresion.println("Linea:  " + simbol.getLinea());
    //System.out.println("Desplazamiento:  " + simbol.getTamano());
    impresion.println("Desplazamiento:  " + simbol.getTamano());
    if (simbol.getTipo()=='m'){
    //System.out.println("Numero de argumentos:  " + simbol.getNumArgMetodo());
    impresion.println("Numero de argumentos:  " + simbol.getNumArgMetodo());
    if (simbol.getColaArgumentos() != null){
        Iterator it;
        it = simbol.getColaArgumentos().iterator();
        int i = 0;
        while (it.hasNext()){
            Argumento lista = (Argumento) it.next();
            i = i+1;
            //System.out.println("    Argumento " +i+":  ");
            //System.out.println("        nombre:  " + lista.getNombre());
            //System.out.println("        tipo:  " + mostrarTipo((lista.getTipo())));
            impresion.println("    Argumento " +i+":  ");
            impresion.println("        nombre:  " + lista.getNombre());
            impresion.println("        tipo:  " + mostrarTipo((lista.getTipo())));
        }
    }
    //System.out.println("Tipo de retorno:  " + mostrarTipo(simbol.getTipoRetorno()));
    impresion.println("Tipo de retorno:  " + mostrarTipo(simbol.getTipoRetorno()));
    }



    //System.out.println("-----------------------------------------");
    impresion.println("-----------------------------------------");

}

} // end Parser

/* pdt - considerable extension from here on */

class ErrorRec {
        public int line, col, num;
        public String str;
        public ErrorRec next;

        public ErrorRec(int l, int c, String s) {
                line = l; col = c; str = s; next = null;
        }

} // end ErrorRec
class tupla_id{
    String id;
    char tipo;

    public tupla_id(String ide, char type){
        id = ide;
        tipo = type;
    }

    public String getId(){
        return id;
    }

    public char getTipo(){
        return tipo;
    }

    public void setId(String ide){
        id = ide;
    }

    public void setTipo(char type){
        tipo = type;
    }

}
// Clase para la lista de tercetos

class tupla_Tercetos {
    String clase, metodo, terceto;

    public tupla_Tercetos(String clase, String metodo, String terceto){
        this.clase = clase;
        this.metodo = metodo;
        this.terceto = terceto;
    }

    public String getClase(){
        return clase;
    }

    public String getMetodo(){
        return metodo;
    }

    public String getTerceto (){
        return terceto;
        }

    public void setClase(String clase){
        this.clase = clase;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public void setTerceto (String terceto) {
        this.terceto = terceto;

    }

}

class Errors {

        public static int count = 0;                                     // number of errors detected
        public static String errMsgFormat = "file {0} : ({1}, {2}) {3}"; // 0=file 1=line, 2=column, 3=text
        static String fileName = "";
        static String listName = "";
        static boolean mergeErrors = false;
        static PrintWriter mergedList;

        static ErrorRec first = null, last;
        static boolean eof = false;

        static String getLine() {
                char ch, CR = '\r', LF = '\n';
                int l = 0;
                StringBuffer s = new StringBuffer();
                ch = (char) Buffer.Read();
                while (ch != Buffer.EOF && ch != CR && ch != LF) {
                        s.append(ch); l++; ch = (char) Buffer.Read();
                }
                eof = (l == 0 && ch == Buffer.EOF);
                if (ch == CR) {  // check for MS-DOS
                        ch = (char) Buffer.Read();
                        if (ch != LF && ch != Buffer.EOF) Buffer.pos--;
                }
                return s.toString();
        }

        static private String Int(int n, int len) {
                String s = String.valueOf(n);
                int i = s.length(); if (len < i) len = i;
                int j = 0, d = len - s.length();
                char[] a = new char[len];
                for (i = 0; i < d; i++) a[i] = ' ';
                for (j = 0; i < len; i++) {a[i] = s.charAt(j); j++;}
                return new String(a, 0, len);
        }

        static void display(String s, ErrorRec e) {
                mergedList.print("**** ");
                for (int c = 1; c < e.col; c++)
                        if (s.charAt(c-1) == '\t') mergedList.print("\t"); else mergedList.print(" ");
                mergedList.println("^ " + e.str);
        }

        public static void Init (String fn, String dir, boolean merge) {
                fileName = fn;
                listName = dir + "listing.txt";
                mergeErrors = merge;
                if (mergeErrors)
                        try {
                                mergedList = new PrintWriter(new BufferedWriter(new FileWriter(listName, false)));
                        } catch (IOException e) {
                                Errors.Exception("-- could not open " + listName);
                        }
        }

        public static void Summarize () {
                if (mergeErrors) {
                        ErrorRec cur = first;
                        Buffer.setPos(0);
                        int lnr = 1;
                        String s = getLine();
                        while (!eof) {
                                mergedList.println(Int(lnr, 4) + " " + s);
                                while (cur != null && cur.line == lnr) {
                                        display(s, cur); cur = cur.next;
                                }
                                lnr++; s = getLine();
                        }
                        if (cur != null) {
                                mergedList.println(Int(lnr, 4));
                                while (cur != null) {
                                        display(s, cur); cur = cur.next;
                                }
                        }
                        mergedList.println();
                        mergedList.println(count + " errors detected");
                        mergedList.close();
                }
                switch (count) {
                        case 0 : System.out.println("Parsed correctly"); break;
                        case 1 : System.out.println("1 error detected"); break;
                        default: System.out.println(count + " errors detected"); break;
                }
                if (count > 0 && mergeErrors) System.out.println("see " + listName);
        }

        public static void storeError (int line, int col, String s) {
                if (mergeErrors) {
                        ErrorRec latest = new ErrorRec(line, col, s);
                        if (first == null) first = latest; else last.next = latest;
                        last = latest;
                } else printMsg(fileName, line, col, s);
        }

        public static void SynErr (int line, int col, int n) {
                String s;
                switch (n) {
                        case 0: s = "EOF Esperadoed"; break;
                        case 1: s = "ident expected"; break;
                        case 2: s = "enteros expected"; break;
                        case 3: s = "cadenaCar expected"; break;
                        case 4: s = "boolean expected"; break;
                        case 5: s = "char expected"; break;
                        case 6: s = "class expected"; break;
                        case 7: s = "false expected"; break;
                        case 8: s = "int expected"; break;
                        case 9: s = "new expected"; break;
                        case 10: s = "short expected"; break;
                        case 11: s = "static expected"; break;
                        case 12: s = "true expected"; break;
                        case 13: s = "void expected"; break;
                        case 14: s = "dosPuntos expected"; break;
                        case 15: s = "comma expected"; break;
                        case 16: s = "punto expected"; break;
                        case 17: s = "llaveIzda expected"; break;
                        case 18: s = "corcheteIzda expected"; break;
                        case 19: s = "parentesisIzda expected"; break;
                        case 20: s = "menos expected"; break;
                        case 21: s = "not expected"; break;
                        case 22: s = "mas expected"; break;
                        case 23: s = "llaveDer expected"; break;
                        case 24: s = "corcheteDer expected"; break;
                        case 25: s = "parentesisDer expected"; break;
                        case 26: s = "multiplicacion expected"; break;
                        case 27: s = "div expected"; break;
                        case 28: s = "menor expected"; break;
                        case 29: s = "mayor expected"; break;
                        case 30: s = "igual expected"; break;
                        case 31: s = "puntoComa expected"; break;
                        case 32: s = "doblesComillas expected"; break;
                        case 33: s = "interrogacion expected"; break;
                        case 34: s = "\"public\" expected"; break;
                        case 35: s = "\"private\" expected"; break;
                        case 36: s = "\"main\" expected"; break;
                        case 37: s = "\"print\" expected"; break;
                        case 38: s = "\"read\" expected"; break;
                        case 39: s = "\"return\" expected"; break;
                        case 40: s = "\"for\" expected"; break;
                        case 41: s = "??? expected"; break;
                        case 42: s = "invalid TiposSimples"; break;
                        case 43: s = "invalid Declaraciones"; break;
                        case 44: s = "invalid ES"; break;
                        case 45: s = "invalid For"; break;
                        case 46: s = "invalid Sentencias"; break;
                        case 47: s = "invalid Expresion4"; break;
                        case 48: s = "invalid Expresion5"; break;
                        default: s = "error " + n; break;
                }
                storeError(line, col, s);
                count++;
        }

        public static void SemErr (int line, int col, int n) {
                storeError(line, col, ("error " + n));
                count++;
        }

        public static void Error (int line, int col, String s) {
                storeError(line, col, s);
                count++;
        }

        public static void Warn (int line, int col, String s) {
                storeError(line, col, s);
        }

        public static void Exception (String s) {
                System.out.println(s);
                System.exit(1);
        }

        private static void printMsg(String fileName, int line, int column, String msg) {
                StringBuffer b = new StringBuffer(errMsgFormat);
                int pos = b.indexOf("{0}");
                if (pos >= 0) { b.replace(pos, pos+3, fileName); }
                pos = b.indexOf("{1}");
                if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
                pos = b.indexOf("{2}");
                if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
                pos = b.indexOf("{3}");
                if (pos >= 0) b.replace(pos, pos+3, msg);
                System.out.println(b.toString());
        }

} // end Errors
