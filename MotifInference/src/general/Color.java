/*
 * Created on 25/02/2008
 */
package general;

public class Color
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

	public String toString()
	{
		return ("" + getId());
	}
}
