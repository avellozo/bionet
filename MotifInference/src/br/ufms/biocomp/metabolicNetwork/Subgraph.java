package br.ufms.biocomp.metabolicNetwork;

public class Subgraph
{

	Reaction	reaction;
	short		size;
	Subgraph	parent;

	public Subgraph(Reaction reaction, Subgraph parent)
	{
		this.reaction = reaction;
		this.parent = parent;

		if (parent == null)
			size = 0;
		else
			size = (short) (parent.size + 1);
	}

}
