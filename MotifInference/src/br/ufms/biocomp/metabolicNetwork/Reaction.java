package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Reaction implements Comparable<Reaction>
{

	String			ID;
	String			color;
	List<Reaction>	linkedTo	= new ArrayList<Reaction>();
	List<Node>		nodes		= new ArrayList<Node>();
	Integer			orderInGraph;

	public Reaction(String ID)
	{
		this.ID = ID;
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

	public boolean isInTree()
	{
		return nodes.size() == 0;
	}

	@Override
	public int compareTo(Reaction o)
	{
		return orderInGraph.compareTo(o.orderInGraph);
	}

}
