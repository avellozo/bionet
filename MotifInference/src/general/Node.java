package general;

import java.util.Collection;
import java.util.TreeSet;

public class Node
{

	String			ID;
	boolean			valid		= true;
	Color			color		= null;
	TreeSet<Node>	neighbors	= new TreeSet<Node>();

	//	public Node(String ID) {
	//		this.ID = ID;
	//	}
	//
	public Node(String ID, Color color) {
		this.ID = ID;
		setColor(color);
	}

	public String toString() {
		return ID;
	}

	public String getID() {
		return ID;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (getColor() != null) {
			getColor().removeNode(this);
		}
		this.color = color;
		if (getColor() != null) {
			getColor().addNode(this);
		}
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid() {
		//		if (!isValid()) {
		valid = true;
		//			ArrayList<Node> nodesToRemove = new ArrayList<Node>();
		//			for (Node node : neighbors) {
		//				node.addNeighbor(this);
		//				if (!node.isValid()) {
		//					nodesToRemove.add(node);
		//				}
		//			}
		//			for (Node node : nodesToRemove) {
		//				removeNeighbor(node);
		//			}
		//		}
	}

	public void setInvalid() {
		//		for (Node node : neighbors) {
		//			node.removeNeighbor(this);
		//		}
		this.valid = false;
	}

	public boolean addNeighbor(Node node) {
		if (node != this) {
			return neighbors.add(node);
		}
		else {
			return false;
		}
	}

	public boolean removeNeighbor(Node r) {
		return neighbors.remove(r);
	}

	public Collection<Node> getNeighbors() {
		return neighbors;
	}

	public int getDegree() {
		return getNeighbors().size();
	}

}