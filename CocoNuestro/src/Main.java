package compilationunit;
import java.io.Console;


public class Main {
	public static void main(String[] arg) {
	Scanner scanner = new Scanner(arg[0]);
	Parser parser = new Parser(scanner);
	parser.Parse();
	System.out.println(parser.errors.count + " errores detectados");
	}
	}


