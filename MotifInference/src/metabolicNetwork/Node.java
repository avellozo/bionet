/*
 * Created on 31/01/2008
 */
package metabolicNetwork;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Node extends Subgraph implements Comparable<Node>
{
	List<Node>			children	= null;
	//	Reaction	lastVisitedFor	= null;
	boolean				connected;
	short		height;

	static int			targetMotifSize;
	static MotifList	TargetMotifs;

	private Node(Reaction reaction, Node parent, boolean connected)
	{
		super(reaction, parent);
		if (parent == null)
			height = 0;
		else
		{
//			if (reaction.compareTo(parent.reaction) <= 0)
//			{
//				throw new RuntimeException("Reaction is less than parent's reaction.");
//			}
			height = (short) (parent.height + 1);
			if (parent.children == null)
			{
				parent.createChildrenList();
			}
			parent.children.add(this);
		}
		this.connected = connected;
//		if (reaction == null)
//		{
//			throw new RuntimeException();
//		}
		reaction.addNode(this);
	}

	public Node(Reaction reaction, boolean connected)
	{
		this(reaction, null, connected);
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

	public Node getChild(Reaction r)
	{
		if (children != null)
		{
			for (Node child : children)
			{
				if (child.reaction == r)
				{
					return child;
				}
			}
		}
		return null;
	}

	public Node createChild(Reaction r, boolean connected)
	{
		if (height < targetMotifSize - 1)
		{
			return new Node(r, this, connected);
		}
		else if ((height == targetMotifSize - 1) && connected)
		{
			TargetMotifs.add(new Subgraph(r, this));
			return null;
		}
		else
		{
			return null;
		}
	}

	// Se o nó encontrado é terminal então despreza o parâmetro connected
	public Node getOrCreateUniqueChild(Reaction r, boolean connected)
	{
		Node nRes;
		if (reaction == r)
		{
			nRes = this;
		}
		else
		{
			nRes = getChild(r);
		}
		if (nRes == null)
		{
			nRes = createChild(r, connected);
		}
		else if (!nRes.connected)
		{
			nRes.connected = connected;
		}
		//		if (nRes != null && nRes.size >= targetMotifSize)
		//		{
		//			nRes = null;
		//		}
		return nRes;
	}

	private void createChildrenList()
	{
		children = new ArrayList<Node>(6);
	}

	public Node addCartesianTree(Node node)
	{
		return addCartesianTree1(this, node);
	}

	// faz o produto cartesiano de n1 e n2 e coloca a árvore gerada como filha deste nó
	public Node addCartesianTree(Node n1, Node n2)
	{
		int comp = n1.compareTo(n2);
		Node nNextParent;
		List<Node> childrenOld;
		List<Node> children1Old;
		List<Node> children2Old;

		if (comp == 0) //n1 == n2
		{
			nNextParent = this.getOrCreateUniqueChild(n1.reaction, n1.connected && n2.connected);
			if (nNextParent != null && nNextParent.height < targetMotifSize)
			{
				if (n1.children != null && n2.children != null)
				{
					children1Old = new ArrayList<Node>(n1.children);
					for (Node n1f : children1Old)
					{
						children2Old = new ArrayList<Node>(n2.children);
						for (Node n2f : children2Old)
						{
							nNextParent.addCartesianTree(n1f, n2f);
						}
					}
				}
				if (n2.children != null && n1.connected && nNextParent != n2)
				{
					children2Old = new ArrayList<Node>(n2.children);
					for (Node n2f : children2Old)
					{
						nNextParent.addNewTree(n2f);
					}
				}
				if (n1.children != null && n2.connected && nNextParent != n1)
				{
					children1Old = new ArrayList<Node>(n1.children);
					for (Node n1f : children1Old)
					{
						nNextParent.addNewTree(n1f);
					}
				}
			}
		}
		else if (comp < 0) //n1 < n2
		{
			nNextParent = this.getOrCreateUniqueChild(n1.reaction, false);
			if (nNextParent != null)
			{
				if (n1.children != null)
				{
					childrenOld = new ArrayList<Node>(n1.children);
					for (Node n1f : childrenOld)
					{
						nNextParent.addCartesianTree(n1f, n2);
					}
				}
				if (n1.connected)
				{
					nNextParent.addNewTree(n2);
				}
			}
		}
		else
		//n2 < n1
		{
			nNextParent = this.getOrCreateUniqueChild(n2.reaction, false);
			if (nNextParent != null)
			{
				if (n2.children != null)
				{
					childrenOld = new ArrayList<Node>(n2.children);
					for (Node n2f : childrenOld)
					{
						nNextParent.addCartesianTree(n2f, n1);
					}
				}
				if (n2.connected)
				{
					nNextParent.addNewTree(n1);
				}
			}
		}
		return nNextParent;
	}

	public Node addCartesianTree1(Node n1, Node n2) //code clean
	{
		List<Node> childrenOld;
		int comp = n1.compareTo(n2);
		if (comp > 0)
		{
			Node aux = n1;
			n1 = n2;
			n2 = aux;
		}
		//n1 <= n2
		Node nNextParent = this.getOrCreateUniqueChild(n1.reaction, comp == 0 && n1.connected && n2.connected);
		if (nNextParent != null && nNextParent.height < targetMotifSize)
		{
			if (n1.children != null)
			{
				childrenOld = new ArrayList<Node>(n1.children);
				for (Node n1f : childrenOld)
				{
					nNextParent.addCartesianTree1(n1f, n2);
				}
			}
			if (n1.connected)
			{
				nNextParent.addNewTree(n2);
			}
		}
		return nNextParent;
	}

	public Node addNewTree(Node nodeTree)
	{
		if (this == nodeTree)
		{
			return this;
		}
		Node nRes = getOrCreateUniqueChild(nodeTree.reaction, nodeTree.connected);
		if (nRes != null && nodeTree.children != null)
		{
			for (Node nf : nodeTree.children)
			{
				nRes.addNewTree(nf);
			}
		}
		return nRes;
	}

	public Node getParentRoot()
	{
		Node nRes = this;
		while (nRes.parent != null)
		{
			nRes = (Node) nRes.parent;
		}
		return nRes;
	}

	public Node addNewSubgAndTree(Node occurrence)
	{
		Node nRes = addNewSubg(occurrence, occurrence.connected);
		if (nRes != null && occurrence.children != null)
		{
			for (Node nf : occurrence.children)
			{
				nRes.addNewTree(nf);
			}
		}
		return nRes;
	}

	public Node addNewSubg(Node subg, boolean connected)
	{
		Node nRes;
		if (subg.parent == null)
		{
			nRes = getOrCreateUniqueChild(subg.reaction, connected);
		}
		else
		{
			nRes = addNewSubg((Node) subg.parent, false);
			if (nRes != null)
			{
				nRes = nRes.getOrCreateUniqueChild(subg.reaction, connected);
			}
		}
		return nRes;
	}

	/*	public Node addTree(Node node)
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
	*/

	/*	//clona a subárvore a partir deste nó e sopbrepõe em parent
		public Node cloneTree(Node parent)
		{
			Node nRes = null;
			//		Node nRes = parent.getChild
			if (parent == null)
			{
				nRes = new Node(this.reaction, null, this.connected);
				for (Node nf : this.children)
				{
					nRes.addChild(nf.cloneTree());
				}
			}
			else
			{

			}
			return nRes;
		}
	*/

	/*	//only stablish one new realtion parent-child, but don't change the height (or size)
	public void addChild(Node child)
	{
		if (child.parent != null)
		{
			((Node) child.parent).removeChild(child);
		}
		child.parent = this;
		if (children == null)
		{
			createChildrenList();
		}
		children.add(child);
	}
	*/

	public int compareTo(Node node)
	{
		return reaction.compareTo(node.reaction);
	}

	public boolean removeChild(Node child)
	{
		if (children != null && children.remove(child))
		{
			child.parent = null;
			return true;
		}
		return false;
	}

	protected void delete()
	{
		if (this.children != null)
		{
			for (Node nf : this.children)
			{
				nf.parent = null;
			}
			children = null;
		}

		if (this.parent != null)
		{
			((Node) this.parent).removeChild(this);
			parent = null;
		}
		reaction.removeNode(this);
		super.delete();
	}

	protected void deleteTree()
	{
		List<Node> children = new ArrayList<Node>(this.children);
		delete();
		if (children != null)
		{
			for (Node nf : children)
			{
				nf.deleteTree();
			}
		}
	}

	public void shrink()
	{
		if (children != null)
		{
			((ArrayList<Node>) children).trimToSize();
			ArrayList<Node> children1 = new ArrayList<Node>(children);
			for (int i = 0; i < children1.size(); i++)
			{
				children1.get(i).shrink();
			}
		}
		if ((children == null || children.size() == 0) && !connected)
		{
			delete();
		}
	}
	
	public void printTree(PrintStream p)
	{
		p.print(reaction+" ->");
		if (children != null)
		{
		for (Node n : children)
		{
			p.print(" "+n.reaction);
		}
		p.println();
		for (Node n : children)
		{
			n.printTree(p);
		}
		}
		p.println();
	}
	
//	public String toString()
//	{
//		return reaction.toString();
//	}

}
