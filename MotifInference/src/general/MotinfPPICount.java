package general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MotinfPPICount
{
	static long	subgraphsCount;

	public static void main(String args[]) throws IOException {

		long time = System.currentTimeMillis();

		if (args.length < 2) {
			System.out.println("usage:  java -jar motinf.jar metabolicNetwork.MotinfPPICount <file .edges> k organismsId(list with ;) {R|P|U} ");
			//R = remove node with color '-'
			//P = proteinID for color '-'
			//U = '-' for color '-'
			return;
		}
		String fileName = args[0];
		int k = (Integer.valueOf(args[1])).intValue();
		String withoutColor = args[3];
		String organismList = args[2];
		String[] organisms;
		if (organismList.equals("*")) {
			organisms = new String[0];
		}
		else {
			organisms = organismList.split(";");
		}

		System.out.print("MotinfPPICount");
		for (String arg : args) {
			System.out.print(" " + arg);
		}
		System.out.println();

		List<Node> graph = Node.createGraph(fileName, organisms, withoutColor);

		//		//set color id accordly the node quantity
		Node.sortByColor(graph);
		short lastColorId = 0;
		int edgesQtty = 0;
		for (Node node : graph) {
			if (node.getColor().getId() == 0) {
				if (lastColorId == Short.MAX_VALUE) {
					throw new RuntimeException("Error: more than 2^15-1 colors.");
				}
				node.getColor().setId(++lastColorId);
			}
			edgesQtty += node.getDegree();
			node.trimToSize();
		}
		edgesQtty = edgesQtty / 2;

		System.out.println("Time to create the graph " + (System.currentTimeMillis() - time + " ms"));
		time = System.currentTimeMillis();

		subgraphsCount = 0;
		Node[] motifPrefix = new Node[k];
		for (Node node : graph) {
			if (node.isValid()) {
				node.setInvalid();
				motifPrefix[0] = node;
				createSubgraph(motifPrefix, 1);
			}
		}

		System.out.println("Time to calculate subGraphs " + (System.currentTimeMillis() - time) + " ms");
		System.out.println("Total subgraphs of size " + k + ": " + subgraphsCount);
		System.out.println("Nodes: " + graph.size());
		System.out.println("Colors: " + lastColorId);
		System.out.println("Edges: " + edgesQtty);
		System.out.println();
	}

	private static void createSubgraph(Node[] motifPrefix, int k1) {
		if (k1 == motifPrefix.length) {
			subgraphsCount++;
		}
		else {
			ArrayList<Node> returnValids = new ArrayList<Node>();
			for (int i = 0; i < k1; i++) {
				for (Node node : motifPrefix[i].getNeighbors()) {
					if (node.isValid()) {
						returnValids.add(node);
						node.setInvalid();
						motifPrefix[k1] = node;
						createSubgraph(motifPrefix, k1 + 1);
					}
				}
			}
			for (Node node : returnValids) {
				node.setValid();
			}
		}
	}
}
