// works with hash table with mapping of colors to reduce key size
// Design and implementation Cinzia Pizzi, 2007

package metabolicNetwork;

import general.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import trie.MotifTrie;

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
		Node.sortByColorQtty(graph);
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

		//		int maxSizeTrie = (int) (Runtime.getRuntime().maxMemory() * 2 / 3);
		//		if (maxSizeTrie < 0) {
		//			maxSizeTrie = Integer.MAX_VALUE - 1;
		//		}
		//		System.out.println("Array size " + maxSizeTrie);
		//		MotifTrie trie = new MotifTrie(maxSizeTrie);

		System.out.println("Time to create the graph " + (System.currentTimeMillis() - time + "ms"));
		time = System.currentTimeMillis();

		subgraphsCount = 0;
		//		Node.sortByColorId(graph);
		//		Color colorOld = null;
		//		int totalLeafs = 0;
		Node[] motifPrefix = new Node[k];
		for (Node node : graph) {
			if (node.isValid()) {
				//				if (colorOld != node.getColor()) {
				//					totalLeafs += trie.totalLeafs;
				//					//					System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
				//					trie.clear();
				//					colorOld = node.getColor();
				//				}
				node.setInvalid();
				motifPrefix[0] = node;
				createSubgraph(motifPrefix, 1);
			}
		}

		//		totalLeafs += trie.totalLeafs;
		//		System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
		//		System.out.println();
		System.out.println("Time to calculate subGraphs " + (System.currentTimeMillis() - time) + "ms");
		System.out.println("Total subgraphs of size " + k + ": " + subgraphsCount);
		//		System.out.println("Total motifs of size " + k + ": " + totalLeafs);
		System.out.println("Nodes: " + graph.size());
		System.out.println("Colors :" + lastColorId);
		System.out.println("Edges :" + edgesQtty);
		//		if (printDetails) {
		//			for (Color color : colors) {
		//				System.out.println(color.getDescription() + "\t" + color.getNumNodes());
		//			}
		//			System.out.println("Motifs: ");
		//			trie.print(System.out, colors, k, n, motCount);
		//		System.out.println("Occurrences:");
		//		int repeats[] = trie.repeats;
		//		for (int j = 0; j < repeats.length; j++) {
		//			if (repeats[j] != 0)
		//				System.out.println((j + 1) + " " + repeats[j]);
		//		}
		//		}

		//			System.out.println("motifs leaves " + TrieLeafMotifShort.counterLeafs);
		//			System.out.println("motifs internal nodes " + TrieInternalNodeMotifShort.counterInternalNodes);
		//			int repeats[] = TrieLeafMotifShort.repeats;
		System.out.println();

	}

	private static void createMotif(Node[] motifPrefix, int k1, MotifTrie trie) {
		if (k1 == motifPrefix.length) {
			short[] motif = new short[motifPrefix.length];
			for (int i = 0; i < motifPrefix.length; i++) {
				motif[i] = motifPrefix[i].getColor().getId();
			}
			Arrays.sort(motif);
			subgraphsCount++;
			trie.addMotif(motif);
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
