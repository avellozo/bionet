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
		int posChild = findChild(data);
		if (posChild >= 0)
		{
			node = children.get(posChild);
			node.setTerminal(terminal);
		}
		else
		{
			node = createTrieNodeChild(data, terminal);
			children.add(posChild, node);
		}
		return node;
	}

	protected abstract TrieNode<O> createTrieNodeChild(O data, boolean terminal);

	protected Collection<TrieNode<O>> createChildren()
	{
		return (children = new ArrayList<TrieNode<O>>());
	}

	public TrieNode<O> getChild(O data)
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
			cmp = midVal.getData().compareTo(data);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return midVal; // data found
		}
		return null; // data not found.
	}

	public int findChild(O data)
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
			cmp = midVal.getData().compareTo(data);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // data found
		}
		return -(low + 1); // data not found.
	}

	public Collection<TrieNode<O>> getChildren()
	{
		return children;
	}

	public int compareTo(TrieNode<O> node)
	{
		return getData().compareTo(node.getData());
	}

	public TrieNode<O> removeChild(O data)
	{
		return children.remove(findChild(data));
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
				removeChild(child.getData());
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
