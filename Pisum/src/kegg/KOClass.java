/*
 * Created on 02/04/2008
 */
package kegg;

public class KOClass
{
	String	id;

	public KOClass(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public String toString()
	{
		return getId();
	}

}