/*
 * Created on 09/04/2008
 */
package kegg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import bionet.general.Gene;
import bionet.general.Organism;

import com.db4o.ObjectContainer;

public class GeneKoHashMap extends HashMap<Gene, KO> implements GeneKoMap
{
	String	mapId;

	public GeneKoHashMap(String mapId)
	{
		super();
		this.mapId = mapId;
	}

	public GeneKoHashMap(String mapId, int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
		this.mapId = mapId;
	}

	public GeneKoHashMap(String mapId, int initialCapacity)
	{
		super(initialCapacity);
		this.mapId = mapId;
	}

	public GeneKoHashMap(String mapId, Map< ? extends Est, ? extends KO> m)
	{
		super(m);
		this.mapId = mapId;
	}

	public String getMapId()
	{
		return mapId;
	}

	public static GeneKoMap loadFromKoFile(File fileIn, Organism org, ObjectContainer db)
			throws IOException, ServiceException
	{
		GeneKoMap geneKos = new GeneKoHashMap(fileIn.getName());
		KOCollection kos = new KOCollection();
		Gene gene;
		KO ko;

		FileReader file = null;
		file = new FileReader(fileIn);
		BufferedReader buffer = new BufferedReader(file);

		// Each line of the file contains one relation between Est and KO
		String line = buffer.readLine();
		String[] genekoStr;
		String koId;
		while (line != null)
		{
			genekoStr = line.split("\t");
			gene = Gene.getOrCreate(genekoStr[0], org, db);
			ko = null;
			if (genekoStr.length > 1)
			{
				koId = genekoStr[1];
				ko = KO.get(koId, db);
				if (ko == null)
				{
					ko = KO.getOrCreate(koId, db);
					kos.add(ko);
				}
			}
			geneKos.put(gene, ko);
			line = buffer.readLine();
		}
		file.close();
		kos.updateFromKegg();
		for (KO ko1 : kos)
		{
			db.set(ko1);
		}
		db.set(geneKos);
		return geneKos;
	}
}
