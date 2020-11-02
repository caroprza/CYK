import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class cyk {
	
	String inicial;
	
	String[] cadena;
	
	Produccion[] generadores;
	
	String[][] table;
	
	Hashtable<String, String> alfabeto;
	
	public cyk() {
		Scanner s = new Scanner(System.in);
		System.out.println("Escribe la cadena: ");
		cadena = s.nextLine().split("");
		
		System.out.println("Escribe el simbolo inicial: ");
		inicial = s.nextLine();
		
		System.out.println("Escribe las producciones (forma X->Y): ");
		String line = s.nextLine();
		String p = line;
		while(!line.equals("0")) {
			p = p.concat(line.concat(","));
			line = s.nextLine();
		}
		String[] temp = p.split(",");
		generadores = new Produccion[temp.length];
		int j = 0;
		for (String i : temp) {
			generadores[j] = new Produccion(i);
			j++;
		}
		
		
		
		table = new String[cadena.length][cadena.length];
		
		s.close();
		
		
	}
	
	
	public void doCYK() {
		int n = cadena.length;
		
		
		//Calcular las subcadenas de longitud 1
		for (int i = 0; i < n; i++) {
			for (Produccion t : generadores) {
				if(cadena[i].equals(t.produccion[0])) {
					table[i][i] = t.simbolo;
				}
				
			}
			
		}
		
		//Calcular las subcadenas de longitud mayor o igual a 2
		for (int l = 1; l < n; l++) {
			for (int i = 0; i < n-l; i++) {
				int j = i+l;
				table[i][j] = "0";
				for (int k = i; k <= j-1; k++) {
					
					for (Produccion g : generadores) {
						if(table[i][k].equals(g.produccion[0]) && table[k+1][j].equals(g.produccion[1])) {
							table[i][j] = g.simbolo;
						}
						
					}
				}
				
			}
		}
		
		
		
	
		if (table[0][n-1].equals(inicial)) {
			System.out.println("Pertenece a la gramatica");
		}
		else{
			System.out.println("No pertenece a la gramatica");
		}
		
		
		System.out.println("\nDerivacion: \n");
		
		Queue<Pair> deriv = new LinkedList<Pair>();
		
		Pair tope = new Pair(-1,-2);
		
		deriv.add(new Pair(0, n-1));
		deriv.add(tope);
		
		while(!deriv.isEmpty()) {
			Pair current = deriv.poll();
			
			if (!current.equals(tope)) {
				System.out.print(table[current.first][current.second] + ", ");
				caro:
				for (int i = 1; i < n; i++) {
					if(current.first == current.second) break;
					
					String c = "";
					c = c.concat(table[current.first][i - 1 + current.first]);
					c = c.concat(table[current.first + i][current.second]);
					
					
					for(int k = 0; k < generadores.length; k++) {
						
						if(arrayToString(generadores[k].produccion).equals(c)) {
							
							Pair a = new Pair(current.first, i - 1 + current.first);
							Pair b = new Pair(current.first + i, current.second);
							deriv.add(a);
							deriv.add(b);
							deriv.add(tope);
							break caro;
						}
					}
				}
			}
			else {
				System.out.println();
			}
			
		}
		
		
	}
	
	public static String arrayToString(String[] lista) {
		String temp = "";
		for (String i : lista) {
			temp = temp.concat(i);
		}
		return temp;
	}
	
	
	public void Chomsky() {
		
		ArrayList<Produccion> producciones = new ArrayList<Produccion>(Arrays.asList(generadores));
		
		Hashtable<String, String> simbolos = new Hashtable<>();
		
		//1.Eliminar las e producciones
		
		Iterator<Produccion> i = producciones.iterator();
		Iterator<Produccion> j = producciones.iterator();
		String nuevaProduccion = "";
		Produccion prod, prodTemp;
		boolean tieneA;
		while(i.hasNext()) {
			prod = i.next();
			
			//La epsilon produccion se considera con un 0
			if(prod.produccion.length == 1 && prod.produccion[0].equals("0")) {
				producciones.remove(prod);
				
				//Agregar las nuevas producciones generadas al eliminar epsilon
				while(j.hasNext()) {
					prodTemp = j.next();
					tieneA = false;
					for (int k = 0; k < prodTemp.produccion.length; k++) {
						if (prodTemp.produccion[k].equals(prod.simbolo)) {
							tieneA = true;
						}
						else {
							nuevaProduccion = nuevaProduccion.concat(prod.produccion[k]);
						}
					}
					if (tieneA) {
						producciones.add(new Produccion(prodTemp.simbolo + "->" + nuevaProduccion));
					}
					nuevaProduccion = "";
				}
			}
			
			
			if(simbolos.isEmpty()) {
				simbolos.put(prod.simbolo, prod.simbolo);
			}
			else if (!simbolos.containsKey(prod.simbolo)) {
				simbolos.put(prod.simbolo, prod.simbolo);
			}
		}
		
		
		//2.Eliminar las producciones unitarias
		
		i = producciones.iterator();
		j = producciones.iterator();
		
		while(i.hasNext()) {
			prod = i.next();
			if(prod.produccion.length == 1 && simbolos.containsKey(prod.produccion[0])) {
				producciones.remove(prod);
				while(j.hasNext()) {
					prodTemp = j.next();
					if(prodTemp.simbolo.equals(prod.produccion[0])) {
						producciones.add(new Produccion(prod.simbolo, prodTemp.produccion));
					}
				}
			}
		}
		
		//temp es producciones
		
		
		//3. Eliminar las producciones inutiles
		
		i = producciones.iterator();
		j = producciones.iterator();
		
		boolean isUseless = true;
		
		while(i.hasNext()) {
			prod = i.next();
			while(j.hasNext()) {
				prodTemp = j.next();
				
				for(int k = 0; k < prodTemp.produccion.length; k++) {
					if(prod.simbolo.equals(prodTemp.produccion[k]) && !prod.equals(prodTemp)) {
						isUseless = false;
						break;
					}
				}
			}
			if(isUseless) {
				producciones.remove(prod);
			}
			
		}
		
		//4. Para cada simbolo terminal agregar una nueva produccion
		
		i = producciones.iterator();
		String s = "";
		
		while(i.hasNext()) {
			prod = i.next();
			for (int k = 0; k < prod.produccion.length; k++) {
				s = s.concat(prod.produccion[k]);
				if(prod.produccion.length != 1 && !simbolos.containsKey(prod.produccion[k])) {
					Random r = new Random();
					char c = (char) (r.nextInt(26) + 'A');
					while(!simbolos.containsKey(Character.toString(c))) {
						c = (char) (r.nextInt(26) + 'A');
					}
					simbolos.put(Character.toString(c), Character.toString(c));
					Produccion p = new Produccion(new Produccion(Character.toString(c)) + "->" + prod.produccion[k]);
					producciones.add(p);
					s = s.concat(Character.toString(c));
				}
			}
			prod.produccion = s.split("");
		}
		
		
		
		//5. Eliminar las producciones con más de dos simbolos
		
		i = producciones.iterator();
		s = "";
		
		while(i.hasNext()) {
			prod = i.next();
			
			if(prod.produccion.length > 2) {
				for (int k = 1; k < prod.produccion.length; k++) {
					s = s.concat(prod.produccion[k]);
				}
				Random r = new Random();
				char c = (char) (r.nextInt(26) + 'A');
				while(!simbolos.containsKey(Character.toString(c))) {
					c = (char) (r.nextInt(26) + 'A');
				}
				simbolos.put(Character.toString(c), Character.toString(c));
				Produccion p = new Produccion(new Produccion(Character.toString(c)) + "->" + s);
				producciones.add(p);
				String nueva = prod.produccion[0] + Character.toString(c);
				prod.produccion = nueva.split("");
			}
		}
		
		
		this.generadores = producciones.toArray(this.generadores);
		
		
	}
	
	

	public static void main(String[] args) {
		cyk prueba = new cyk();
		prueba.doCYK();
	}

}
