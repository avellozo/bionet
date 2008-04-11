/*
 * Created on 02/04/2008
 */
package kegg;

import java.util.List;

import bionet.general.Gene;
import bionet.general.Organism;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class Est extends Gene
{
	public Est(String id, Organism org)
	{
		super(id, org);
	}

	public static Est getOrCreate(final String estId, Organism org, ObjectContainer db)
	{
		List<Est> ests = db.query(new Predicate<Est>()
		{
			public boolean match(Est est)
			{
				return est.getId().equals(estId);
			}
		});
		if (ests.size() == 0)
		{
			Est est = new Est(estId, org);
			db.set(est);
			return est;
		}
		else if (ests.size() == 1)
		{
			return ests.get(0);
		}
		else
		{
			throw new RuntimeException("More than one EST with the same ID.");
		}
	}
}
