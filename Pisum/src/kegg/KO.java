/*
 * Created on 02/04/2008
 */
package kegg;

import java.util.ArrayList;

public class KO
{
	String				id;

	String				definition;

	GO					go;

	EC					ec;

	ArrayList<KOClass>	koClasses;

	public KO(String id)
	{
		this.id = id;
	}

	public String getDefinition()
	{
		return definition;
	}

	public void setDefinition(String definition)
	{
		this.definition = definition;
	}

	public GO getGo()
	{
		return go;
	}

	public void setGo(GO go)
	{
		this.go = go;
	}

	public EC getEc()
	{
		return ec;
	}

	public void setEc(EC ec)
	{
		this.ec = ec;
	}

	public ArrayList<KOClass> getKoClasses()
	{
		return koClasses;
	}

	public void setKoClasses(ArrayList<KOClass> koClasses)
	{
		this.koClasses = koClasses;
	}

	public String getId()
	{
		return id;
	}
}
