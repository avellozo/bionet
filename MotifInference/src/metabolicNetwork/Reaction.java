package metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Reaction implements Comparable<Reaction>
{

	static short	nextOrderCreated	= 0;
	String			ID;
	String			color;
	List<Reaction>	linkedTo			= new ArrayList<Reaction>();
	List<Node>		nodes				= new ArrayList<Node>();
	short			orderCreated;
	Node			subgraphsTree;

	public Node getSubgraphsTree()
	{
		return subgraphsTree;
	}

	public void setSubgraphsTree(Node subgraphsTree)
	{
		this.subgraphsTree = subgraphsTree;
	}

	public Reaction(String ID)
	{
		this.ID = ID;
		orderCreated = nextOrderCreated++;
	}

	public void addReactionLink(Reaction r)
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

	public int compareTo(Reaction o)
	{
		return orderCreated - o.orderCreated;
	}

}
