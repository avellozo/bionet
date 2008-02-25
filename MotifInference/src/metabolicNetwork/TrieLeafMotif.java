/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

import java.io.PrintStream;
import java.util.List;

public class TrieLeafMotif extends TrieNode<Color>
{
	int	counter	= 0;

	public TrieLeafMotif(Color color)
	{
		super(color);
	}

	public TrieNode<Color> addChild(Color color, boolean terminal)
	{
		return null;
	}

	public TrieNode<Color> getChild(Color color)
	{
		return null;
	}

	public List<TrieNode<Color>> getChildren()
	{
		return null;
	}

	public Color getData()
	{
		return data;
	}

	public boolean isTerminal()
	{
		return true;
	}

	public Color getColor()
	{
		return data;
	}

	public int getCounter()
	{
		return counter;
	}

	public int incCounter()
	{
		return ++counter;
	}

	public int compareTo(TrieNode<Color> node)
	{
		return data.compareTo(node.getData());
	}

	public void printTree(PrintStream p)
	{
		p.println(data);
	}

	public void shrink()
	{

	}

	public void deleteTree()
	{

	}

	public TrieNode<Color> removeChild(Color color)
	{
		return null;
	}

	@Override
	public int getNumberOfChildren()
	{
		return 0;
	}

	@Override
	public void setTerminal(boolean terminal)
	{
	}

}
