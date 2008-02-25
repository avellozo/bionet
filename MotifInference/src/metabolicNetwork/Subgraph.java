package metabolicNetwork;

public class Subgraph extends LinkedListReverse<Reaction>
{

	public Subgraph(Subgraph parent, Reaction reaction)
	{
		super(parent, reaction);
	}

	public Reaction getRection()
	{
		return super.getData();
	}

}
