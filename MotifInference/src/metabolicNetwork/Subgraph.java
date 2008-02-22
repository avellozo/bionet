package metabolicNetwork;

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
		{
			if (reaction.compareTo(parent.reaction) <= 0)
			{
				throw new RuntimeException("Reaction is less than parent's reaction.");
			}
			size = (short) (parent.size + 1);
		}
	}

	protected void delete()
	{

	}
	
	public String toString()
	{
		String str = reaction.ID;
		if (parent!=null)
		{
			str = parent.toString() + " " + str;
		}
		return str;
	}

}
