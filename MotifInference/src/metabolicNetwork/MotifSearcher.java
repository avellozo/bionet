package metabolicNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MotifSearcher
{

	//HashMap<String,Reaction> reactions = new HashMap<String, Reaction>();

	public static void main(String[] args)
	//args[0] = sifFile;
	//args[1] = Colors;
	//args[2] = threshold for colors
	//args[3] = k
	{
		ReactionNetwork network = new ReactionNetwork();
		//network.buildFromSifFile("T:\\Trabalho em Lyon\\MotifInference\\Examples\\reaction_graph_motus_coli.sif");
		//network.loadColorsFrom("T:\\Trabalho em Lyon\\MotifInference\\Examples\\primCpdsSmmReactionsCompounds.col", 3);

		network.buildFromSifFile(args[0]);
		network.loadColorsFrom(args[1], Integer.parseInt(args[2]));
		network.eraseVerticesWithoutColor();
		// network.print();

		System.out.println("Number of Colors: " + network.numberOfColors());
		// Build the tree of the motif seeds, searching for motifs with size k = args[4]
		long time = System.currentTimeMillis();
		MotifList motifList = buildMotifList(network, Integer.parseInt(args[3]));
		System.out.println("Execution time: " + (System.currentTimeMillis() - time));
		System.out.println("Number of subgraphs of size k: " + motifList.size());

	}

	public static MotifList buildMotifList(ReactionNetwork network, int k)
	{
		MotifList motifList = new MotifArrayList();
		Node.TargetMotifs = motifList;
		Node.targetMotifSize = k - 1;
		Node treeRoot = null;
		List<Reaction> reactionsList = sortNetwork(network);
		List<Node> ocurrences;

		for (Reaction reaction : reactionsList)
		{
			treeRoot = new Node(reaction, true);
			reaction.setSubgraphsTree(treeRoot);
			for (Reaction neighbour : reaction.linkedTo)
			{
				if (neighbour.isInTree())
				{
					ocurrences = new ArrayList<Node>(neighbour.nodes);
					for (Node ocurrence : ocurrences)
					{
						if (ocurrence.parent == null)
						{
							treeRoot.addCartesianTree(ocurrence);
						}
						else if (!reaction.linkedTo.contains(ocurrence.getParentRoot().reaction))
						{
							treeRoot.addCartesianSubg(ocurrence);
						}
					}
				}
			}

			//			if (!neighbourInTree)
			//			{
			//				// Clean old tree
			//				if (root != null)
			//				{
			//					for (Node node : root.children)
			//					{
			//						node.reaction.nodes = null;
			//					}
			//				}
			//				// start new tree
			//				root = new Node(null, null);
			//			}
			//			root.createChild(reaction);
		}
		return motifList;
	}

	public static List<Reaction> sortNetwork(ReactionNetwork network)
	{
		ArrayList<Reaction> reactionsList = new ArrayList<Reaction>(network.reactions.values());
		Collections.sort(reactionsList, new DescendentComparatorReaction());
		return reactionsList;
	}

}

class DescendentComparatorReaction implements Comparator<Reaction>
{

	@Override
	public int compare(Reaction o1, Reaction o2)
	{
		return -o1.compareTo(o2);
	}

}
