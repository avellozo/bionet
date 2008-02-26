/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

import java.util.ArrayList;
import java.util.Collection;

public abstract class TrieNodeChildrenSorted<O extends Comparable<O>> extends TrieNode<O>
{
	ArrayList<TrieNode<O>>	children;

	public TrieNodeChildrenSorted(O data)
	{
		super(data);
		createChildren();
	}

	public TrieNode<O> addChild(O data, boolean terminal)
	{
		TrieNode<O> node;
		int posChild = findChild(data, terminal);
		if (posChild >= 0)
		{
			node = children.get(posChild);
		}
		else
		{
			node = createTrieNodeChild(data, terminal);
			children.add(posChild, node);
		}
		return node;
	}

	public int findChild(O data, boolean terminal)
	{
		int low = 0;
		int high = children.size() - 1;
		int mid;
		int cmp;
		TrieNode<O> midVal;

		while (low <= high)
		{
			mid = (low + high) >> 1;
			midVal = children.get(mid);
			cmp = midVal.compareTo(data, terminal);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // data found
		}
		return -(low + 1); // data not found.
	}

	protected abstract TrieNode<O> createTrieNodeChild(O data, boolean terminal);

	protected Collection<TrieNode<O>> createChildren()
	{
		return (children = new ArrayList<TrieNode<O>>());
	}

	@Override
	public TrieNode<O> getChild(O data, boolean terminal)
	{
		int posChild = findChild(data, terminal);
		if (posChild >= 0)
		{
			return children.get(posChild);
		}
		else
		{
			return null;
		}
	}

	public Collection<TrieNode<O>> getChildren()
	{
		return children;
	}

	public int compareTo(O data, boolean terminal)
	{
		int ret = getData().compareTo(data);
		if (ret == 0 && (isTerminal() != terminal))
		{
			if (terminal)
			{
				ret = -1; //fisrt non terminal
			}
			else
			{
				ret = +1;
			}
		}
		return ret;
	}

	public TrieNode<O> removeChild(O data, boolean terminal)
	{
		return children.remove(findChild(data, terminal));
	}

	public void deleteTree()
	{
		createChildren();
	}

	public void shrink()
	{
		ArrayList<TrieNode<O>> children1 = new ArrayList<TrieNode<O>>(children);
		TrieNode<O> child;
		for (int i = 0; i < children1.size(); i++)
		{
			child = children1.get(i);
			child.shrink();
			if (!child.isTerminal() && child.getChildren().size() == 0)
			{
				removeChild(child.getData(), false);
			}
		}
		children.trimToSize();
	}

	@Override
	public int getNumberOfChildren()
	{
		return children.size();
	}

}
