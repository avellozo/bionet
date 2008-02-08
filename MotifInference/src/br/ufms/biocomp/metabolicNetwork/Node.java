/*
 * Created on 31/01/2008
 */
package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Node extends Subgraph
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

	public void insertInSubTree(Node n)
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

	public Node getChild(Reaction r)
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
	}
}
