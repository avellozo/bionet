/*
 * Created on 25/02/2008
 */
package general;

import java.util.Arrays;
import java.util.Comparator;

public class Color
{
	short	id;
	short	numReactions	= 0;
	String	description;

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

	public short getNumReactions() {
		return numReactions;
	}

	public void incNumReactions() {
		this.numReactions++;
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

	public static void sortByNumReactions(Color[] colors) {
		Arrays.sort(colors, new ComparatorByNumReactions());
	}

}

class ComparatorByNumReactions implements Comparator<Color>
{
	public int compare(Color o1, Color o2) {
		return o1.getNumReactions() - o2.getNumReactions();
	}
}
