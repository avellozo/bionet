package general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class MotinfPPI
{
	static long					subgraphsCount;
	static int					notInTrie;
	static StatisticalModel	statisticalModel;

	public static void main(String args[]) throws IOException {

		long time = System.currentTimeMillis();

		if (args.length < 2) {
			System.out.println("usage:  java -jar motinf.jar general.MotinfPPI <file .edges> k organismsId(list with ;) {R|P|U} b(best z-score)");
			//R = remove node with color '-'
			//P = proteinID for color '-'
			//U = unique color '-' for color '-'
			//B- get the b motifs with the best z-scores
			return;
		}
		String fileName = args[0];
		int k = (Integer.valueOf(args[1])).intValue();
		String withoutColor = args[3];
		String organismList = args[2];
		int numberOfBestMotifs = (Integer.valueOf(args[4])).intValue();

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

		Graph graph = new Graph(fileName, organisms, withoutColor);

		graph.setColorIdByColorOccurrences();
		//set color id accordly the node quantity of the color
		graph.sortNodesByColorId();
		statisticalModel = new ErdosRenyiModel(graph);
		MotifCollection bestMotifs = new BestMotifCollection(numberOfBestMotifs);

		//		graph.sortByColorNodeQtty();
		//		short lastColorId = 0;
		//		int edgesQtty = 0;
		//		for (Node node : graph) {
		//			if (node.getColor().getId() == 0) {
		//				if (lastColorId == Short.MAX_VALUE) {
		//					throw new RuntimeException("Error: more than 2^15-1 colors.");
		//				}
		//				node.getColor().setId(++lastColorId);
		//			}
		//			edgesQtty += node.getDegree();
		//			//			node.trimToSizeNeighbors();
		//		}
		//		edgesQtty = edgesQtty / 2;

		int maxSizeTrie = (int) (Runtime.getRuntime().maxMemory() * 2 / 3);
		if (maxSizeTrie < 0) {
			maxSizeTrie = Integer.MAX_VALUE - 1;
		}
		MotifTrie trie = new MotifTrie(maxSizeTrie);

		System.out.println("Time to create the graph " + (System.currentTimeMillis() - time + " ms"));
		System.out.println("Nodes: " + graph.getNumberOfNodes());
		System.out.println("Edges: " + graph.getNumberOfEdges());
		System.out.println("Colors: " + graph.getNumberOfColors());
		time = System.currentTimeMillis();

		subgraphsCount = 0;
		notInTrie = 0;
		Node[] subgraph = new Node[k];
		Color colorOld = null;
		int totalLeafs = 0;
		for (Node node : graph) {
			if (node.isValid()) {
				if (!node.getColor().equals(colorOld)) {
					totalLeafs += trie.totalLeafs;
					//					System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
					trie.getBestMotifs(bestMotifs, statisticalModel, numberOfBestMotifs, k);
					trie.clear();
					colorOld = node.getColor();
				}
				node.setInvalid();
				subgraph[0] = node;
				createMotif(subgraph, 1, trie, bestMotifs);
			}
		}

		totalLeafs += trie.totalLeafs;
		int totalMotifs = totalLeafs + notInTrie;
		trie.getBestMotifs(bestMotifs, statisticalModel, numberOfBestMotifs, k);
		System.out.println("Time to calculate motifs " + (System.currentTimeMillis() - time) + " ms");
		System.out.println("Total subgraphs of size " + k + ": " + subgraphsCount);
		System.out.println("Total motifs of size " + k + ": " + totalMotifs);
		System.out.println("Total motifs didn't put in the trie " + ": " + notInTrie);
		if (k == 1) {
			System.out.println("Occurrences:");
			int repeats[] = trie.repeats;
			for (int j = 0; j < repeats.length; j++) {
				if (repeats[j] != 0)
					System.out.println((j + 1) + " " + repeats[j]);
			}
			System.out.println();
		}
		System.out.println("Motifs selected:");
		System.out.println("zScore|Occurrences|Motif");
		for (Motif motif : bestMotifs.getMotifs()) {
			System.out.println(motif.getzScore() + "|" + motif.getNumberOfOccurrences() + "|" + motif.colorsToString());
		}
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

	private static void createMotif(Node[] subgraph, int k1, MotifTrie trie, MotifCollection bestMotifs) {
		if (k1 == subgraph.length) {
			boolean onlyOneOccurrence = true;
			Color[] colors = new Color[k1];
			for (int i = 0; i < k1; i++) {
				colors[i] = subgraph[i].getColor();
				onlyOneOccurrence = onlyOneOccurrence && (colors[i].getNumNodes() == 1);
			}
			subgraphsCount++;
			Arrays.sort(colors);
			if (onlyOneOccurrence) {
				trie.repeats[0]++;
				notInTrie++;
				double zScore = statisticalModel.getZScore(colors, 1);
				Motif motif = new Motif(colors, 1, zScore);
				bestMotifs.add(motif);
			}
			else {
				trie.addMotif(colors);
			}
		}
		else {
			ArrayList<Node> returnValids = new ArrayList<Node>();
			Collection<Node> neighborsNodeI;
			for (int i = 0; i < k1; i++) {
				neighborsNodeI = subgraph[i].getNeighbors();
				for (Node node : neighborsNodeI) {
					if (node.isValid()) {
						returnValids.add(node);
						node.setInvalid();
						subgraph[k1] = node;
						createMotif(subgraph, k1 + 1, trie, bestMotifs);
					}
				}
			}
			for (Node node : returnValids) {
				node.setValid();
			}
		}
	}
}
