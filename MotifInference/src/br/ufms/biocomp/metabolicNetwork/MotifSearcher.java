package br.ufms.biocomp.metabolicNetwork;

public class MotifSearcher
{

	//HashMap<String,Reaction> reactions = new HashMap<String, Reaction>();

	public static void main(String[] args)
	{
		ReactionNetwork network = new ReactionNetwork();
		//network.buildFromSifFile("T:\\Trabalho em Lyon\\MotifInference\\Examples\\reaction_graph_motus_coli.sif");
		//network.loadColorsFrom("T:\\Trabalho em Lyon\\MotifInference\\Examples\\primCpdsSmmReactionsCompounds.col", 3);

		network.buildFromSifFile(args[1]);
		network.loadColorsFrom(args[2], Integer.parseInt(args[3]));
		network.eraseVerticesWithoutColor();
		// network.print();

		System.out.println("Number of Colors: " + network.numberOfColors());

		// Build the tree of the motif seeds, searching for motifs with size k = args[4]
		MotifList motifList = buildMotifList(network, Integer.parseInt(args[4]));

	}

	public static MotifList buildMotifList(ReactionNetwork network, int k)
	{
		MotifList motifList = null;
		boolean neighbourInTree;
		Node root = null;

		for (Reaction reaction : network.reactions.values())
		{
			neighbourInTree = false;
			for (Reaction reactionLinked : reaction.linkedTo)
			{
				if (reactionLinked.isInTree())
				{
					neighbourInTree = true;
					for (Node node : reactionLinked.nodes)
					{
						node.createMotifs(reaction, k, motifList);
					}
				}
				else
				{
					break;
				}
			}

			if (!neighbourInTree)
			{
				// Clean old tree
				if (root != null)
				{
					for (Node node : root.children)
					{
						node.reaction.nodes = null;
					}
				}
				// start new tree
				root = new Node(null, null);
			}
			root.createChild(reaction);
		}
		return motifList;
	}

}
