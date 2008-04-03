/*
 * Created on 02/04/2008
 */
package kegg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;
import keggapi.LinkDBRelation;

public class KOCollection
{

	Hashtable<String, KO>	kos	= new Hashtable<String, KO>();	//koId e KO

	public void updateFromKegg(String kosId) throws ServiceException, IOException
	{
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		String tits = serv.btit(kosId);
		LinkDBRelation[] linksEC = serv.get_linkdb_by_entry(kosId, "ENZYME", 1, 100);
		LinkDBRelation[] linksGO = serv.get_linkdb_by_entry(kosId, "GO", 1, 100);
		String tit, koDef, koId, koEc, koGo;
		String[] strs, strs0;
		KO ko;
		BufferedReader readAns = new BufferedReader(new StringReader(tits));
		//update definition of ko
		while ((tit = readAns.readLine()) != null)
		{
			strs = tit.split("; ");
			strs0 = strs[0].split("[:\\s]");
			if (strs0.length > 1)
			{
				koId = strs0[1];
				ko = kos.get(koId);
				if (ko == null)
				{
					ko = new KO(koId);
					kos.put(koId, ko);
				}
			}
			else
			{
				throw new RuntimeException("Without [:\\s] to find ko number: " + strs[0]);
			}
			if (strs.length > 1)
			{
				koDef = strs[1];
				ko.setDefinition(koDef);
			}
			//			else
			//			{
			//				System.out.println("Without definition : " + tit);
			//			}
		}
		for (LinkDBRelation link : linksEC)
		{
			strs = link.getEntry_id1().split(":");
			koId = strs[1];
			ko = kos.get(koId);
			if (ko == null)
			{
				ko = new KO(koId);
				kos.put(koId, ko);
			}
			strs = link.getEntry_id2().split(":");
			//			if (strs.length > 1)
			//			{
			koEc = strs[1];
			ko.setEc(new EC(koEc));
			//			}
		}
		for (LinkDBRelation link : linksGO)
		{
			strs = link.getEntry_id1().split(":");
			koId = strs[1];
			ko = kos.get(koId);
			if (ko == null)
			{
				ko = new KO(koId);
				kos.put(koId, ko);
			}
			strs = link.getEntry_id2().split(":");
			//			if (strs.length > 1)
			//			{
			koGo = strs[1];
			ko.setGo(new GO(koGo));
			//			}
		}
	}

	public void add(KO ko)
	{
		kos.put(ko.getId(), ko);
	}

	public void updateFromKegg() throws ServiceException, IOException
	{
		int i = 0;
		String query = "";
		for (KO ko : kos.values())
		{
			i++;
			query += "ko:" + ko.getId() + " ";
			if (i == 100)
			{
				updateFromKegg(query);
				query = "";
				i = 0;
			}
		}
		if (i > 0)
		{
			updateFromKegg(query);
		}
	}
}
