/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class Color implements Comparable<Color>
{
	String	id;

	public Color(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public int compareTo(Color o)
	{
		return getId().compareTo(o.getId());
	}

	public String toString()
	{
		return id;
	}
}
