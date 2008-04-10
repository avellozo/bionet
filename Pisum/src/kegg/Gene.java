/*
 * Created on 08/04/2008
 */
package kegg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.Predicate;

public class Gene
{
	String		id;
	String		name;
	Organism	org;

	byte[]		sequence;

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

	public byte[] getSequence()
	{
		return sequence;
	}

	public void setSequence(byte[] sequence)
	{
		this.sequence = sequence;
	}

	public static Gene getOrCreate(final String geneId, Organism org, ObjectContainer db)
	{
		List<Gene> genes = db.query(new Predicate<Gene>()
		{
			public boolean match(Gene gene)
			{
				return gene.getId().equals(geneId);
			}
		});
		if (genes.size() == 0)
		{
			Gene gene = new Gene(geneId, org);
			db.set(gene);
			return gene;
		}
		else if (genes.size() == 1)
		{
			return genes.get(0);
		}
		else
		{
			throw new RuntimeException("More than one Gene with the same ID.");
		}
	}

	public static EstFile loadFromFastaFile(File fileIn) throws IOException, ServiceException
	{
		ArrayList<Est> ests = new ArrayList<Est>();
		KOCollection kos = new KOCollection();
		Est est;
		KO ko;

		FileReader file = null;
		file = new FileReader(fileIn);
		BufferedReader buffer = new BufferedReader(file);

		// Each line of the file contains one relation between Est and KO
		String line = buffer.readLine();
		String[] estkoStr;
		String koId;
		while (line != null)
		{
			estkoStr = line.split("\t");
			est = new Est(estkoStr[0]);
			if (estkoStr.length > 1)
			{
				koId = estkoStr[1];
				ko = kos.getKo(koId);
				if (ko == null)
				{
					ko = new KO(koId);
					kos.add(ko);
				}
				est.setKo(ko);
			}
			ests.add(est);
			line = buffer.readLine();
		}
		file.close();
		kos.updateFromKegg();
		return new EstFile(ests, fileIn.getName());
	}
}
