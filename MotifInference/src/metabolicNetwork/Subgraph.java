package metabolicNetwork;

public class Subgraph extends LinkedListReverse<ReactionTrie>
{

	public Subgraph(Subgraph parent, ReactionTrie reaction)
	{
		super(parent, reaction);
	}

	public ReactionTrie getRection()
	{
		return super.getData();
	}

}
