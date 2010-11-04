			/* programa ass13.c */

			/* ensamblador-emulador "ass" versión 1.3 */

			/* Miguel A. Lerma (Febrero 1992) */

                        /* Miguel A. Vicente Puente (Mayo 1996)*/

                        /* David Marín Carreño (Mayo 2002) */


/****************************** cabeceras **********************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h> 
#include <ctype.h>

#if (defined(__TURBOC__) || defined(__BORLANDC__))
#include <conio.h>
#include <io.h>
#else
#include "vt100.h"
#endif

/******************************* macros ************************************/

#define NOMASS "ass"	/* nombre del ensamblador-emulador */
#define VER "1.3"		/* número de versión del ensamblador-emulador */
#define AUT "(Miguel A. Lerma, Enero 1992)"	/* autor */
#define ACTUAL1 "(Francisco Malpartida Candel, Mayo 1995)" 
#define ACTUAL2 "(Miguel A. Vicente Puente, Mayo 1996)"  /*actualizacion*/
#define ACTUAL3 "(David Marín Carreño, Mayo 2002)"  /*solución de error*/

#define NOMEXE "ass"	/* nombre para llamar el ejecut. de este programa */
#define NOMCOD "codmaq" /* nombre del archivo para el código má quina */
#define NOMTAB "tabetq"	/* nombre del archivo para la tabla de etiquetas */

#define PASADAS	256		/* máximo número de pasadas permitidas */

#define M 16384			/* cantidad de memoria de la máquina virtual */

#define LONG_ID 24		/* máxima longitud permitida para un identificador */
#define LONG_LIN 80		/* máxima longitud de una línea */
#define MAX_ERROR 30 	/* máximo número de errores permitidos */
#define N_ERROR_PAUSA 6	/* número de errores para hacer pausa */
#define MAX_ETIQ 256	/* máximo número de etiquetas */

#define COM (ca==';')		/* carácter de inicio de comentario */

#define EO_LIST -1			/* marca de fin de lista */

/* macros para asegurar compatibilidad de tipos con otros S.O. */
#define CH signed char				/* macro de 'caracter' */
#define IN signed short int			/* macro de 'entero corto' */
#define LI signed long int			/* macro de 'entero largo' */

#define MAX_ENT 32767l		/* rango de valores de entero corto */
#define MIN_ENT -32768l


/* comprueba si x cae en el rango de entero corto */
#define ENTERO_CORTO(x)		((x>=MIN_ENT)&&(x<=MAX_ENT))

#define SINSIGNO_CORTO(x)	((x>=0)&&(x<=65535l))

/* rango de carácter con signo */
#define CHAR(x)		((x>=-128)&&(x<=127))

/* salta espacios */
#define SALTABLANCOS		while (isspace(ca)) leecar()

/* emisión de código máquina */
#define EMITE(x)			( *(pmem(mem_top))=x , ++mem_top )

/* instrucción para apilar */
#define APILAR(x)			( *(pmem(sp-1))=(x) , --sp )

/* instrucción para desapilar */
#define DESAPILAR(x)  x = ( (sp>=M) ? error("Acceso a pila vacía") : m[sp++] )



/************************** tabla de mnemónicos ****************************/

#define NUM_MNE 41		/* número de mnemónicos */

/* tabla de mnemónicos */

CH tdm[NUM_MNE][6]=
{
/* NOP */					"NOP",
/* movimientos */			"LDA","STA","LDSP","STSP",
							"LDR","STR","LDIX","STIX",
/* operac. aritméticas */	"ADD","SUB","MUL","DIV","INC","DEC","NEG",
/* operaciones lógicas */	"AND","OR","NOT",
/* manejo de pila */		"PUSH","POP",
/* saltos */				"J","JZ","JNZ","JP","JNP","JM","JNM",
/* llamadas y retornos */	"CALL","RET","STOP",
/* entradas y salidas */	"INPUT","WRITE","ININT","WRINT","WRSTR",
/* pseudoinstrucciones */	"EQU","DC","DS","DFSTR","END"
};

/************************ repertorio de instrucciones **********************/

enum
{
/* NOP */					NOP,
/* movimientos */			LDA, STA, LDSP, STSP,
							LDR, STR, LDIX, STIX,
/* operac. aritméticas */	ADD, SUB, MUL, DIV, INC, DEC, NEG,
/* operaciones lógicas */	AND, OR, NOT,
/* manejo de pila */		PUSH, POP,
/* saltos */				J, JZ, JNZ, JP, JNP, JM, JNM,
/* llamadas y retornos */	CALL, RET, STOP,
/* entradas y salidas */	INPUT, WRITE, ININT, WRINT, WRSTR,
/* pseudoinstrucciones */	EQU, DC, DS, DFSTR, END
};

/*********************** modos de direccionamiento ***************************/

#define NUM_DIR 6	/* número de modos de direccionamiento */

enum
{
ACDIR, 		/* acumulador directo */
ACIND, 		/* acumulador indirecto */
INMED, 		/* inmediato */
DIREC, 		/* directo */
INDIR, 		/* indirecto */
RELAT		/* relativo */
};

/**************************** tipos de token *********************************/

enum
{
				/* tokens del lenguaje */
NULO,
NUMERO,
IDENTIFICADOR,
DOS_PUNTOS,
COMA_I,
ABRE_PARENTESIS,
CIERRA_PARENTESIS,
ABRE_CORCHETE,
CIERRA_CORCHETE,
FIN_DE_LINEA,

			/* tokens de expresiones aritméticas */

MAS,
MENOS,
POR,
ENTRE

};

/************************** variables globales *****************************/

CH nomb_fuente[80]="";	/* nombre del archivo fuente */

IN m[M];			/* memoria de la máquina virtual */

IN mem_top;		/* final de la memoria */
	/* (apunta a la primera posición libre durante la emisión de código) */

	/* indicador de pila inaccesible */
const IN pila_inaccesible=0;	/* 0: pila accesible */
								/* 1: pila inaccesible */

CH fca;			/* carácter leído del programa fuente */
CH ca;			/* carácter leído de la línea en curso */


struct					/* estructura de token */
{
	IN tipo;		/* tipo de token */
	union					/* parte semántica del token */
	{
		IN val;				/* valor de token numérico */
		CH str[LONG_ID+1];		/* token como cadena */
	} sem;
} token;


struct				/* tabla de etiquetas */
{
	CH nomb[LONG_ID+1];	/* nombre de la etiqueta */
	IN def;	/* indicador de etiqueta definida */
	IN asig;	/* indicador de etiqueta asignada */
	IN val;	/* valor de la etiqueta */
} tde[MAX_ETIQ];


typedef struct			/* tipo atributos */
{			/* (usado por el analizador de expresiones aritméticas) */
	IN def;	/* definición */
	IN val;	/* valor */
} atr;


IN etiq_top=-1;		/* final de la tabla de etiquetas */

IN num_linea;		/* número de línea */
IN num_car;			/* número de carácter de la línea en curso */
CH linea[LONG_LIN];	/* contenido de la línea en curso */
IN n_errores=0;	/* contador del numero de errores */
IN cont=0;		/* contador para control de pausas */
IN nei=0;		/* número de etiquetas indefinidas */
IN andei=1;		/* número de etiquetas indefinidas en la pasada anterior */
IN num_expr_indef;	/* número de expresiones indefinidas */

IN cm=0;			/* código de mnemónico */
IN cd=0;			/* direccionamiento */
IN cop=0;	  		/* operando */
IN codg=0;			/* código máquina a emitir */

/* control de opciones */

IN cod=0;		/* indicador de emisión de código máquina */
IN dep=0;		/* indicador de depuración */

/* control de tiempos */

IN num_pasada=0;
IN ensamblando=0;
IN ultima_pasada=0;
IN forz=0;	/* indicador para control de mensajes */
IN ejecutando=0;

/* punteros a archivos */

FILE *f_fuente;		/* programa fuente */
FILE *f_codigo;		/* archivo para código máquina */
FILE *f_tabla;		/* archivo para tabla de etiquetas */

/********************* registros de la máquina ****************************/

IN instr;		/* registro de instrucción */

IN acum=0;		/* acumulador */
IN r=0;			/* registro especial */
IN co=0;		/* contador de operación */
IN sp=M;		/* puntero de pila */
IN ix=0;			/* indice */

/********************** prototipos de funciones ***************************/

IN uso();

IN ensamblar();
IN ejecutar();

IN pasada();
IN get_linea();
IN ensamblar_linea();
IN no_inmed();
IN final_linea();
IN operando();
IN emite_cadena();
IN busca_etiq();
IN define_etiq();
IN codigo_mnemonico();
IN get_token();
IN get_numero();
IN get_caracter();
IN get_ident();
IN leecar();
IN error_ci();

atr expr();
atr expr1();
atr pterm();
atr term();
atr term1();
atr fact();
atr oper();

IN abrir_fuente();
IN abrir_codigo();
IN abrir_tabla();
IN actual_cod();
IN actual_tde();

IN * pmem();
IN error();
IN ens_error();
IN ejec_error();
IN error2();
CH pausa();
IN salida();
IN interrup();
IN f_error();
IN f_error2();

/***************************************************************************/
/***********************Portabilidad a linux********************************/
char vic_getch()
{
  char a;
  scanf("%c",&a);
  return a;
}

/***************************** principal ***********************************/

main(argc,argv)
IN argc;		/* número de argumentos */
CH *argv[];	/* lista de argumentos */
{
	IN k;
	IN fuente_leido=0;

	if (argc<2) uso();
	for (k=1; k<argc; ++k)	/* lectura de los argumentos */
	{
		if (strcmp("/c",argv[k])==0) cod=1;			/* opciones */
		else if (strcmp("/d",argv[k])==0) dep=1;
		else
		{
			if (fuente_leido) uso();
			else
			{					/* lectura del nombre del fuente */
				strcpy(nomb_fuente,argv[k]);
				fuente_leido=1;
			}
		}
	}

	clrscr();
	ensamblar();
	ensamblando=0;
	if (n_errores!=0)
	{
		printf("\nDetectad(os) %d error(es) en fase de ensamblado.\n",n_errores);
	}
	else
	{
		printf("Ensamblado finalizado sin errores.\n");
		printf("Pulse una tecla para ejecutar.\n");
		getch();
		ejecutar();
		ejecutando=0;
	}
}

IN uso()		/* muestra el uso de este programa */
{
	printf("\n");
	printf("Ensamblador-emulador %s \n",NOMASS);
	printf("%s\n\n",AUT);
        printf("Actualización a LINUX ");
	printf("(versión %s)\n",VER);
        printf("  %s\n  %s\n  %s\n\n",ACTUAL1,ACTUAL2,ACTUAL3);
	printf("Uso:\n\n");
	printf("%s [opciones] nombarch [opciones]\n\n",NOMEXE);
	printf("donde 'nombarch' es el nombre del archivo a ensamblar,\n");
	printf("y las posibles opciones son las siguientes:\n\n");
	printf("/c   emisión de código máquina al archivo '%s'\n\n",NOMCOD);
	printf("/d   modo de depuración, ejecución paso a paso\n\n");
	exit(0);
}



/***************************** ensamblar ***********************************/

IN ensamblar()		/* ensambla el programa fuente */
{
    ensamblando=1;
	printf("\nEnsamblando...\n\n");
	if (cod)
	{
		printf("Se envia código máquina al archivo -> %s.\n",NOMCOD);
		printf("Se envia tabla de etiquetas al archivo -> %s.\n",NOMTAB);
	};
	do								/* bucle de pasadas */
	{
		++num_pasada;
		printf("pasada número: %d...\n",num_pasada);
		pasada();
		if ((num_pasada==1)&&(n_errores>0)) interrup();
		if (nei==0) break;	/* fin si no hay etiquetas indefinidas */
	} while ((num_pasada<PASADAS)&&(nei!=andei));
	if ((nei>0)||(num_expr_indef>0))
	{		/* la última pasada sirve para mostrar */
			/* etiquetas y expresiones indefinidas */
		ultima_pasada=1;
		++num_pasada;
		printf("\nUltima pasada...\n\n",nei);
		pasada();
	};
}

IN pasada()		/* pasada de ensamblado */
{
	IN k, m;

	abrir_fuente();
	andei=nei;
	m=mem_top;
	mem_top=0;
	num_linea=0;								/* se desasignan etiquetas */
	for (k=0, nei=0; k<=etiq_top; ++k) tde[k].asig=0;
	num_expr_indef=0;
	while (1)
	{
		get_linea();
		if (ensamblar_linea()==END) break;
		if (fca==EOF)
		{
			error("END no encontrado");
			break;
		};
	};
	mem_top=max(m,mem_top);
	fclose(f_fuente);
    if (cod)
	{
		actual_cod();		/* actualiza el archivo de código */
		actual_tde();		/* actualiza archivo de etiquetas */
	};
	for (k=0, nei=0; k<=etiq_top; ++k)
	{					/* se recuenta el número de etiquetas indefinidas */
		if (!tde[k].def) ++nei;
	};
}

IN get_linea()				/* obtiene una línea del fuente */
{					/* y devuelve su número de caracteres */
	IN k=0;

	++num_linea;
	fca=getc(f_fuente);
	for (k=0; k<LONG_LIN; fca=getc(f_fuente), ++k)
	{
		if ((fca=='\n')||(fca==EOF))
		{
			linea[k]='\0';
			return k;	/* se retorna al llegar a fin de línea o archivo */
		};
		linea[k]=fca;		/* se lee el cáracter */
	};			/* si la línea es demasiado larga se va a final de línea */
	linea[LONG_LIN-1]='\0';
	while ((fca!='\n')&&(fca!=EOF)) fca=getc(f_fuente);
	error("línea demasiado larga");		/* y se da error */
}

							/* ensambla línea en curso y */
IN ensamblar_linea()		/* devuelve código del mnemónico */
{
	IN x, k=-1;	/* k=-1 significa 'sin etiqueta' */
	IN dir_oper;	/* dirección del operando */
	atr arg;		/* argumento de EQU */

	cm=NOP;
	num_car=-1;		/* principio de la línea */
	leecar();
	get_token();	/* se obtiene el primer token */
	if (token.tipo==FIN_DE_LINEA) return cm;	/* línea vacía */
	if (token.tipo!=IDENTIFICADOR)
	{
		error("una línea debe empezar por etiqueta o mnemónico");
		return -1;
	};
	SALTABLANCOS;
	if (ca==':')	/* se comprueba si el identificador es una etiqueta */
	{				/* si lo es, se busca en la tabla de etiquetas */
		k=busca_etiq(token.sem.str);	/* pero no se le asigna valor */
										/* salvo para retornar */
		if (tde[k].asig) error2("reasignación de etiqueta -> ",tde[k].nomb);
		else tde[k].asig=1;	/* si no estaba asignada, se marca como tal */
		leecar();
		get_token();	/* se toma otro token */
		if (token.tipo==FIN_DE_LINEA)
		{								/* línea con sólo etiqueta */
        	define_etiq(k,mem_top);
			return cm;
		};
		if (token.tipo!=IDENTIFICADOR)
		{
			error("falta mnemónico");
            define_etiq(k,mem_top);
			return -1;
		};
	};
							/* ahora se espera un mnemónico */
	cm=codigo_mnemonico(token.sem.str);

	if ((num_pasada>1)&&(!ultima_pasada))
	{			/* sólo se analiza en la primera y última pasadas */
		if (cm!=EQU) return cm;
	};

	if(k>=0)	/* asignación de etiqueta */
	{		/* si la instrucción no es EQU */
			/* se asigna mem_top (dirección actual) */
		if (cm!=EQU) define_etiq(k,mem_top);
	};

	if (cm<0)
	{
		error("identificador no reconocido como mnemónico");
		return -1;
	};

	dir_oper=mem_top+1;

	switch(cm)
	{						/*  pseudoinstrucciones */
		case END:	if (k>=0)
					{
						error ("pseudoinstrucción END con etiqueta");
					};
					get_token();
					no_inmed();
					final_linea();
					return END;

		case EQU:   if (k<0) if (!ultima_pasada) error("falta etiqueta");
					get_token();
					if (token.tipo==FIN_DE_LINEA)
					{
						error("falta argumento de EQU");
						final_linea();
						return -1;
					};
					no_inmed();
					arg=expr();
					if (!arg.def)
					{
						++num_expr_indef;
						final_linea();
						return -1;
					}
					else if (k>=0) define_etiq(k,arg.val);
					return final_linea();

		case DC:	get_token();
					no_inmed();
					if (token.tipo!=NUMERO)
					{
						error("DC requiere un argumento numérico");
					}
					else EMITE(token.sem.val);
					get_token();
					return final_linea();

		case DS:	get_token();
					no_inmed();
					if (token.tipo!=NUMERO)
                    {
						error("DS requiere un argumento numérico");
					}
					else if (token.sem.val<=0)
					{
						error("el argumento de DS debe ser positivo");
					}
					else
					{
						x=mem_top+token.sem.val;
						if (pmem(x)==NULL)
						{
							error("no hay suficiente espacio en memoria");
						}
						else
						{
							while (mem_top<x)
							{
								kbhit();
								EMITE(0);
							};
						};
					};
					get_token();
					return final_linea();

		case DFSTR:	if (!emite_cadena())
					{
						get_token();
						final_linea();
						return -1;
					};
					get_token();
					return final_linea();

		case RET:			/* instrucciones sin operando */
		case STOP:
		case NOP:
					codg=cm*NUM_DIR;
					EMITE(codg);
					get_token();
					no_inmed();
					return final_linea();

		default:			/* instrucciones con operando */
					get_token();
					if (token.tipo==COMA_I)
					{
						cd=INMED;
						if ((J<=cm)&&(cm<=CALL))
						{
							error("direccionamiento inmediato en salto o llamada");
							get_token();
						}
						else
						{
							switch(cm)		/* instrucciones sin */
							{				/* direccionamiento inmediato */
								case STA:
								case STSP:
								case STR:
								case STIX:
								case INC:
								case DEC:
								case NEG:
								case NOT:
								case POP:
								case INPUT:
								case ININT:
								case WRSTR:
											no_inmed();
											return -1;

								default:	get_token();
							};
						};
						if (operando(&dir_oper)) get_token();
						else error("falta operando");
					}
					else if (token.tipo==ABRE_PARENTESIS)
					{
						get_token();
						if (operando(&dir_oper))
						{
							cd=INDIR;
							get_token();
						}
						else cd=ACIND;
						if (token.tipo!=CIERRA_PARENTESIS)
						{
							error("falta paréntesis derecho");
						}
						else get_token();
					}
					else if (token.tipo==ABRE_CORCHETE)
					{
						get_token();
						if (operando(&dir_oper))
						{
							cd=RELAT;
							get_token();
						}
						else
						{
							error("falta operando");
						};
						if (token.tipo!=CIERRA_CORCHETE)
						{
							error("falta corchete derecho");
						}
						else get_token();
					}
					else
					{
						if (operando(&dir_oper))
						{
							cd=DIREC;
							get_token();
						}
						else
						{
							cd=ACDIR;
							if ((J<=cm)&&(cm<=CALL))
								error("falta dirección de destino en salto o llamada");
							if (cm==WRSTR)
								error("instrucción WRSTR sin dirección de cadena");
						};
					};
	};

	codg=cm*NUM_DIR+cd;
	EMITE(codg);			/* emisión del código de la instrucción */
	switch(cd)
	{						/* emisión del código del operando */
		case INMED:
        case DIREC:
		case INDIR:
		case RELAT:
					EMITE(cop);
	};
	return final_linea();
}

IN no_inmed()		/* comprobación para instruc. sin direcc. inmed. */
{
	if (token.tipo==COMA_I)
	{
		error("mal uso de direccionamiento inmediato");
		get_token();
	};
}

IN final_linea()		/* comprueba si se ha llegado a final de línea */
{					/* y dá mensaje de error en caso negativo */
	if (token.tipo!=FIN_DE_LINEA)
	{
		error("código extra¤o al final de la linea");
		return -1;
	}
	else return cm;
}

IN operando(pvalor)	/* gestiona la codificación de un operando */
IN *pvalor; /* valor a asignar si el operando es una etiqueta indefinida */
{				/* (pasado por referencia) */
	IN k;

	switch(token.tipo)
	{
    	case NUMERO:	cop=token.sem.val;
						return 1;

		case IDENTIFICADOR:

						k=busca_etiq(token.sem.str);
                        cop=tde[k].val;
						if (!tde[k].def)
						{	
							if (pvalor!=NULL) tde[k].val=*pvalor;
										/* si la etiqueta no está definida
										se mantiene lista de direcciones
										para rellenar más tarde */

							return -1;
								/* si la etiq. es indef. se devuelve -1 */
						}
						else return 1;
								/* si está definida, se devuelve +1 */
		default: return 0;
	};
}

IN emite_cadena()		/* emite la cadena definida por DFSTR */
{
	SALTABLANCOS;
	if (ca!='\"')
	{
		error("falta cadena");
		return 0;
	};
	ca=linea[++num_car];
	while (ca!='\0')
	{
		if (ca=='\"')
		{
        	ca=linea[++num_car];
			if (ca!='\"')
			{
				EMITE('\0');	/* fin de cadena = código nulo */
				return 1;
			};
		};
		EMITE(ca);
        ca=linea[++num_car];
	};
	error("faltan comillas de cierre");
	return 0;
}

IN busca_etiq(ident)	/* busca una etiqueta en la tde y devuelve su posición */
CH ident[LONG_ID];	/* (si no está, la a¤ade) */
{
	IN k;

	for (k=0; k<=etiq_top; ++k)	/* busca etiqueta */
	{
			/* si la encuentra, devuelve su posición en la tde */
		if (strcmp(ident,tde[k].nomb)==0)
		{
					/* en la últ. pas. se da error de et. indef. */
			if (ultima_pasada)
				if (!tde[k].def)
					f_error2("etiqueta indefinida -> ",tde[k].nomb);

			return k;

		};
	};
	if (etiq_top>=MAX_ETIQ)
	{
		error("tabla de etiquetas llena; proceso interrumpido");
		salida(0);
	};
	strcpy(tde[++etiq_top].nomb,ident);	/* si no, la a¤ade al final */
	tde[etiq_top].def=0;				/* y la marca como indefinida */
	tde[etiq_top].val=EO_LIST;
	return etiq_top;					/* devuelve su posición */
}

IN define_etiq(k,val)	/* asigna el valor val a la etiqueta k-ésima */
IN k, val;			/* y devuelve 0 si no estaba previamente definida */
{
	IN d1, d2;

	if ((k<0)||(k>etiq_top))
	{
		error("acceso fuera de tabla de etiquetas, proceso interrumpido");
		salida(0);
	};
	if (!tde[k].def)
	{
		d1=tde[k].val;
		while(d1!=EO_LIST)	/* si la etiqueta no estaba definida */
		{					/* se sigue la pista a la lista de direcciones */
			d2=m[d1];		/* donde asignar el valor */
			m[d1]=val;
			d1=d2;
		};
		tde[k].val=val;			/* se pone valor a la etiqueta */
		tde[k].def=1;			/* y se marca como definida */
		return 0;
	}
	else return 1;		/* una etiqueta no se puede redefinir */
}

IN codigo_mnemonico(ident)	/* devuelve el código del mnemónico ident */
CH ident[LONG_ID];
{
	IN k;

	for (k=0; k<NUM_MNE; ++k)
	{			/* búsqueda 'case insensitive' de ident en tdm */
		if (strcasecmp(ident,tdm[k])==0) return k;
	};
	return -1;	/* si ident no es mnemónico, se devuelve -1 */
}

IN get_token()		/* obtiene el siguiente token */
{
	SALTABLANCOS;
	if (cm==EQU)
	{
		switch(ca)		/* reconocimiento de operadores en expr. aritm. */
		{
			case '+':	token.tipo=MAS;
						leecar();
						return token.tipo;

			case '-':	token.tipo=MENOS;
						leecar();
						return token.tipo;

			case '*':	token.tipo=POR;
						leecar();
                        return token.tipo;

			case '/':	token.tipo=ENTRE;
						leecar();
                        return token.tipo;
		};
	};
	if (isdigit(ca)||(ca=='+')||(ca=='-')||(ca=='$')) get_numero();
	else if (ca=='\'') get_caracter();
	else if ((isalpha(ca))||(ca=='_')) get_ident();
	else if ((ca=='\0')||COM) token.tipo=FIN_DE_LINEA;
	else
	{
		switch(ca)
		{
			case ':':	token.tipo=DOS_PUNTOS;
            			leecar();
						break;

			case ',':	leecar();
						if (tolower(ca)!='i') error("tras ',' se espera 'i'");
						token.tipo=COMA_I;
						leecar();
						break;

			case '(':	token.tipo=ABRE_PARENTESIS;
            			leecar();
						break;

			case ')':	token.tipo=CIERRA_PARENTESIS;
            			leecar();
						break;

			case '[':	token.tipo=ABRE_CORCHETE;
            			leecar();
						break;

			case ']':	token.tipo=CIERRA_CORCHETE;
            			leecar();
						break;

			default:	token.tipo=NULO;
						leecar();
						error("token no reconocido");
		};
	};
	return token.tipo;
}

IN get_numero()	/* obtención de un token numérico */
{
	IN signo=1, fuera_rango=0;
	LI val=0;

	token.tipo=NUMERO;
	switch(ca)
	{
		case '-':	signo=-1;

		case '+':	leecar();
					SALTABLANCOS;

		num:		if (!isdigit(ca))
					{
						error("se espera un número entero en base 10");
						token.tipo=NULO;
						return token.tipo;
					};
					while (isdigit(ca))
					{
						val = ( signo==+1 ? 10*val+(ca-'0') : 10*val-(ca-'0'));
						if (!ENTERO_CORTO(val)) fuera_rango=1;
						leecar();
					};
					break;

		case '$':	leecar();
					SALTABLANCOS;
					if (!isxdigit(ca))
					{
						error("se espera un número hexadecimal");
						token.tipo=NULO;
						return token.tipo;
					};
					while(isxdigit(ca))
					{
						if (isdigit(ca))
						{
							val=16*val+(ca-'0');
						}
						else
						{
							val=16*val+(toupper(ca)-'A'+10);
						};
						if (!SINSIGNO_CORTO(val)) fuera_rango=1;
						leecar();
					};
					break;

		default:    if (isdigit(ca)) goto num;
					error("se espera un número");
					token.tipo=NULO;
					return token.tipo;

	};

	if (fuera_rango) error("entero fuera de rango");
	token.sem.val=(IN)val;
	return token.tipo;
}

IN get_caracter()		/* obtiene un carácter como token numérico */
{
	if (ca!='\'')
	{
		error("se espera definición de carácter");
		token.tipo=NULO;
		return 0;
	};
	token.tipo=NUMERO;	/* un carácter se considera token numérico */
	leecar();
	if (ca=='\\')
	{
		leecar();
		switch(ca)		/* lectura de códigos especiales */
		{
			case 'b':	ca='\b';	/* 'backspace' */
						break;
			case 'f':	ca='\f';	/* salto de página */
						break;
			case 'n':	ca='\n';	/* nueva línea */
						break;
			case 'r':	ca='\r';	/* retorno de carro */
						break;
			case 't':	ca='\t';	/* tabulador horizontal */
						break;
			case '\"':	ca='\"';	/* comilla dobles */
						break;
			case '\'':	ca='\'';	/* comilla simple */
						break;
			case '0':	ca='\0';	/* código nulo */
						break;
			case '\\':	ca='\\';	/* barra invertida */
						break;
			case 'v':	ca='\v';	/* tabulador vertical */
						break;
			case 'a':	ca='\a';	/* alerta (campana) */
						break;
			case 'o':	ca='o';	/* constante octal */
						break;
			case 'x':	ca='x';	/* constante hexadecimal */
						break;

			default:	error("carácter especial mal definido");
						return 0;

		};
	};
	token.sem.val=ca;
	leecar();
	if (ca!='\'')
	{
		error("carácter mal definido");
		return 0;
	};
	leecar();
	return token.tipo;
}

IN get_ident()		/* obtención de un identificador */
{
	IN k;

	if (!isalpha(ca)&&(ca!='_'))
	{
		error("se espera identificador");
		token.tipo=NULO;
		return token.tipo;
	};
	token.tipo=IDENTIFICADOR;
	for (k=0; k<LONG_ID; ++k)
	{
		token.sem.str[k]=ca;
		leecar();
		if (!(isalnum(ca))&&(ca!='_'))
		{
			token.sem.str[++k]='\0';
			return token.tipo;
		};
	};
	error("identificador demasiado largo");
	while ((isalnum(ca))||(ca=='_')) leecar();
	return token.tipo;
}

IN leecar()		/* lee siguiente carácter ascii de la línea */
{				/* y dá error si encuentra alguno que no es ascii */
	do
	{
		ca=linea[++num_car];
		if (isascii(ca)) return ca;
		error_ci();
	} while (1);
}

IN error_ci()		/* error de carácter ilegal */
{
	CH s[8];

        sprintf(s,"%u",ca);
	error2("carácter ilegal -> código ",s);
};


/***************** analizador de expresiones aritméticas *********************/


atr expr()			/*    E  ->  [ + | - ]  T  E1   */
{
	atr a;

	switch(token.tipo)
	{
		case MAS:
		case MENOS:		a.val=0;
						a.def=1;
						return expr1(a);

		default:		return expr1(term());
	};
}

atr expr1(a)		/*     E1  ->  ( + | - )  T  E1  |  lambda    */
atr a;
{
	atr b;
	LI rv;

	switch(token.tipo)
	{
		case MAS:	get_token();
					b=term();
					if (a.def&&b.def)
					{
						rv=(LI)a.val+b.val;
						if (!ENTERO_CORTO(rv))
						{
							if (ultima_pasada) f_error("suma fuera de rango");
							a.def=0;
						} else a.val=(IN)rv;
					}
					else a.def=0;
					return expr1(a);

		case MENOS:	get_token();
					b=term();
					if (a.def&&b.def)
					{
						rv=(LI)a.val-b.val;
						if (!ENTERO_CORTO(rv))
						{
							if (ultima_pasada) f_error("resta fuera de rango");
							a.def=0;
						} else a.val=(IN)rv;
					}
					else a.def=0;
					return expr1(a);

		default:	return a;
	};
}

atr term()			/*    T  ->  F  T1    */
{
	return term1(fact());
}

atr term1(a)		/*    T1  ->  ( * | / )  F  T1  |  lambda    */
atr a;
{
	LI rv;
	atr b;

	switch(token.tipo)
	{
		case POR:	get_token();
					b=fact();
					if (a.def&&b.def)
					{
						rv=(LI)a.val*b.val;
						if (!ENTERO_CORTO(rv))
						{
							if (ultima_pasada)
									f_error("producto fuera de rango");
							a.def=0;
						} else a.val=(IN)rv;
					}
					else a.def=0;
					a=term1(a);
					return a;

		case ENTRE:	get_token();
					b=fact();
					if (a.def&&b.def)
					{
						if (b.val==0)
						{
							if (ultima_pasada) f_error("división por cero");
							a.def=0;
						}
						else
						{
							rv=(LI)a.val/b.val;
							if (!ENTERO_CORTO(rv))
							{
								if (ultima_pasada)
										f_error("cociente fuera de rango");
								a.def=0;
							}
							else a.val=(IN)rv;
						};
					}
					else a.def=0;
					a=term1(a);
					return a;

		default:	return a;
	};
}

atr fact()			/*    F  ->  '('  E  ')'  |  Op    */
{
	atr a;

	if (token.tipo==ABRE_PARENTESIS)
	{
		get_token();
		a=expr();
		if (token.tipo!=CIERRA_PARENTESIS)
		{
			error("falta cerrar paréntesis");
		}
		else get_token();
	}
	else
	{
		a=oper();
	};
	return a;
}

atr oper()		/* define atributos de un operando */
{
	IN op;
	atr a;

	op=operando(NULL);
	switch(op)
	{								/* operando definido */
		case 1:		a.def=1;
					a.val=cop;
					get_token();
					return a;
									/* operando ausente */
		case 0:		error("falta operando");
					a.def=0;
					return a;
									/* operando indefinido */
		case -1:    a.def=0;
					get_token();
					return a;

		default:	f_error("operando no reconocido; error fatal");
					salida(0);
	};
}



/****************************** ejecutar ***********************************/

IN ejecutar()
{
	IN *p_op;	/* puntero al operando */
	LI rv;	/* <- resultado verdadero de una operación aritmética */
	IN m_dir, dir;

	ejecutando=1;
	clrscr();
	acum=0;
	r=0;
	co=0;
	sp=M;
	if (dep)
	{
		printf("\nEjecución en modo de depuración.\n");
		printf("Se muestra el contenido de los registros ");
		printf("y la siguiente instrucción.\n");
        printf("Pulse 't' para terminar, ");
		printf("y cualquier otra tecla para seguir.\n");
	};

	while (1)		/* ciclo de ejecución */
	{

		kbhit();   /*  <- sirve para interrumpir con Ctrl-Brk  */

		instr=*(pmem(co));	/* se toma la instrucción */
		m_dir=instr%NUM_DIR;	/* se decodifica */
		instr=instr/NUM_DIR;
		if (dep)			/* en modo de depuración, se desensambla */
		{
			printf("acum=%d($%X) co=%d($%X) ",acum,acum,co,co);
			printf("sp=%d($%X) r=%d($%X) ",sp,sp,r,r);
			printf("ix=%d($%X)\n",ix,ix);
			if ((instr<0)||(instr>=NUM_MNE))
			{
				error("Instrucción no reconocida");
			};
			printf("%5d: %s",co,tdm[instr]);
			switch(m_dir)					/* desensamblado */
			{
				case ACDIR: printf("\n");
							break;

				case ACIND:	printf(" ()\n");
							break;

                case INMED:	printf(",i %d\n",m[co+1]);
							break;

				case DIREC: printf(" %d\n",m[co+1]);
							break;

				case INDIR: printf(" (%d)\n",m[co+1]);
							break;

				case RELAT:	printf(" [%d]\n",m[co+1]);
							break;

				default:	error("Modo de direccionamiento no reconocido");
			};
			if (getch()=='t') exit(0);
		}
		switch (m_dir)	/* se determina la dirección del operando */
		{
			case ACDIR: dir=-1;	/* en este modo no hay dirección */
						p_op=&acum;
						break;

			case ACIND:	dir=acum;
						goto ld_p_op;

			case INMED:	dir=++co;
            			goto ld_p_op;

			case DIREC:	dir=*(pmem(++co));
            			goto ld_p_op;

			case INDIR:	dir=*(pmem(*(pmem(++co))));
						goto ld_p_op;

			case RELAT:	dir=ix+*(pmem(++co));

			ld_p_op:	p_op=pmem(dir);
						break;

			default:	error("Modo de direccionamiento no reconocido");
		};
		++co;	/* se avanza el contador de operación */
		switch (instr)
		{						/* se ejecuta la instrucción */
			case NOP:	break;

			case LDA:	acum=*p_op;
						break;
			case STA:	*p_op=acum;
						break;
			case LDSP:	sp=*p_op;
						break;
			case STSP:	*p_op=sp;
						break;
			case LDR:	r=*p_op;
						break;
			case STR:	*p_op=r;
						break;
			case LDIX:	ix=*p_op;
						break;
			case STIX:	*p_op=ix;
						break;

			case ADD:	rv=(LI)acum;
            			rv+=(LI)*p_op;
						acum+=*p_op;
						goto ajuste_r1;

			case SUB:	rv=(LI)acum;
            			rv-=(LI)*p_op;
						acum-=*p_op;
						goto ajuste_r1;

			case MUL:	rv=(LI)acum;
						rv*=(LI)*p_op;
                        acum*=*p_op;
						goto ajuste_r1;

			case DIV:   if (*p_op==0) error("División por cero");
						rv=(LI)acum/(LI)*p_op;
						if (!ENTERO_CORTO(rv))
								error("Cociente fuera de rango");
						r=acum-(IN)rv*(*p_op);
						acum=(IN)rv;
						break;

			case INC:   rv=(LI)*p_op;
						++rv;
                        ++(*p_op);
						goto ajuste_r2;

			case DEC:	rv=(LI)*p_op;
            			--rv;
						--(*p_op);
						goto ajuste_r2;

			case NEG:   rv=(LI)*p_op;
						rv=-rv;
                        *p_op=-(*p_op);
						goto ajuste_r2;

			ajuste_r1:	r=(IN)((rv-(LI)acum)>>16);
						break;

			ajuste_r2:	r=(IN)((rv-(LI)*p_op)>>16);
						break;

			case AND:	acum=((acum)&&(*p_op));
						break;

			case OR:	acum=((acum)||(*p_op));
						break;

			case NOT:	*p_op=(!*p_op);
						break;

			case PUSH:	APILAR(*p_op);
						break;

			case POP:	DESAPILAR(*p_op);
						break;

			case J:		goto salto;

			case JZ:	if (acum==0) goto salto;
						break;
			case JNZ:	if (acum!=0) goto salto;
						break;
			case JP:	if (acum>0) goto salto;
						break;
			case JNP:	if (acum<=0) goto salto;
						break;
			case JM:	if (acum<0) goto salto;
						break;
			case JNM:	if (acum>=0) goto salto;
						break;

			case CALL:	APILAR(co);
						goto salto;

			case RET:	DESAPILAR(co);
						pmem(co);
						break;

			salto:		if ((m_dir==ACDIR)||(m_dir==INMED))
						{
							error("Llamada o salto mal dirigido");
						};
						pmem(dir);
						co=dir;
						break;

			case STOP:	return acum;

			case INPUT:	*p_op=getch();
						if (*p_op==3) exit(0);	/* salida con Ctrl-C */
						break;
			case WRITE:	putchar((*p_op)&255);
						break;
			case ININT:	scanf("%d",p_op);	/* lee entero decimal */
						scanf("$%x",p_op);	/* lee entero hexadecimal */
						scanf("\'%c\'",p_op);	/* lee 'caracter' */
						fflush(stdin);	/* <- "limpia" la entrada */
						break;
			case WRINT:	printf("%d",*p_op);
						break;
			case WRSTR:	if ((m_dir==ACDIR)||(m_dir==INMED))
						{
							error("Instrucción WRSTR mal dirigida");
						};
						acum=*pmem(dir);
						while(acum!='\0')
						{
							putchar(acum&255);
							acum=*pmem(++dir);
						};
						break;

			default:	error("Instrucción no reconocida");
		};

	};
	ejecutando=0;
}



/*********************** operaciones con archivos **************************/

IN abrir_fuente()
{
	if ((f_fuente=fopen(nomb_fuente,"r"))==NULL)
	{
		fprintf(stderr,"No se puede abrir el archivo %s\n",nomb_fuente);
		exit(1);
	};
}

IN abrir_codigo()
{
	if ((f_codigo=fopen(NOMCOD,"w"))==NULL)
	{
		fprintf(stderr,"No se puede abrir el archivo %s\n",NOMCOD);
		exit(1);
	};
    fprintf(f_codigo,"Dirección:    \tCódigo:\n");
}

IN abrir_tabla()
{
	if ((f_tabla=fopen(NOMTAB,"w"))==NULL)
	{
		fprintf(stderr,"No se puede abrir el archivo %s\n",NOMCOD);
		exit(1);
	};
	fprintf(f_tabla,"    \t%*s \tdef: \tasig: \tvalor:\n",LONG_ID,"Etiqueta:");
}

IN actual_cod()		/* actualiza el archivo de código */
{
	IN k, x;

	abrir_codigo();
	for (k=0; k<mem_top; ++k)
	{
		kbhit();
		x=m[k];
		fprintf(f_codigo,"%6d (%04X): \t%6d (%04X) \t",k,k,x,x);
		if (CHAR(x))	/* impresión de caracteres */
		{
			if ((x<0)||(x>=32)) fprintf(f_codigo,"%c",x);
			else
			{
				switch(x)	/* impresión de algunos caracteres especiales */
				{
					case '\b':	fprintf(f_codigo,"\\b");
								break;
					case '\f':	fprintf(f_codigo,"\\f");
								break;
					case '\n':	fprintf(f_codigo,"\\n");
								break;
					case '\r':	fprintf(f_codigo,"\\r");
								break;
					case '\t':	fprintf(f_codigo,"\\t");
								break;
					case '\0':	fprintf(f_codigo,"\\0");
								break;
					case '\v':	fprintf(f_codigo,"\\v");
								break;
					case '\a':	fprintf(f_codigo,"\\a");
								break;
					default:	fprintf(f_codigo,"  ");
				};
			};
		};
		fprintf(f_codigo,"\n");
	};
	fclose(f_codigo);
}

IN actual_tde()	/* actualiza el archivo de etiquetas */
{
	IN k;

	abrir_tabla();
	for (k=0; k<=etiq_top; ++k)
	{
		kbhit();
		fprintf(f_tabla,"%3d \t%*s \t%4d \t%5d \t%6d\n",
					k,LONG_ID,tde[k].nomb,tde[k].def,tde[k].asig,tde[k].val);
	};
	fclose(f_tabla);
}


/*************************** manejo de errores ****************************/

IN * pmem(x)			/* test y acceso a memoria disponible */
IN x;		/* devuelve NULL si x cae fuera de la memoria disponible */
{					/* y puntero a m[x] si cae dentro */
	if (mem_top>=sp)
	{
		error("Falta memoria");
		salida(0);
	}
	else if (x<0)
	{
		error("Dirección negativa");
        return NULL;
	}
	else if (x>=M)
	{
		error("Intento de acceso fuera de la memoria");
        return NULL;
	}
	else if ((pila_inaccesible)&&(x>=sp))
	{
		error("Intento de acceso dentro de la pila");
        return NULL;
	}
	else if ((ensamblando)&&(x<mem_top))
	{
		error("intento de acceso dentro del código generado");
        return NULL;
	}
	else
	{
		return &(m[x]);
	};
}

IN error(mensaje)		/* mensajes de error en general */
CH mensaje[];
{
	if (ensamblando)
	{
		if ((num_pasada==1)||forz) ens_error(mensaje);
	}
    else if (ejecutando) ejec_error(mensaje);
	else printf("\nError: %s\n.",mensaje);
}

IN ens_error(mensaje)	/* mensajes de error en tiempo de ensamblado */
CH mensaje[];
{
	IN k;

	++n_errores;
	printf("%2d> Error: ",n_errores);
	printf("Línea %d; ",num_linea);
	printf("%s\n",mensaje);
	printf("%s\n",linea);
	for (k=0; k<num_car-1; ++k)
	{
		auto CH c=linea[k];
		if (c=='\t') putchar(c);
		else printf(" ");
	};
	printf("%c\n",124);
	if (n_errores>=MAX_ERROR)
	{
		printf("Demasiados errores; ensamblado interrumpido.\n");
		exit(0);
	};
	++cont;
	if (cont>=N_ERROR_PAUSA)
	{
		interrup();
	};
}

IN ejec_error(mensaje)	/* mensajes de error en tiempo de ejecución */
CH mensaje[];
{
	printf("\nError: %s.\n",mensaje);
	exit(0);
}

IN error2(mens1,mens2)		/* mensajes de error dobles */
CH *mens1, *mens2;
{
	CH mens[160];

/* Código que producía violación de segmento (DMC 2002)*/
/*	mens1[79]='\0';*/
/*	mens2[79]='\0';*/
/* Cambio de strcpy y strcat a strncpy y strncat para emular */
/* el mismo comportamiento */
	strncpy(mens,mens1,80);
	strncat(mens,mens2,80);
	error(mens);
};

CH pausa()		/* pausa actualizando archivos */
{
	if (cod)
	{
		actual_cod();
		actual_tde();
	};
	return(getch());
}

IN salida(k)			/* salida actualizando archivos */
IN k;
{
	if (cod)
	{
		actual_cod();
		actual_tde();
	};
	exit (k);
}


IN interrup()
{
	cont=0;
	printf("\nEnsamblado interrumpido temporalmente.\n");
	printf("Pulse una tecla para proseguir, o 't' para terminar.\n");
	if(pausa()=='t') exit(0);
	printf("\nProsigue el ensamblado...\n\n");
}

IN f_error(mens)		/* mensajes de error forzados */
CH mens[];
{
	forz=1;
	error(mens);
	forz=0;
}

IN f_error2(mens1,mens2)
CH mens1[], mens2[];
{
	forz=1;
	error2(mens1,mens2);
	forz=0;
}

