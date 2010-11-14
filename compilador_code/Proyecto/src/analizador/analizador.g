header {
	package analizador;
}

// Este será uno de los tres tipos de analizadores que tendremos
// que hacer, este caso es el Lexico.
class analizador extends Lexer ;


options{
	charVocabulary = '\3'..'\377';	// Caracteres que podemos leer
	exportVocab = CompLexerVocab;
	testLiterals=false;
	k=2;	 // Tamano del lexema
}

tokens {
	// Palabras Reservadas
	// TIPO_ALGO = "algo";
	
	CTE_LOGTRUE = "true";
	CTE_LOGFALSE = "false";
	CHAR = "char";
	INT = "int";
	BOOL = "bool";
	IF = "if";
	ELSE = "else";
	RETURN = "return";
	DO = "do";
	WHILE = "while";
	FOR = "for";
	VOID = "void";
	CLASS = "class";
	PUBLIC = "public";
	PRIVATE = "private";
	SWITCH = "switch";
	CASE = "case";
	BREAK = "break";
	DEFAULT = "default";
	CIN = "cin";
	COUT = "cout";
	// Literales cadena
	LIT_NL = "nl"; LIT_TAB = "tab" ; LIT_COM = "com";
	 
	
	LIT_ENTERO_OCTAL;
	LIT_ENTERO_DECIMAL;
}

{ // Comienza la zona de código
	protected Token makeToken(int type)	{
		// Usamos la implementación de la superclase...
		Token result = super.makeToken(type);
		// ...y añadimos información del nombre de fichero
		//result.setFilename(inputState.filename);
		// y devolvemos el token
		return result;
	}
}

/** Esta regla permite ignorar los blancos.*/
BLANCO : ( ' '
		| '\t'
		| NL
	) { $setType(Token.SKIP); }; // La accion del blanco: ignorar

/**Los tres tipos de retorno de carro.*/
protected NL : (("\r\n") => "\r\n" // MS-DOS
		| '\r'	// MACINTOSH
		| '\n'	// UNIX
	)	{ newline();};
				
			
/** Letras españolas. */
protected LETRA: 'a'..'z'
				| 'A'..'Z'
				| 'ñ' | 'Ñ'
				| 'á' | 'é' | 'í' | 'ó' | 'ú'
				| 'Á' | 'É' | 'Í' | 'Ó' | 'Ú'
				| 'ü' | 'Ü';
				
/** Dígitos usuales */
protected DIGITO : '0'..'9';
protected DIGIT_O : '1'..'9';

/** Regla que permite reconocer los literales (y palabras reservadas).*/
IDENT options {testLiterals=true;} // Comprobar palabras reservadas
			: (LETRA|'_') (LETRA|DIGITO|'_')*; 	// Empieza por Letra o _
												// _a3 a_3_4 qweWEQ...

// COMENTARIOS
COMENTARIO1 // Este tipo de commentario //
	: "//" (~('\n'|'\r'))*
		{ $setType(Token.SKIP); }
	;

COMENTARIO2 : "/*" ( ('*' NL) => '*' NL
		| ('*' ~('/'|'\n'|'\r')) => '*' ~('/'|'\n'|'\r') // Modificada
		| NL
		| ~( '\n' | '\r' | '*' )
		)* "*/"
	{ $setType(Token.SKIP); } ;


												
// CONSTANTES
LIT_NUMERO : ( '0' ( DIGIT_O )+ ( DIGITO )*) { $setType (LIT_ENTERO_OCTAL);}
			|	 ( DIGITO )+ { $setType (LIT_ENTERO_DECIMAL);}
;

// CADENA
LIT_CADENA :
'"' !
( ~('"'|'\n'|'\r') )*
'"' !
;

												
// OPERADORES
// OP_Artimeticos
OP_MAS : '+' ;
OP_MENOS : '-' ;
OP_PRODUCTO : '*' ;
OP_DIVISION : '/' ;
OP_MODULO : '%' ;
// OP_Relacion
OP_IGUAL : "==" ;
OP_DISTINTO : "!=" ;
OP_MENOR : '<' ;
OP_MAYOR : '>' ;
OP_MENOR_IGUAL : "<=" ;
OP_MAYOR_IGUAL : ">=" ;
// OP_LOGICOS
OP_AND : "&&" ;
OP_OR : "||" ;
OP_NOT : '!' ;
//REFERENCIA
REFERENCIA : '&' ;
// OP_AUTO_[INCR|DECR]emento
// tanto delanto como detrás
OP_MASMAS : "++" ;
OP_MENOSMENOS : "--" ;
// OP_condicional
DOSPUNTOS : ':' ;
INTER : '?' ;
// OP_asignacion
OP_ASIG : '=' ;
// OP_asig+op
OP_ASIG_MAS : "+=" ;
OP_ASIG_MENOS : "-=" ;
OP_ASIG_PRODUCTO : "*=" ;
OP_ASIG_DIVISION : "/=" ;
OP_ASIG_MODULO : "%=" ;
// Separadores
PUNTO_COMA : ';' ;
COMA : ',' ;
CORCHETE_AB : '[' ;
CORCHETE_CE : ']' ;
LLAVE_AB : '{' ;
LLAVE_CE : '}' ;
PUNTO : '.' ;//operador de acceso a clase
PARENT_AB : '(' ;
PARENT_CE : ')' ;
BARRA_VERT : '|';
MENOR_MENOR: "<<" ;
MAYOR_MAYOR: ">>" ; 
DOSPUNTOS_DOS : "::" ;
