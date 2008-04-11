/*
 * Created on 02/04/2008
 */
package kegg;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.rpc.ServiceException;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import bionet.general.EC;
import bionet.general.GO;
import bionet.general.Gene;
import bionet.general.Organism;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class KO
{
	String				id;

	String				definition;

	GO					go;

	EC					ec;

	ArrayList<KOClass>	koClasses;

	ArrayList<Gene>		genes	= null;

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

	public Collection<Gene> getGenes()
	{
		return genes;
	}

	public void loadGenes(Organism[] orgs) throws ServiceException, RemoteException
	{
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		Definition[] genesRes;
		String[] strs;
		genes = new ArrayList<Gene>();
		Gene gene;
		for (Organism org : orgs)
		{
			genesRes = serv.get_genes_by_ko(getId(), org.getId());
			for (Definition def : genesRes)
			{
				strs = def.getEntry_id().split(":");
				gene = new Gene(strs[1], org);
				genes.add(gene);
				strs = def.getDefinition().split(";");
				gene.setName(strs[0]);
			}
		}

	}

	public static KO getOrCreate(final String koId, ObjectContainer db)
	{
		List<KO> kos = db.query(new Predicate<KO>()
		{
			public boolean match(KO ko)
			{
				return ko.getId().equals(koId);
			}
		});
		if (kos.size() == 0)
		{
			KO ko = new KO(koId);
			db.set(ko);
			return ko;
		}
		else if (kos.size() == 1)
		{
			return kos.get(0);
		}
		else
		{
			throw new RuntimeException("More than one KO with the same ID.");
		}
	}

	public static KO get(final String koId, ObjectContainer db)
	{
		List<KO> kos = db.query(new Predicate<KO>()
		{
			public boolean match(KO ko)
			{
				return ko.getId().equals(koId);
			}
		});
		if (kos.size() == 0)
		{
			return null;
		}
		else if (kos.size() == 1)
		{
			return kos.get(0);
		}
		else
		{
			throw new RuntimeException("More than one KO with the same ID.");
		}
	}
}
