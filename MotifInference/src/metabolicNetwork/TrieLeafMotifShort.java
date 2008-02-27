/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class TrieLeafMotifShort implements TrieNodeMotifShort
{
	int			counter			= 0;
	static int	counterLeafs	= 0;
	short		color;

	public TrieLeafMotifShort(short color)
	{
		this.color = color;
		counterLeafs++;
	}

	public TrieInternalNodeMotifShort addChild(short color, boolean terminal)
	{
		return null;
	}

	public short getColor()
	{
		return color;
	}

	//	public int getCounter()
	//	{
	//		return counter;
	//	}
	//
	public int incCounter()
	{
		return ++counter;
	}

	//	public void printTree(PrintStream p)
	//	{
	//		p.println(color);
	//	}
	//
	//	public void shrink()
	//	{
	//
	//	}
	//
	//	public void deleteTree()
	//	{
	//
	//	}
	//
	//	public TrieNode<Color> removeChild(Color color, boolean terminal)
	//	{
	//		return null;
	//	}
	//
	//	public int getNumberOfChildren()
	//	{
	//		return 0;
	//	}
	//
	//	public int compareTo(short color, boolean terminal)
	//	{
	//		return this.color - color;
	//	}

}
