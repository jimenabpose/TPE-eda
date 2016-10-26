package graph;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Graph<V, E extends ArcGraph> extends GraphAdjList<V, E> {

	/*guarda la cantidad minima de colores encontrados para colorear el grafo*/
	int minColorsFound = Integer.MAX_VALUE;
	/*guarda el coloreo minimo encontrado para colorear el grafo*/
	HashMap<V,Integer> colorsGraphMap= new HashMap<V,Integer>();
	

	/*---Metodos estaticos que crean grafos conocidos---*/
	
	
	
	public static Graph<String, NoInfoArc> H(int k, int n){
		/*crea un grafo harary*/
		Graph<String, NoInfoArc> g = new Graph<String, NoInfoArc>();
		for(int i=0;i<n;i++)
			g.addVertex(nodeName(i));
		int r=k/2;
		for(int i=0 ;i<n ;i++)
			for(int j=(i+1)%n;j!=(i+r+1)%n;j=(j==n-1)?0:j+1)
				g.addArc(nodeName(i), nodeName(j), new NoInfoArc());	
		if(k%2!=0){
			if(n%2==0){
				for(int i=0;i<n/2;i++)
					g.addArc(nodeName(i), nodeName(i+n/2), new NoInfoArc());
			}
			else{
				g.addArc(nodeName(0), nodeName((n-1)/2), new NoInfoArc());
				g.addArc(nodeName(0), nodeName((n+1)/2), new NoInfoArc());
				for(int i=1;i<(n-1)/2;i++)
					g.addArc(nodeName(i), nodeName(i+(n+1)/2), new NoInfoArc());
			}
		}
		return g;
	}
	
	
	public static Graph<String, NoInfoArc> C(int n){
		/*crea un grafo ciclo de n vertices*/
		Graph<String, NoInfoArc> g=new Graph<String, NoInfoArc>();
		int i;
		g.addVertex(nodeName(0));
		for(i=1;i<n;i++){
			g.addVertex(nodeName(i));
			g.addArc(nodeName(i),nodeName(i-1) , new NoInfoArc());		
		}
		g.addArc(nodeName(0), nodeName(i-1), new NoInfoArc());
		return g;
	}
	
	
	public static Graph<String, NoInfoArc> K(int n){
		/*crea un grafo completo de n vertices*/
		Graph<String, NoInfoArc> g=new Graph<String, NoInfoArc>();
		for(int i=0;i<n;i++){
			g.addVertex(nodeName(i));
			for(int j=0;j<i; j++){
				g.addArc(nodeName(i), nodeName(j), new NoInfoArc());
			}
		}
		return g;
	}
	
	public static Graph<String, NoInfoArc> K(int n, int m){
		/*crea un grafo bipartito completo de n+m vertices */
		Graph<String, NoInfoArc> g=new Graph<String, NoInfoArc>();
		for(int i=0; i<n;i++ )
			g.addVertex(nodeName(i));
		for(int i=0;i<m;i++){
			g.addVertex(nodeName(n+i));
			for(int j=0;j<n;j++){
				g.addArc(nodeName(n+i), nodeName(j), new NoInfoArc());
			}
		}
		
		return g;
	}
	
	
	public static Graph<String, NoInfoArc>  randomTree(int n){
		/*crea un grafo arbol de n vertices al azar*/
		Graph<String, NoInfoArc> g = new Graph<String, NoInfoArc>();
		g.addVertex(nodeName(0));
		for(int i=1;i<n;i++){
			g.addVertex(nodeName(i));
			g.addArc(nodeName(i),nodeName((int)(Math.random()*i)), new NoInfoArc());
		}		
		return g;
	}
	
	
	public static Graph<String, NoInfoArc>  randomGraphConexo(int n){
		/*crea un grafo conexo de n vertices al azar*/
		Graph<String, NoInfoArc> g=randomTree(n);
		int num= (int) (Math.random()*((n-1)*(n-1)));
		for(int i=0;i<num;i++){
			g.addArc(nodeName((int)(Math.random()*n)), nodeName((int)(Math.random()*n)), new NoInfoArc());
		}
		return g;
		
	}
	
	
	
	/*------Fin de metodos estaticos que crean grafos conocidos---------*/
	
	
	
	
	
	/*---Metodos que levantan y guardan grafos en archivos---*/
	
	
	
	public static Graph<String, NoInfoArc> getGraph(String fileName){
		/*lee un archivo .graph y crea una instancia del grafo con la informacion del archivo*/
		Graph<String, NoInfoArc> g =new Graph<String, NoInfoArc>();
		try{
			File f=new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			for(String line=br.readLine(); line!=null ;line=br.readLine()){
				if(line.length()!=0){
					String nodeName1= "";
					String nodeName2= "";
					int i;
					for( i =0; line.charAt(i)!= ',' ;i++){
						if(i==line.length())
							throw new IllegalArgumentException("linea corrupta");/**/
						nodeName1 = nodeName1 + line.charAt(i);
					}
					g.addVertex(nodeName1);
					i++;
					for(; i != line.length(); i++){
						if(line.charAt(i)== ',')
							throw new IllegalArgumentException("linea corrupta");/**/
						nodeName2 = nodeName2 + line.charAt(i);
					}
					g.addVertex(nodeName2);
					g.addArc(nodeName1, nodeName2, new NoInfoArc());	
				}
			}
			br.close();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return g;
	}
	
	
	public void toColorFile(String name){
		/*crea un archivo [name].color y almacena el color de los vertices*/
		try{
			File f= new File(name + ".color");
			BufferedWriter br = new BufferedWriter(new FileWriter(f));
			for(Node n: nodeList){
				br.write(n.info.toString());
				Integer color=colorsGraphMap.get(n.info);
				if(color!=null){
					br.write("=" + color + "\n");
				}
			}
			br.close();
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public void toGraphFile(String name){
		/*crea un archivo [name].graph y almacena los datos del grafo*/
		clearMarks();
		try{
			File f= new File("./"+ name + ".graph");
			BufferedWriter br = new BufferedWriter(new FileWriter(f));
			for(Node n : nodeList){
				n.visited=true;
				for(Arc a:n.adj){
					if(!a.neighbor.visited)
						br.write(n.info.toString()+","+a.neighbor.info.toString() + '\n');
				}
			}
			
			br.close();
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public void toDotFile(String name){
		/*crea un archivo [name].dot y almacena los datos del grafo*/
		clearMarks();
		try{
			File f= new File(name + ".dot");
			BufferedWriter br = new BufferedWriter(new FileWriter(f));
			br.write("graph g{"+ '\n');
			for(Node n : nodeList){
				Integer color=colorsGraphMap.get(n.info);
				if(color!=null){
					br.write("\"" +n.info.toString() + "\"" +"[label="+ "\""+ n.info.toString() + "=" + color + "\"" +"]"+ "\n");
				}
			}
			for(Node n:nodeList){
				n.visited=true;
				for(Arc a:n.adj)
				{
					if(!a.neighbor.visited)
						br.write("\"" + n.info.toString() + "\"--\""+a.neighbor.info.toString() + "\"\n");
				}
			}
			br.write("}");
			br.close();
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	
	/*------Fin de metodos que se relacionan con archivos-------*/
	
	
	
	
	/*----------------------------Metodos de coloreo-------------*/
	
	
	/*greedy*/
	
	public int greedy(){
		/*Metodo raw que elige por que nodo comenzar el greedy.
		 * (no es el famoso greedy que agarra nodos al azar ni el greedy que agarra nodos ordenados por grado,
		 * es un greedy que agarra nodos ordenados por adyacencia).
		 * Retorna la cantidad de colores usados.*/
		clearMarks();
		ColorsChange colors = new ColorsChange();
		return greedy(nodeList.get(0),1,colors);
	}
	
	private int greedy(Node node, int max, ColorsChange colors){
		/*Metodo recursivo privado que en tiempo polinomico encuentra un coloreo del grafo, 
		 * que aunque no sea nescesariamente minimo, se intenta que sea bueno.
		 * Retorna la cantidad de colores usados. */
		node.visited = true;
		int min = colors.minColor(node);
		colorsGraphMap.put(node.info, min);
		Node fullColor = null;
		for(Arc a : node.adj){
			if(!a.neighbor.visited){
				colors.removeColor(a.neighbor, min);
				int j = colors.minColor(node);
				if(j > max){
					fullColor = a.neighbor;
				}
			}
		}
		if(fullColor != null){
			max = greedy(fullColor,max+1,colors);
		}
		for(Arc a : node.adj){
			if(!a.neighbor.visited){
				max = greedy(a.neighbor,max,colors);
			}
		}
		return max;
	}
	
	/*fin greedy*/
	
	/*exact*/
	
	public Graph<String , NoInfoArc> exact(){
		/*metodo raw que colorea un subgrafo kn del grafo original y elije con que nodo comenzar el exact.
		 * Retorna un arbolque indica el recorrido de vertices del metodo.*/
		clearMarks();
		Graph<String, NoInfoArc> tree = new Graph<String, NoInfoArc>();
		HashMap<V, Integer> actualColors = new HashMap<V, Integer>();
		int colorCant = 1;
		ColorsChange colors=new ColorsChange();
		
		Node node=nodeList.get(0);
		String previous=null;
		actualColors.put(node.info, 1);
		for(Arc a : node.adj){
			colors.removeColor(a.neighbor, 1);
			int minPosibleColor=colors.minColor(a.neighbor);
			if(minPosibleColor>colorCant){
				String actual= a.neighbor.info.toString() + "(" + tree.getCounter(a.neighbor.info.toString()) + ")" ;
				colorCant++;
				tree.addVertex(actual);
				tree.addArc(actual, previous, new NoInfoArc());
				previous = actual;
				actualColors.put(a.neighbor.info,minPosibleColor);
				a.neighbor.visited = true;
				for(Arc b: a.neighbor.adj){
					colors.removeColor(b.neighbor, minPosibleColor);
				}
			}
		}
		tree.addArc(exact(node, colors, tree, actualColors, colorCant),previous,new NoInfoArc());
		return tree;
	}
	
	@SuppressWarnings("unchecked")
	private String exact(Node node, ColorsChange colors, Graph<String, NoInfoArc> tree, HashMap<V, Integer> actualColors, int colorCant){
		/*Metodo recursivo privado que colorea el grafo con un coloreo minimo en tiempo exponencial.
		 * Retorna el nombre de la raiz del arbol que arma*/
		if(node.visited){
			return null;
		}
		if(colorCant >= minColorsFound){
			return null;
		}
		String nodeInfo= node.info.toString() + "(" + tree.getCounter(node.info.toString()) + ")";
		tree.addVertex(nodeInfo);
		int minColor = colors.minColor(node);
		node.visited = true;
		actualColors.put(node.info, minColor);
		if(minColor > colorCant){
			colorCant = minColor;
		}
		for(Arc a : node.adj){
			colors.removeColor(a.neighbor, minColor);
		}
		for(Node n : nodeList){
			tree.addArc(exact(n,colors,tree, actualColors, colorCant),nodeInfo, new NoInfoArc());
		}
		if(unvisited() == null){
			if(colorCant < minColorsFound){
				minColorsFound = colorCant;
				colorsGraphMap = (HashMap<V, Integer>) actualColors.clone();
			}
		}
		node.visited = false;
		actualColors.remove(node.info);
		for(Arc a : node.adj){
			colors.addColor(a.neighbor, minColor);
		}
		return nodeInfo;
	}
	
	
	private int getCounter(String info) {
		/*Metodo privado hecho para que al construir el arbol de recursion en el metodo exact, este sea un arbol.
		 * Retorna el numero de veces que se visito ya ese nodo.*/
		int i;
		for(i=0;nodes.containsKey(info+"("+ i +")");i++);
		return i;
	}
	
	/*fin exact*/

	/*taboo search*/
	public void ts(){
		/*Metodo que hace encuentra el mejor coloreo que puede encontrar en un segundo */
		ts(1000,20,10);
	}
	
	
	@SuppressWarnings("unchecked")
	private void ts(int time, int iter, int T){
		/*Metodo taboo search que resuelve el coloreo de grafos en determinado tiempo, iteraciones y
		 *  probavilidad de quedarse con un colorea peor o igual al acual*/
		HashMap<V, Integer> bestSolution = new HashMap<V, Integer>();
		int bestColorsCant =  Integer.MAX_VALUE;
		while(time>0){
			long ini=System.currentTimeMillis();
			minColorsFound=greedy();
			HashMap<V, Integer> neighbour = new HashMap<V, Integer>();
			HashMap<V, Integer> frecuencyMap= new HashMap<V, Integer>();
			List<Node> frecuencyList= new ArrayList<Node>();
			for(Node n: nodeList){
				frecuencyMap.put(n.info, 0);
				frecuencyList.add(n);
			}
			for(int i=0;i<iter;i++){
				Node node=frecuencyList.get((int)(Math.random()*(frecuencyList.size()-1)));
				neighbour = neighbour(node);
				int actualEval= minColorsFound;
				int neighbourEval=eval(neighbour);
				if(actualEval>neighbourEval || Math.random()<Math.exp((actualEval-neighbourEval)/T)){
					int frec=frecuencyMap.get(node.info)+nodeList.size()/3;
					frecuencyMap.put(node.info,frec);
					if(frec>nodeList.size()/2){
						frecuencyList.remove(node);
					}
					colorsGraphMap=(HashMap<V, Integer>)neighbour.clone();
					minColorsFound=neighbourEval;
				} 	
				
				for(Node n: nodeList){
					int frec=frecuencyMap.get(n.info);
					frec=frec==0?0:frecuencyMap.get(n.info)-1;
					frecuencyMap.put(n.info,frec);
					if(frec <= nodeList.size()/2){
						frecuencyList.add(n);
					}
				}
				if(minColorsFound < bestColorsCant){
					bestColorsCant = minColorsFound;
					bestSolution = (HashMap<V, Integer>)colorsGraphMap.clone();
				}
			}
			
			
			time-=(System.currentTimeMillis()-ini);
		}
		minColorsFound = bestColorsCant;
		colorsGraphMap = (HashMap<V, Integer>) bestSolution.clone();
	}

	private int eval(HashMap<V,Integer> coloring){
		/*Metodo privado.
		 * Retorna la cantidad de colores que se uso en este coloreo*/
		int max=0;
		for(Node n: nodeList){
			int color = coloring.get(n.info);
			max=color > max? color : max;
		}
		return max;
	}
	
	
	
	private HashMap<V,Integer> neighbour(Node node){
		/*Metodo raw privado de neighbour, que elije un color al azar para que neighbour pinte el nodo que le pasaron.
		 * Retorna un hash mas con el coloreo.*/
		clearMarks();
		ColorsChange colors=new ColorsChange();
		HashMap<V, Integer> map= new HashMap<V, Integer>();
		int color=(int)(Math.random()* (minColorsFound-1))+1;
		neighbour(node, color,map, colors);
		for(Node n : nodeList){
			if(n.visited==false){
				map.put(n.info, colorsGraphMap.get(n.info));
			}
		}
		return map;
		
	}


	private void neighbour(Node node, int color, HashMap<V, Integer> map, ColorsChange colors) {
		/*Metodo privado recursivo que encuentra un vecino para el taboo search.*/
		map.put(node.info, color);
		node.visited=true;
		for(Arc a: node.adj){
			colors.removeColor(a.neighbor, color);
		}
		for(Arc a: node.adj){
			if(a.neighbor.visited==false && colorsGraphMap.get(a.neighbor.info)==color){
				int minColor=colors.minColor(a.neighbor);
				neighbour(a.neighbor, minColor,map,colors);
			}
		}
		
	}

	/*fin taboo search*/
	

	public Node maxDegree(){
		Node maxDegree = nodeList.get(0);
		for(Node node : nodeList){
			maxDegree = degree(node.info) > degree(maxDegree.info)? node:maxDegree;
		}
		return maxDegree;
	}
	
	
private class ColorsChange{
	/*Clase iner privada que indica que colores puede y que colores no puede tener determinado nodo.*/
	
	
	HashMap<V, Integer[]> map;
	
	public ColorsChange(){
		/*Constructor que inicializa la estructura*/
		map = new HashMap<V, Integer[]>();
		
		for(Node n:nodeList){
			Integer[] vec= new Integer[5];
			for(int i=0;i<5;i++){
				vec[i]=0;
			}
			map.put(n.info, vec);
		}
	}
	
	public String toString(){
		/*Imprime la estructura
		 * (solo se utiliza para "debug")*/
		String rta = "";
		for(Entry<V, Integer[]> info : map.entrySet()){
			for(int i = 1; i < info.getValue().length; i++){
				rta += info.getValue()[i] + " ";
			}
			rta += "\n";
		}
		return rta;
	}
	
	private void removeColor(Node node, int i) {
		/*Indica que el nodo node tiene un nuevo nodo adyacente con el color i*/
		Integer[] colors = map.get(node.info);
		
		if(i>=colors.length){
			Integer[] aux= new Integer[i+5];
			int j;
			for(j=0;j<colors.length;j++){
				aux[j]=colors[j];
			}
			for(;j<aux.length;j++){
				aux[j]=0;
			}
			colors=aux;
		}
		colors[i]++;
		
		map.put(node.info, colors);
	}
	
	private void addColor(Node node, int i) {
		/*Indica que el nodo node tiene un nodo adyacente menos con el color i*/
		Integer[] colors = map.get(node.info);
		colors[i]--;
	}
	
	private int minColor(Node node){
		/*Retorna el minimo color con el que se puede pintar el nodo para que siga siendo un coloreo.*/
		int i;
		for(i=1;i<map.get(node.info).length &&  map.get(node.info)[i]>0;i++);
		return i;
	}
	
}
	
	
	
	/*---------------Fin de metodos de coloreo----------------*/
	
	
	
	
	
	
	@Override
	public void addArc(V v, V w, E e) {
		super.addArc(v, w, e);
		super.addArc(w, v, e);
	}
	
	@Override
	public void removeArc(V v, V w) {
		super.removeArc(v, w);
		super.removeArc(w, v);
	}
	
	public int degree(V v) {
		Node node = nodes.get(v);
		if (node != null) {
			return node.adj.size();
		}
		return 0;
	}

	public boolean isConnected() {
		if (isEmpty()) {
			return true;
		}
		clearMarks();
		List<Node> l = getNodes();
		List<V> laux = new ArrayList<V>();
		DFS(l.get(0), laux);
		for (Node node : l) {
			if (!node.visited) {
				return false;
			}
		}
		return true;
	}
	
	public int connectedComponents() {
		clearMarks();
		return pathCount();
	}
	
	private int pathCount() {
		int count = 0;
		Node node;
		while ((node = unvisited()) != null) {
			count++;
			DFS(node, new ArrayList<V>());
		}
		return count;
	}

	private Node unvisited() {
		for(Node node : getNodes()) {
			if (! node.visited )
				return node;
		}
		return null;
	}
	
	public boolean cutVertex(V vertex) {
		Node node = nodes.get(vertex);
		if (node == null || node.adj.size() == 0)
			return false;
		clearMarks();
		int components = pathCount();
		clearMarks();
		node.visited = true;
		return components != pathCount();
	}
	
	public boolean isBridge(V v, V w) {
		E e = isArc(v,w);
		if ( e == null)
			return false;
		int components = connectedComponents();
		removeArc(v, w);
		int newComponents = connectedComponents();
		addArc(v, w, e);
		return components != newComponents;
		
	}
	
	private static String nodeName(int i){
		/*crea nombres distintos para distintos j*/
		if(i==0){
			return "A";
		}
		String s= "";
		for(;i != 0;i/=26)
			s=  String.valueOf(Character.toChars(65 + i%26)) + s;
		return s;
	}


}
