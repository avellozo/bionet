/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class TrieNodeMotif extends TrieNodeChildrenSorted<Color>
{

	public TrieNodeMotif(Color color)
	{
		super(color);
	}

	@Override
	protected TrieNode<Color> createTrieNodeChild(Color data, boolean terminal)
	{
		TrieNode<Color> node;
		if (terminal)
		{
			node = new TrieLeafMotif(data);
		}
		else
		{
			node = new TrieNodeMotif(data);
		}
		return node;
	}

	@Override
	public boolean isTerminal()
	{
		return false;
	}

	@Override
	public void setTerminal(boolean terminal)
	{
		if (terminal)
		{
			throw new RuntimeException();
		}
	}

	@Override
	public TrieNode<Color> addChild(Color data, boolean terminal)
	{
		TrieNode<Color> node;
		int posChild = findChild(data);
		if (posChild >= 0)
		{
			node = children.get(posChild);
			if (terminal)
			{
				if (!node.isTerminal())
				{
					node = new TrieLeafMotif(data);
					children.set(posChild, node);
				}
				else
				{
					((TrieLeafMotif) node).incCounter();
				}
			}
		}
		else
		{
			node = createTrieNodeChild(data, terminal);
			children.add(posChild, node);
		}
		return node;
	}

}
