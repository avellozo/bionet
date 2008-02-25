package metabolicNetwork;

public class Subgraph
{
	Reaction	reaction;
	Subgraph	parent;

	public Subgraph(Reaction reaction, Subgraph parent)
	{
		this.reaction = reaction;
		this.parent = parent;
	}

	protected void delete()
	{

	}
	
	public String toString()
	{
		String str = reaction.toString();
		if (parent!=null)
		{
			str = parent.toString() + " " + str;
		}
		return str;
	}

}
