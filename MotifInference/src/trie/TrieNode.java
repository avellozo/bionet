/*
 * Created on 25/02/2008
 */
package trie;

import java.io.PrintStream;
import java.util.Collection;

public abstract class TrieNode<O> implements Comparable<TrieNode<O>>
{
	O	data;

	public TrieNode(O data)
	{
		this.data = data;
	}

	public O getData()
	{
		return data;
	}

	public void printTree(PrintStream p)
	{
		p.print(data);
		if (getNumberOfChildren() > 0)
		{
			p.print(" ->");
			for (TrieNode<O> n : getChildren())
			{
				p.print(" " + n.getData());
				p.println();
			}
			for (TrieNode<O> n : getChildren())
			{
				n.printTree(p);
			}
		}
		p.println();
	}

	public abstract Collection<TrieNode<O>> getChildren();

	public abstract void setTerminal(boolean terminal);

	public abstract boolean isTerminal();

	public abstract TrieNode<O> addChild(O data, boolean terminal);

	//assumindo que há dois filhos com o mesmo dado
	public abstract TrieNode<O> getChild(O data, boolean terminal);

	//	//assumindo que não há dois filhos com o mesmo dado
	//	public abstract TrieNode<O> getChild(O data);

	public abstract void shrink();

	public abstract TrieNode<O> removeChild(O data, boolean terminal);

	public abstract void deleteTree();

	public abstract int getNumberOfChildren();

	public abstract int compareTo(O data, boolean terminal);

	public int compareTo(TrieNode<O> node)
	{
		return compareTo(node.getData(), node.isTerminal());
	}

}
