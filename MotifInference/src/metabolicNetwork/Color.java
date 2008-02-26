/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class Color implements Comparable<Color>
{
	short	id;

	public Color(short id)
	{
		this.id = id;
	}

	public short getId()
	{
		return id;
	}

	public int compareTo(Color o)
	{
		return id - o.getId();
	}

	public String toString()
	{
		return ("" + getId());
	}
}
