/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class TrieNodeSubgraph extends TrieNodeChildrenSortedUnique<Reaction>
{

	public TrieNodeSubgraph(Reaction data)
	{
		super(data);
	}

	@Override
	protected TrieNode<Reaction> createTrieNodeChild(Reaction data, boolean terminal)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTerminal()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTerminal(boolean terminal)
	{
		// TODO Auto-generated method stub

	}

}
