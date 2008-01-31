package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Reaction
{

	String			ID;
	String			color;
	List<Reaction>	linkedTo	= new ArrayList<Reaction>();
	List<MotifNode>	nodes		= new ArrayList<MotifNode>();

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

	public void addNode(MotifNode node)
	{
		nodes.add(node);
	}

	public boolean isInTree()
	{
		return nodes.size() == 0;
	}

}
