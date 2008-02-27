/*
 * Created on 27/02/2008
 */
package metabolicNetwork;

public class TrieInternalNodeMotifShort implements TrieNodeMotifShort
{
	TrieNodeMotifShort[]	children;
	//	short[]					data		= new short[2];				//data[0] = color and data[1] = number of children

	short					color;

	public TrieInternalNodeMotifShort(short color)
	{
		this.color = color;
	}

	public TrieNodeMotifShort addChild(short color, boolean terminal)
	{
		TrieNodeMotifShort node;
		int posChild = findChild(color);
		if (posChild >= 0)
		{
			node = children[posChild];
			if (terminal)
			{
				((TrieLeafMotifShort) node).incCounter();
			}
		}
		else
		{
			if (terminal)
			{
				node = new TrieLeafMotifShort(color);
			}
			else
			{
				node = new TrieInternalNodeMotifShort(color);
			}
			insertChild(-posChild - 1, node);
			//			if (-posChild >= children.length)
			//			{
			//				children.add(node);
			//			}
			//			else
			//			{
			//				children.add(-posChild, node);
			//			}
		}
		return node;
	}

	public void insertChild(int pos, TrieNodeMotifShort node)
	{
		if (children == null)
		{
			children = new TrieNodeMotifShort[1];
			children[pos] = node;
		}
		else
		{
			TrieNodeMotifShort[] childrenNew = new TrieNodeMotifShort[children.length + 1];
			System.arraycopy(children, 0, childrenNew, 0, pos);
			childrenNew[pos] = node;
			if (pos != children.length)
			{
				System.arraycopy(children, pos, childrenNew, pos + 1, children.length - pos);
			}
			children = childrenNew;
		}
	}

	public int findChild(short color)
	{
		int low = 0;
		int high = getNumberChildren() - 1;
		int mid;
		int cmp;
		TrieNodeMotifShort midVal;

		while (low <= high)
		{
			mid = (low + high) >> 1;
			midVal = children[mid];
			cmp = midVal.getColor() - color;

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // data found
		}
		return -(low + 1); // data not found.
	}

	public short getColor()
	{
		return color;
	}

	public int getNumberChildren()
	{
		if (children == null)
			return 0;
		return children.length;
	}

	//	public TrieInternalNodeMotifShort getChild(short data)
	//	{
	//		int posChild = findChild(data);
	//		if (posChild >= 0)
	//		{
	//			return children.get(posChild);
	//		}
	//		else
	//		{
	//			return null;
	//		}
	//	}
	//
}
