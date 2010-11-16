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

	public Color(short id, String description) {
		this.id = id;
		this.description = description;
	}

	public Color(String description) {
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

	public String toString() {
		return ("" + getId());
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

}
