/*
 * Created on 08/04/2008
 */
package bionet.general;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.rpc.ServiceException;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class Gene
{
	String		id;
	String		name;
	Compilation	comp;

	byte[]		dnaSequence;

	public Gene(String id, Compilation comp)
	{
		this.id = id;
		this.comp = comp;
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

	public Compilation getCompilation()
	{
		return comp;
	}

	public byte[] getDNASequence()
	{
		return dnaSequence;
	}

	public void setDNASequence(byte[] sequence)
	{
		this.dnaSequence = sequence;
	}

	public void setDNASequence(String sequenceStr)
	{
		int seqSize = sequenceStr.length();
		dnaSequence = new byte[seqSize];
		for (int i = 0; i < seqSize; i++)
		{
			dnaSequence[i] = (byte) sequenceStr.charAt(i);
		}
	}

	public static Gene getOrCreate(final String id, final Compilation comp, ObjectContainer db)
	{
		List<Gene> genes = db.query(new Predicate<Gene>()
		{
			public boolean match(Gene gene)
			{
				return gene.getId().equals(id) && gene.getCompilation() == comp;
			}
		});
		if (genes.size() == 0)
		{
			Gene gene = new Gene(id, comp);
			db.set(gene);
			return gene;
		}
		else if (genes.size() == 1)
		{
			return genes.get(0);
		}
		else
		{
			throw new RuntimeException("More than one Gene with the same ID and compilation.");
		}
	}

	public static Collection<Gene> loadFromFastaFile(File fileIn, Compilation comp, ObjectContainer db)
			throws IOException, ServiceException
	{
		ArrayList<Gene> genes = new ArrayList<Gene>();

		FileReader file = null;
		file = new FileReader(fileIn);
		BufferedReader buffer = new BufferedReader(file);

		// Each line of the file contains one relation between Est and KO
		String line;
		Gene gene = null;
		StringBuffer sequenceStr = null;
		int i = 0;
		while ((line = buffer.readLine()) != null)
		{
			if (line.startsWith(">"))
			{
				if (gene != null && sequenceStr != null)
				{
					gene.setDNASequence(sequenceStr.toString());
					db.set(gene);
				}
				gene = getOrCreate(line.substring(1), comp, db);
				genes.add(gene);
				sequenceStr = null;
			}
			else if (line.length() > 0)// && line.startsWith("[a-zA-Z]"))
			{
				if (sequenceStr == null)
				{
					sequenceStr = new StringBuffer();
				}
				sequenceStr.append(line);
			}
			System.out.println(i++);
		}
		if (gene != null && sequenceStr != null)
		{
			gene.setDNASequence(sequenceStr.toString());
			db.set(gene);
		}
		file.close();
		return genes;
	}

}
