/*
 * Created on 11/04/2008
 */
package bionet.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojavax.bio.seq.RichSequence.IOTools;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class Compilation
{
	Organism	org;
	String		id;
	String		comments;

	public Compilation(String id, Organism org)
	{
		this.org = org;
		this.id = id;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public Organism getOrg()
	{
		return org;
	}

	public String getId()
	{
		return id;
	}

	public static Compilation getOrCreate(final String id, final Organism org, ObjectContainer db)
	{
		List<Compilation> comps = db.query(new Predicate<Compilation>()
		{
			public boolean match(Compilation comp)
			{
				return comp.getId().equals(id) && comp.getOrg() == org;
			}
		});
		if (comps.size() == 0)
		{
			Compilation comp = new Compilation(id, org);
			db.set(comp);
			return comp;
		}
		else if (comps.size() == 1)
		{
			return comps.get(0);
		}
		else
		{
			throw new RuntimeException("More than one Compilation with the same ID and organism.");
		}
	}

	public Sequence loadSequenceFromESTFile(String fileName) throws FileNotFoundException
	{
		SequenceIterator stream = IOTools.readFastaDNA(new BufferedReader(new FileReader(fileName)), null);
		while (stream.hasNext())
		{
			Sequence seq = stream.nextSequence();
			// do something with the sequence.
		}
		return stream;
	}

}
