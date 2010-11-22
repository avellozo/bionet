/*
 * Created on 25/02/2008
 */
package general;

import java.util.TreeSet;

public class Color implements Comparable<Color>
{
	short			id		= 0;
	TreeSet<Node>	nodes	= new TreeSet<Node>();
	String			description;
	Double			f		= null;

	public Color(short id, String description) {
		this.id = id;
		this.description = description;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public int getNumNodes() {
		return nodes.size();
	}

	public void addNode(Node node) {
		nodes.add(node);
	}

	public void removeNode(Node node) {
		nodes.remove(node);
	}

	public double getF() {
		if (f == null) {
			f = getNumNodes() * 1.0 / getGraph().getNumberOfNodes();
		}
		return f;
	}

	public Graph getGraph() {
		return nodes.first().getGraph();
	}

	public String toString() {
		return ("" + getDescription());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int compareTo(Color o) {
		return getId() - o.getId();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Color && ((Color) obj).compareTo(this) == 0);
	}

}
