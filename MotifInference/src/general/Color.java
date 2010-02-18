/*
 * Created on 25/02/2008
 */
package general;

import java.util.Arrays;
import java.util.Comparator;

public class Color implements Comparable<Color>
{
	short	id			= 0;
	short	numNodes	= 0;
	String	description;

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

	public short getNumNodes() {
		return numNodes;
	}

	public void incNumNodes() {
		this.numNodes++;
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

	public static void sortByNumNodes(Color[] colors) {
		Arrays.sort(colors, new ComparatorByNumNodes());
	}

	public int compareTo(Color o) {
		return getId() - o.getId();
	}

}

class ComparatorByNumNodes implements Comparator<Color>
{
	public int compare(Color o1, Color o2) {
		return o1.getNumNodes() - o2.getNumNodes();
	}
}
