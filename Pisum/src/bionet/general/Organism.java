/*
 * Created on 08/04/2008
 */
package bionet.general;

import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

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

	public static Organism getOrCreate(final String orgId, ObjectContainer db)
	{
		List<Organism> orgs = db.query(new Predicate<Organism>()
		{
			public boolean match(Organism org)
			{
				return org.getId().equals(orgId);
			}
		});
		if (orgs.size() == 0)
		{
			Organism org = new Organism(orgId);
			db.set(org);
			return org;
		}
		else if (orgs.size() == 1)
		{
			return orgs.get(0);
		}
		else
		{
			throw new RuntimeException("More than one Organism with the same ID.");
		}
	}

}
