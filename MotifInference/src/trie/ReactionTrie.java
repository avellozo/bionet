package trie;

import java.util.ArrayList;
import java.util.List;


public class ReactionTrie implements Comparable<ReactionTrie>
{

	static short	nextOrderCreated	= 0;
	String			ID;
	String			color;
	List<ReactionTrie>	linkedTo			= new ArrayList<ReactionTrie>();
	List<Node>		nodes				= new ArrayList<Node>();
	short			orderCreated;
	Node			subgraphsTree;
	String			ECNumber;

	public Node getSubgraphsTree()
	{
		return subgraphsTree;
	}

	public void setSubgraphsTree(Node subgraphsTree)
	{
		this.subgraphsTree = subgraphsTree;
	}

	public ReactionTrie(String ID)
	{
		this.ID = ID;
		orderCreated = nextOrderCreated++;
	}

	public void addReactionLink(ReactionTrie r)
	{
		if (!linkedTo.contains(r))
		{
			linkedTo.add(r);
			r.addReactionLink(this);
		}
	}

	public void addNode(Node node)
	{
		nodes.add(node);
	}

	public void removeNode(Node node)
	{
		nodes.remove(node);
	}

	public boolean isInTree()
	{
		return nodes.size() != 0;
	}

	public int compareTo(ReactionTrie o)
	{
		return orderCreated - o.orderCreated;
	}

	public String toString()
	{
		return ID;
	}
}
