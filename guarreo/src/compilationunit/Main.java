package compilationunit;


/**
 *
 * @author Edu
 */
public class Main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        // TODO code application logic here

        boolean mergeErrors = false;
                String inputName = null;


                for (int i = 0; i < args.length; i++) {
                        if (args[i].toLowerCase().equals("-l")) mergeErrors = true;
                        else inputName = args[i];
                }

        //Por ahora se pasará el archivo directamente.

//        inputName = "F:\\Universidad\\4º\\Compiladores\\pruebas_definitivas\\prueba1\\PruebaWeb.java";
        inputName = "/home/fernando/Escritorio/prueba1.java";
//    inputName = "F:\\Universidad\\4º\\Compiladores\\pruebas_definitivas\\prueba1\\prueba1.java";
//        inputName = "F:\\Universidad\\4º\\Compiladores\\pruebas_definitivas\\prueba3\\prueba3.java";


                if (inputName == null) {
                        System.err.println("No hay archivo de entrada seleccionado");
                        System.exit(1);
                }

                int pos = inputName.lastIndexOf('/');
                if (pos < 0) pos = inputName.lastIndexOf('\\');
                String dir = inputName.substring(0, pos+1);

                Scanner.Init(inputName);
                Errors.Init(inputName, dir, mergeErrors);
        Parser parser = new Parser();
        
                parser.Parse();
        
                Errors.Summarize();

        }

} // end driver
