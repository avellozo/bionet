package general;

import java.io.IOException;
import java.util.ArrayList;

public class SubgraphsCount
{
	static long	subgraphsCount;

	public static void main(String args[]) throws IOException {

		long time = System.currentTimeMillis();

		if (args.length < 2) {
			System.out.println("usage:  java -jar motinf.jar general.SubgraphsCount <file .edges> k organismsId(list with ;) {R|P|U} ");
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

		System.out.print("SubgraphsCount");
		for (String arg : args) {
			System.out.print(" " + arg);
		}
		System.out.println();

		Graph graph = new Graph(fileName, organisms, withoutColor);

		System.out.println("Time to create the graph " + (System.currentTimeMillis() - time + " ms"));
		time = System.currentTimeMillis();

		subgraphsCount = 0;
		Node[] subgraph = new Node[k];
		for (Node node : graph) {
			if (node.isValid()) {
				node.setInvalid();
				subgraph[0] = node;
				createSubgraph(subgraph, 1);
			}
		}

		System.out.println("Time to calculate subGraphs " + (System.currentTimeMillis() - time) + " ms");
		System.out.println("Total subgraphs of size " + k + ": " + subgraphsCount);
		System.out.println("Nodes: " + graph.getNumberOfNodes());
		System.out.println("Edges: " + graph.getNumberOfEdges());
		System.out.println();
	}

	private static void createSubgraph(Node[] subgraph, int k1) {
		if (k1 == subgraph.length) {
			subgraphsCount++;
		}
		else {
			ArrayList<Node> returnValids = new ArrayList<Node>();
			for (int i = 0; i < k1; i++) {
				for (Node node : subgraph[i].getNeighbors()) {
					if (node.isValid()) {
						returnValids.add(node);
						node.setInvalid();
						subgraph[k1] = node;
						createSubgraph(subgraph, k1 + 1);
					}
				}
			}
			for (Node node : returnValids) {
				node.setValid();
			}
		}
	}
}
