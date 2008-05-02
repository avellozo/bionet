/*
 * Created on 25/02/2008
 */
package trie;

import java.util.ArrayList;

public abstract class TrieNodeChildrenSortedUnique<O extends Comparable<O>> extends TrieNodeChildrenSorted<O>
{
	public TrieNodeChildrenSortedUnique(O data)
	{
		super(data);
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
			if (-posChild >= children.size())
			{
				children.add(node);
			}
			else
			{
				children.add(-posChild, node);
			}
		}
		return node;
	}

	public TrieNode<O> getChild(O data)
	{
		int posChild = findChild(data);
		if (posChild >= 0)
		{
			return children.get(posChild);
		}
		else
		{
			return null;
		}
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

	public TrieNode<O> removeChild(O data)
	{
		return children.remove(findChild(data));
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

}
