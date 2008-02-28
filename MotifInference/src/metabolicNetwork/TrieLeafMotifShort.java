/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class TrieLeafMotifShort implements TrieNodeMotifShort
{
	//	private int					counter;
	static int					counterLeafs	= 0;
	static int[]				repeats			= {0};
	//	private short				color;
	private int					colorCounter;						//10 last bits for color
	private TrieNodeMotifShort	brother;

	private static int			flagColor		= 2047;
	private static int			flagCounter		= 0 ^ flagColor;

	public TrieLeafMotifShort(short color)
	{
		setColor(color);
		//		counter = 0;
		repeats[0]++;
		counterLeafs++;
	}

	private void setCounter(int i)
	{
		//		counter = i;
		if (i >= repeats.length)
		{
			int[] newRepeats = new int[repeats.length + 1];
			System.arraycopy(repeats, 0, newRepeats, 0, repeats.length);
			newRepeats[repeats.length] = 0;
			repeats = newRepeats;
		}
		if (i > 0)
		{
			repeats[i - 1]--;
		}
		repeats[i]++;
		i = i << 10;
		colorCounter = ((colorCounter & flagColor) | i);
	}

	public TrieNodeMotifShort getBrother()
	{
		return brother;
	}

	public void setBrother(TrieNodeMotifShort brother)
	{
		this.brother = brother;
	}

	private void setColor(short color)
	{
		colorCounter = ((colorCounter & flagCounter) | color);
		//		this.color = color;
	}

	public TrieInternalNodeMotifShort addChild(short color, boolean terminal)
	{
		return null;
	}

	public short getColor()
	{
		return (short) (colorCounter & flagColor);
	}

	//	public int getCounter()
	//	{
	//		return counter;
	//	}
	//
	public void incCounter()
	{
		setCounter(getCounter() + 1);
	}

	private int getCounter()
	{
		return ((colorCounter & flagCounter) >> 10);
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
