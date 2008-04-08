/*
 * Created on 08/04/2008
 */
package kegg;

public class Gene
{
	String		id;
	String		name;
	Organism	org;

	public Gene(String id, Organism org)
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

	public Organism getOrg()
	{
		return org;
	}

}
