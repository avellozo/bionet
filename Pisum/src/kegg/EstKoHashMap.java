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

import bionet.general.Organism;

import com.db4o.ObjectContainer;

public class EstKoHashMap extends HashMap<Est, KO> implements EstKoMap
{
	String	mapId;

	public EstKoHashMap(String mapId)
	{
		super();
		this.mapId = mapId;
	}

	public EstKoHashMap(String mapId, int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
		this.mapId = mapId;
	}

	public EstKoHashMap(String mapId, int initialCapacity)
	{
		super(initialCapacity);
		this.mapId = mapId;
	}

	public EstKoHashMap(String mapId, Map< ? extends Est, ? extends KO> m)
	{
		super(m);
		this.mapId = mapId;
	}

	public String getMapId()
	{
		return mapId;
	}

	public static EstKoHashMap loadFromKoFile(File fileIn, Organism org, ObjectContainer db)
			throws IOException, ServiceException
	{
		EstKoHashMap estKos = new EstKoHashMap(fileIn.getName());
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
			est = Est.getOrCreate(estkoStr[0], org, db);
			ko = null;
			if (estkoStr.length > 1)
			{
				koId = estkoStr[1];
				ko = KO.get(koId, db);
				if (ko == null)
				{
					ko = KO.getOrCreate(estkoStr[0], db);
					kos.add(ko);
				}
			}
			estKos.put(est, ko);
			line = buffer.readLine();
		}
		file.close();
		kos.updateFromKegg();
		for (KO ko1 : kos)
		{
			db.set(ko1);
		}
		return estKos;
	}
}
