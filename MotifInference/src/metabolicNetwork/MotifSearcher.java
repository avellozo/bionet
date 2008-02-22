package metabolicNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JOptionPane;

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
		//network.buildFromSifFile("reaction_graph_motus_coli.sif");
		//network.loadColorsFrom("primCpdsSmmReactionsCompounds.col", 3);

		network.buildFromSifFile(args[0]);
		network.loadColorsFrom(args[1], Integer.parseInt(args[2]));
		network.eraseVerticesWithoutColor();
		// network.print();
		
		int k;
		
		if (args.length < 4)
		{
			k=Integer.parseInt(JOptionPane.showInputDialog("Please input a 'k' value:"));
		}
		else
		{
			k=Integer.parseInt(args[3]);
		}
		int printSubg;
		if (args.length >4)
		{
			if (args[4].equals("y"))
			{
				printSubg = JOptionPane.YES_OPTION;
			}
			else
			{
				printSubg = JOptionPane.NO_OPTION;
			}
		}
		printSubg = JOptionPane.showConfirmDialog(null, "Show subgraphs?", "Show subgraphs?", JOptionPane.YES_NO_OPTION);

		System.out.println("Number of Colors: " + network.numberOfColors());
		System.out.println("Number of Vertexes: " + network.reactions.size());
		// Build the tree of the motif seeds, searching for motifs with size k = args[4]
		long time = System.currentTimeMillis();
		MotifList motifList = buildMotifList(network, k);
		System.out.println("Execution time: " + (System.currentTimeMillis() - time));
		System.out.println("Number of subgraphs of size " + k + ": " + motifList.size());

		if (printSubg == JOptionPane.YES_OPTION)
		{
			motifList.print(System.out);
		}
	}

	public static MotifList buildMotifList(ReactionNetwork network, int k)
	{
		MotifList motifList = new MotifArrayList();
		Node.TargetMotifs = motifList;
		Node.targetMotifSize = k - 1;
		Node treeRoot = null;
		List<Reaction> reactionsList = sortNetwork(network);
		List<Node> occurrences;
		ArrayList<Node> children;

		for (Reaction reaction : reactionsList)
		{
			treeRoot = new Node(reaction, true);
			reaction.setSubgraphsTree(treeRoot);
			for (Reaction neighbour : reaction.linkedTo)
			{
				if (neighbour.isInTree())
				{
					occurrences = new ArrayList<Node>(neighbour.nodes);
					for (Node occurrence : occurrences)
					{
						if (occurrence.size < Node.targetMotifSize-1 || (occurrence.connected && occurrence.size == Node.targetMotifSize-1))
						{
							treeRoot.addNewSubgAndTree(occurrence);
						}
					}
//					System.out.println("Depois de colocar as ocorrências de "+neighbour+" em "+reaction);
//					treeRoot.printTree(System.out);
//					System.out.println();
				}
			}
			treeRoot.shrink();
			if (treeRoot.children != null)
			{
				children = new ArrayList<Node>(treeRoot.children);
				Collections.sort(children);
				for (int i = children.size()-2; i >=0 ; i--)
				{
					for (int j = i + 1; j < children.size(); j++)
					{
						if (!children.get(i).connected || !children.get(i).reaction.linkedTo.contains(children.get(j).reaction))
						{
						treeRoot.addCartesianTree1(children.get(i), children.get(j));
						}
					}
//					System.out.println("Antes do shrink:");
//					treeRoot.printTree(System.out);
//					System.out.println("Depois do shrink:");
										children.get(i).shrink();
//										treeRoot.printTree(System.out);
				}
			}
			//						if (occurrence.parent == null)
			//						{
			//							treeRoot.addCartesianTree(occurrence);
			//						}
			//						else if (!reaction.linkedTo.contains(occurrence.getParentRoot().reaction))
			//						{
			//							ArrayList<Node> brothers = new ArrayList<Node>(treeRoot.children);
			//							Node newOcurrence = treeRoot.addNewSubgAndTree(occurrence);
			//							if (newOcurrence != null)
			//							{
			//								while (newOcurrence.parent != treeRoot)
			//								{
			//									newOcurrence = (Node) newOcurrence.parent;
			//								}
			//								for (Node brother : brothers)
			//								{
			//									treeRoot.addCartesianTree1(newOcurrence, brother);
			//								}
			//							}
			//						}
			//					}
			//				}
			//			}
//						treeRoot.shrink();

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
		ArrayList<Reaction> reactions = new ArrayList<Reaction>(network.reactions.values().size());
		Reaction r;
		TreeSet<Reaction> reactionSet = new TreeSet<Reaction>(network.reactions.values());
		TreeSet<Reaction> reactionSet1 = new TreeSet<Reaction>(); //já foram analisados, mas os filhos ainda não
		while (!reactionSet.isEmpty())
		{
			r = reactionSet.first();
			reactions.add(r);
			reactionSet.remove(r);
			reactionSet1.add(r);
			while (!reactionSet1.isEmpty())
			{
				r = reactionSet1.first();
				for (Reaction r1 : r.linkedTo)
				{
					if (reactionSet.remove(r1))
					{
						reactionSet1.add(r1);
						reactions.add(r1);
					}
				}
				reactionSet1.remove(r);
			}
		}
		for (int i = 0; i < reactions.size(); i++)
		{
			reactions.get(i).orderCreated = (short) i;
		}
		Collections.sort(reactions, new DescendentComparatorReaction());
		return reactions;
	}

}

class DescendentComparatorReaction implements Comparator<Reaction>
{

	public int compare(Reaction o1, Reaction o2)
	{
		return -o1.compareTo(o2);
	}

}
