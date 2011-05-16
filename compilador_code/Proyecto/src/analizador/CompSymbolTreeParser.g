header
{
	package analizador;
	
	//| ANALISIS SEMÁNTICO -- Instalación de símbolos |
	
	import antlraux.context.ContextException;
	import antlraux.context.Scope;
	import antlraux.context.asts.*;
	import antlraux.context.types.Type;
	import antlraux.context.types.AttributeType;
	
	import leli.types.*;
	
	import antlraux.util.LexInfoAST;
	import antlraux.util.Logger;
	
	import antlr.TokenStreamException;
	
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
}

class CompSymbolTreeParser extends CompTreeParser;

options {

	importVocab=CompParserVocab;
	exportVocab=CompSymbolTreeParserVocab;
	buildAST = true;
	ASTLabelType = antlraux.util.LexInfoAST;
}

tokens
{
TIPO_ERROR = "error";
METACLASE = "metaclase";
}

{
	/* 
	 * La clase {link LeLiContext} es una fachada que facilita la manipulacion
	 * contextual
	 * */
	private CompContext contexto;
	
	/** El logger de la clase **/
	private Logger logger;
	
	/** Constructor habitual **/
	public CompSymbolTreeParser(Logger logger)
	throws RecognitionException
	{
		this();
		
		this.logger = logger;
		this.contexto = new CompContext(logger);
		
		setASTNodeClass("antlraux.util.LexInfoAST");
	}
	
	/** Constructor privado (utilizado para leer TiposBasicos.leli **/
	public CompSymbolTreeParser(Logger logger, CompContext contexto)
	throws RecognitionException
	{
		this();
		
		this.logger = logger;
		this.contexto = contexto;
		
		setASTNodeClass("antlraux.util.LexInfoAST");
	}
	
	public CompContext obtenerContexto()
	{ return contexto; }
	
	public void reportError( String msg,
	                         String filename,
	                         int line,
	                         int column )
	{   
		if(null==logger)
		{
			logger = new Logger("error", System.err);	
		}
		logger.log( msg, 1, filename, line, column);
	}
	
	public void reportError(RecognitionException e)
	{
		reportError( e.getMessage(), e.getFilename(),
		             e.getLine(), e.getColumn() );
		e.printStackTrace(System.out);
	}
}


// ------------------- FASE 1: Creacion de ambitos -----------------
//
// La fase 1 consiste en:
//  * Crear la jerarquia de ambitos
//  * Insertar las declaraciones de parametros, variables, atributos, clases, 
//    metodos y constructores en sus respectivos ambitos
//  * Asociar el ambito adecuado a la raiz de los ASTs de objetos con ambito
//    (clases, metodos, constructores, bucles y alternativas condicionales)
//

/* Regla que sirve para recorrer el AST de definicion de un programa LeLi  */

programa
	: #(PROGRAMA
	     instalarTiposBasicos
		(instDecVar)*
			( decMetodo
		  		| subprograma
		  			| decClase )*	// aqui irá la decClase, programas, metodos... 
			main)

      { ## = new ScopeAST(##, contexto.getCurrentScope()); }
	;
	
decClase
{
	CompType claseActualTipo = null;
	ScopeAST ast = null;
}:
	#( RES_CLASE nombre:IDENT #(RES_EXTIENDE padre:IDENT)
	{
		ast = new ScopeAST(##, contexto.abrirAmbitoClase (#nombre));
		
		if(contexto.tiposBasicosLeidos == true)
		{
			LeLiType tipoPadre = contexto.obtenerTipo(#padre);
			
			claseActualTipo =
			new LeLiType( #nombre.getText(),
			              tipoPadre,
			              contexto.getCurrentScope(),
			              new LeLiMetaType(#nombre.getText()) );
		} else {
			claseActualTipo =
				LeLiTypeManager.obtenerTipoBasico(
					#nombre.getText(), #nombre );
			claseActualTipo.setScope( contexto.getCurrentScope() );
		}
		
		claseActualTipo.insertarAtributosSuperClase();
		contexto.insertarDecClase(##, #nombre, claseActualTipo);
	}
	listaMiembrosContexto[ claseActualTipo ] )
	{
		contexto.cerrarAmbitoClase();
		claseActualTipo.insertarMetodosSuperClase();
		## = ast;
	}
	;