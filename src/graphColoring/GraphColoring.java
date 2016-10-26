package graphColoring;

import graph.Graph;
import graph.NoInfoArc;


public class GraphColoring {

	public static void main(String[] args) {
		
		if(args.length < 2){
			throw new WrongArgumentsQuantityException("At least a filename and a methodname were expected as arguments");
		}
		
		String file = args[0];
		String fileName = file.substring(0, file.length()-6);
		
		String method = args[1];
		
		boolean tree = false;
		
		if(args.length > 2 && args[2].equals("tree")){
			tree = true;
		}
		
		Graph<String, NoInfoArc> g = Graph.getGraph("./graph/" + file);
		if(method.equals("ts")){
			g.ts();
		} else if(method.equals("greedy")){
			g.greedy();
		} else if(method.equals("exact")){
			Graph<String, NoInfoArc> graph = g.exact();
			if(tree){
				graph.toDotFile("./" + method + "/" + fileName + "Tree");
			}
		}
		g.toColorFile("./" + method + "/" + fileName);
		g.toDotFile("./" + method + "/" + fileName);
	}
}