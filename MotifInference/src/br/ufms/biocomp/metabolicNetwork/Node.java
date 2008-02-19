/*
 * Created on 31/01/2008
 */
package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Node extends Subgraph implements Comparable<Node>
{
	List<Node>			children	= null;
	//	Reaction	lastVisitedFor	= null;
	boolean				connected;

	static int			targetMotifSize;
	static MotifList	TargetMotifs;

	public Node(Reaction reaction, Node parent, boolean connected)
	{
		super(reaction, parent);
		this.connected = connected;
		if (reaction != null)
		{
			reaction.addNode(this);
		}
		if (parent != null)
		{
			parent.addChild(this);
		}
	}

	/*	public void insertInSubTree(Node n)
		{
			int compare = this.reaction.compareTo(n.reaction);
			if (compare == 0)
			{
				if (n.connected)
				{
					this.connected = true;
				}
				for (Node n1 : n.children)
				{
					insertInSubTree(n1);
				}
			}
			else if (compare < 0)
			{
				Node childN = getChild(n.reaction);
				if (childN != null)
				{
					childN.insertInSubTree(n);
				}
				else
				{
					Node nClone = addChild(n.reaction, n.connected);
					if (nClone != null) //don't reach targetMotifSize
					{
						for (Node child : children)
						{
							if (n.reaction.compareTo(child.reaction) >= 0)
								child.insertInSubTree(n);
							else
								nClone.insertInSubTree(child, conn);
						}
					}
				}
			}
		}
	*/

	/*	public Node getChild(Reaction r)
		{
			for (Node child : children)
			{
				if (child.reaction == r)
				{
					return child;
				}
			}
			return null;
		}
	*/

	public Node addChild(Reaction r, boolean connected)
	{
		if (size == targetMotifSize - 1)
		{
			if (connected)
			{
				Subgraph motif = new Subgraph(r, this);
				TargetMotifs.add(motif);
			}
			return null;
		}
		else
		{
			Node node = new Node(r, this, connected);
			addChild(node);
			return node;
		}

	}

	//only stablish one new realtion parent-child, but don't change the height (or size)
	public void addChild(Node child)
	{
		if (child.parent != null)
		{
			((Node) child.parent).removeChild(child);
		}
		child.parent = this;
		if (children == null)
			children = new ArrayList<Node>();
		children.add(child);
	}

	public void removeChild(Node child)
	{
		children.remove(child);
		child.parent = null;
	}

	public static Node cartesian(Node n1, Node n2)
	{
		int comp = n1.compareTo(n2);
		Node nRes;

		if (comp == 0) //n1 == n2
		{
			nRes = new Node(n1.reaction, null, n1.connected && n2.connected);
			for (Node n1f : n1.children)
			{
				for (Node n2f : n2.children)
				{
					nRes.addTree(cartesian(n1f, n2f));
				}
			}
			if (n1.connected)
			{
				for (Node n2f : n2.children)
				{
					nRes.addTree(n2f.cloneTree());
				}
			}
			if (n2.connected)
			{
				for (Node n1f : n1.children)
				{
					nRes.addTree(n1f.cloneTree());
				}
			}
		}
		else if (comp < 0) //n1 < n2
		{
			nRes = new Node(n1.reaction, null, false);
			for (Node n1f : n1.children)
			{
				nRes.addTree(cartesian(n1f, n2));
			}
			if (n1.connected)
			{
				nRes.addTree(n2.cloneTree());
			}
		}
		else
		//n2 < n1
		{
			nRes = new Node(n2.reaction, null, false);
			for (Node n2f : n2.children)
			{
				nRes.addTree(cartesian(n2f, n1));
			}
			if (n2.connected)
			{
				nRes.addTree(n1.cloneTree());
			}
		}
		return nRes;
	}

	public Node addTree(Node node)
	{
		int comp = this.compareTo(node);
		if (comp > 0) // this < node
		{
			throw new RuntimeException();
		}
		else if (comp < 0) //this < node
		{
			Node nEqual = null;
			for (Node nf : this.children)
			{
				if (node.compareTo(nf) == 0)
				{
					nEqual = nf;
					break;
				}
			}
			if (nEqual == null)
			{
				this.addChild(node);
				return node;
			}
			else
			{
				return nEqual.addTree(node);
			}
		}
		else
		//this == node
		{
			if (node.connected)
			{
				this.connected = true;
			}
			for (Node nf : node.children)
			{
				this.addTree(nf);
			}
			node.delete();
			return this;
		}
	}

	protected void delete()
	{
		for (Node nf : this.children)
		{
			nf.parent = null;
		}
		children = null;
		if (this.parent != null)
		{
			((Node) this.parent).removeChild(this);
		}
		super.delete();
	}

	public Node cloneTree()
	{
		Node nRes = new Node(this.reaction, null, this.connected);
		for (Node nf : this.children)
		{
			nRes.addChild(nf.cloneTree());
		}
		return nRes;
	}

	@Override
	public int compareTo(Node node)
	{
		return reaction.compareTo(node.reaction);
	}
}
