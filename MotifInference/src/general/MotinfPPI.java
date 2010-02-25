package general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MotinfPPI
{
	static long	subgraphsCount;
	static int	notInTrie	= 0;

	public static void main(String args[]) throws IOException {

		long time = System.currentTimeMillis();

		if (args.length < 2) {
			System.out.println("usage:  java -jar motinf.jar metabolicNetwork.MotinfPPI <file .edges> k organismsId(list with ;) {R|P|U} ");
			//R = remove node with color '-'
			//P = proteinID for color '-'
			//U = unique color '-' for color '-'
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

		System.out.print("MotinfPPI");
		for (String arg : args) {
			System.out.print(" " + arg);
		}
		System.out.println();

		List<Node> graph = Node.createGraph(fileName, organisms, withoutColor);

		//set color id accordly the node quantity
		Node.sortByColorNodeQtty(graph);
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

		int maxSizeTrie = (int) (Runtime.getRuntime().maxMemory() * 2 / 3);
		if (maxSizeTrie < 0) {
			maxSizeTrie = Integer.MAX_VALUE - 1;
		}
		MotifTrie trie = new MotifTrie(maxSizeTrie);

		System.out.println("Time to create the graph " + (System.currentTimeMillis() - time + " ms"));
		time = System.currentTimeMillis();

		subgraphsCount = 0;
		Node[] motifPrefix = new Node[k];
		Node.sortByColor(graph);
		Color colorOld = null;
		int totalLeafs = 0;
		for (Node node : graph) {
			if (node.isValid()) {
				if (colorOld != node.getColor()) {
					totalLeafs += trie.totalLeafs;
					//					System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
					trie.clear();
					colorOld = node.getColor();
				}
				node.setInvalid();
				motifPrefix[0] = node;
				createMotif(motifPrefix, 1, trie);
			}
		}

		totalLeafs += trie.totalLeafs;
		int totalMotifs = totalLeafs + notInTrie;
		System.out.println("Time to calculate motifs " + (System.currentTimeMillis() - time) + " ms");
		System.out.println("Total subgraphs of size " + k + ": " + subgraphsCount);
		System.out.println("Total motifs of size " + k + ": " + totalMotifs);
		System.out.println("Total motifs didn't put in the trie " + ": " + notInTrie);
		System.out.println("Nodes: " + graph.size());
		System.out.println("Colors: " + lastColorId);
		System.out.println("Edges: " + edgesQtty);
		System.out.println("Occurrences:");
		int repeats[] = trie.repeats;
		for (int j = 0; j < repeats.length; j++) {
			if (repeats[j] != 0)
				System.out.println((j + 1) + " " + repeats[j]);
		}
		System.out.println();
	}

	//	private static void createMotif(Node[] motifPrefix, int k1, MotifTrie trie) {
	//		if (k1 == motifPrefix.length) {
	//			Node[] motifPrefixAux = new Node[k1];
	//			System.arraycopy(motifPrefix, 0, motifPrefixAux, 0, k1);
	//			Node.sortByColor(motifPrefixAux);
	//			short currentColor = -1;
	//			int nodeWithColors = 0;
	//			short[] motif = new short[k1];
	//			for (int i = 0; i < k1; i++) {
	//				motif[i] = motifPrefixAux[i].getColor().getId();
	//				//put in the trie only the motifs which colors don't appear more in the graph
	//				if (motif[i] != currentColor) {
	//					nodeWithColors += motifPrefixAux[i].getColor().getNumNodes();
	//					currentColor = motif[i];
	//				}
	//				nodeWithColors--;
	//			}
	//			subgraphsCount++;
	//			if (nodeWithColors == 0) {
	//				trie.repeats[0]++;
	//				notInTrie++;
	//			}
	//			else {
	//				trie.addMotif(motif);
	//			}
	//		}
	//		else {
	//			ArrayList<Node> returnValids = new ArrayList<Node>();
	//			Collection<Node> neighborsNodeI;
	//			for (int i = 0; i < k1; i++) {
	//				neighborsNodeI = motifPrefix[i].getNeighbors();
	//				for (Node node : neighborsNodeI) {
	//					if (node.isValid()) {
	//						returnValids.add(node);
	//						node.setInvalid();
	//						motifPrefix[k1] = node;
	//						createMotif(motifPrefix, k1 + 1, trie);
	//					}
	//				}
	//			}
	//			for (Node node : returnValids) {
	//				node.setValid();
	//			}
	//		}
	//	}

	private static void createMotif(Node[] motifPrefix, int k1, MotifTrie trie) {
		if (k1 == motifPrefix.length) {
			boolean notPutInTrie = true;
			short[] motif = new short[k1];
			for (int i = 0; i < k1; i++) {
				motif[i] = motifPrefix[i].getColor().getId();
				notPutInTrie = notPutInTrie && (motifPrefix[i].getColor().getNumNodes() == 1);
			}
			Arrays.sort(motif);
			subgraphsCount++;
			if (notPutInTrie) {
				trie.repeats[0]++;
				notInTrie++;
			}
			else {
				trie.addMotif(motif);
			}
		}
		else {
			ArrayList<Node> returnValids = new ArrayList<Node>();
			Collection<Node> neighborsNodeI;
			for (int i = 0; i < k1; i++) {
				neighborsNodeI = motifPrefix[i].getNeighbors();
				for (Node node : neighborsNodeI) {
					if (node.isValid()) {
						returnValids.add(node);
						node.setInvalid();
						motifPrefix[k1] = node;
						createMotif(motifPrefix, k1 + 1, trie);
					}
				}
			}
			for (Node node : returnValids) {
				node.setValid();
			}
		}
	}
}
