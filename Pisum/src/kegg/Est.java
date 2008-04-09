/*
 * Created on 02/04/2008
 */
package kegg;

import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class Est
{
	String	id;

	byte[]	sequence;

	public Est(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public byte[] getSequence()
	{
		return sequence;
	}

	public void setSequence(byte[] sequence)
	{
		this.sequence = sequence;
	}

	public static Est getOrCreate(final String estId, ObjectContainer db)
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
			Est est = new Est(estId);
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
