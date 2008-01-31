package br.ufms.biocomp.metabolicNetwork;


public class MotifSeed
{

	Reaction	reaction;
	short		height;
	MotifSeed	parent;

	public MotifSeed(Reaction reaction, MotifSeed parent)
	{
		this.reaction = reaction;
		this.parent = parent;

		if (parent == null)
			height = 0;
		else
			height = (short) (parent.height + 1);
	}

}
