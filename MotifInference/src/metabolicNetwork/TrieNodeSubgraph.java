/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class TrieNodeSubgraph extends TrieNodeChildrenSortedUnique<ReactionTrie>
{

	public TrieNodeSubgraph(ReactionTrie data)
	{
		super(data);
	}

	@Override
	protected TrieNode<ReactionTrie> createTrieNodeChild(ReactionTrie data, boolean terminal)
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
