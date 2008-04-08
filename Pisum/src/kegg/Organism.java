/*
 * Created on 08/04/2008
 */
package kegg;

public class Organism
{
	String	id;
	String	name;

	public Organism(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
