/*
 * Created on 25/02/2008
 */
package trie;

public class LinkedListReverse<O extends Comparable<O>>
{
	LinkedListReverse<O>	parent;
	O						data;

	public LinkedListReverse(LinkedListReverse<O> parent, O data)
	{
		this.parent = parent;
		this.data = data;
	}

	public LinkedListReverse<O> getParent()
	{
		return parent;
	}

	public void setParent(LinkedListReverse<O> parent)
	{
		this.parent = parent;
	}

	public O getData()
	{
		return data;
	}

	public void setData(O data)
	{
		this.data = data;
	}

	public String toString()
	{
		String str = data.toString();
		if (parent != null)
		{
			str = parent.toString() + " " + str;
		}
		return str;
	}

}
