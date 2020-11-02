
public class Produccion {

	String simbolo;
	String[] produccion;
	
	public Produccion(String s) {
		String[] c = s.split("->");
		simbolo = c[0];
		produccion = c[1].split("");
	}
	
	public Produccion(String simbolo, String[] produccion) {
		this.simbolo = simbolo;
		this.produccion = produccion;
	}
}
