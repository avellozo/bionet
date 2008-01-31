package br.ufms.biocomp.metabolicNetwork;

public class Motif
{

	Reaction	reaction;
	short		size;
	Motif	parent;

	public Motif(Reaction reaction, Motif parent)
	{
		this.reaction = reaction;
		this.parent = parent;

		if (parent == null)
			size = 0;
		else
			size = (short) (parent.size + 1);
	}

}
