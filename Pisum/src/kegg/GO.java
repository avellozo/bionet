/*
 * Created on 02/04/2008
 */
package kegg;

public class GO
{
	String	id;

	public String getId()
	{
		return id;
	}

	public GO(String id)
	{
		this.id = id;
	}

	public String toString()
	{
		return getId();
	}
}
