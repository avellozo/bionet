/*
 * Created on 27/02/2008
 */
package metabolicNetwork;

public class TrieInternalNodeMotifShort implements TrieNodeMotifShort
{

	static int					gc						= 0;
	static int					counterInternalNodes	= 0;
	//	TrieNodeMotifShort[]	children;
	TrieNodeMotifShort			firstChild;
	//	short[]					data		= new short[2];				//data[0] = color and data[1] = number of children

	short						color;
	private TrieNodeMotifShort	brother;

	//	int							leafsCounter;

	public TrieInternalNodeMotifShort(short color)
	{
		this.color = color;
		counterInternalNodes++;
	}

	public TrieNodeMotifShort getBrother()
	{
		return brother;
	}

	public void setBrother(TrieNodeMotifShort brother)
	{
		this.brother = brother;
	}

	public short getColor()
	{
		return color;
	}

	public TrieNodeMotifShort addChild(short color, boolean terminal)
	{
		TrieNodeMotifShort node = findChild(color);
		if (node != null && node.getColor() == color)
		{
			if (terminal)
			{
				((TrieLeafMotifShort) node).incCounter();
			}
		}
		else
		{
			TrieNodeMotifShort newNode;
			if (terminal)
			{
				newNode = new TrieLeafMotifShort(color);
			}
			else
			{
				newNode = new TrieInternalNodeMotifShort(color);
			}
			//			insertChild(-posChild - 1, node);
			insertChild(newNode, node);
			node = newNode;
		}
		//		leafsCounter++;
		return node;
	}

	private void insertChild(TrieNodeMotifShort newNode, TrieNodeMotifShort node)
	{
		if (node == null)
		{
			newNode.setBrother(firstChild);
			firstChild = newNode;
		}
		else
		{
			newNode.setBrother(node.getBrother());
			node.setBrother(newNode);
		}
	}

	private TrieNodeMotifShort findChild(short color)
	{
		TrieNodeMotifShort node = firstChild;
		if (node == null || node.getColor() > color)
		{
			return null;
		}
		while (node.getBrother() != null && node.getBrother().getColor() <= color)
		{
			node = node.getBrother();
		}
		return node;
	}

	/*	public void insertChild(int pos, TrieNodeMotifShort node)
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
				if (gc == 1000)
				{
					System.gc();
					gc = 0;
				}
				else
				{
					gc++;
				}
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
		 *
		 *
		 */
}
