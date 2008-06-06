/*
 * Created on 06/06/2008
 */
package baobab.sequence.general;

import java.util.List;

import org.biojavax.bio.taxa.NCBITaxon;
import org.hibernate.Query;
import org.hibernate.Session;

import baobab.sequence.exception.DBObjectNotFound;

public class Organism
{
	NCBITaxon	taxon;

	public Organism(NCBITaxon taxon)
	{
		this.taxon = taxon;
	}

	public Organism(int ncbiTaxonNumber, Session session) throws DBObjectNotFound
	{
		Query taxonsQuery = session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber");
		taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
		List taxons = taxonsQuery.list();
		if (taxons.size() != 1)
		{
			throw new DBObjectNotFound(new Object[] {ncbiTaxonNumber});
		}
		this.taxon = (NCBITaxon) taxons.get(0);
	}

	public String getName()
	{
		return taxon.getDisplayName();
	}

	public NCBITaxon getTaxon()
	{
		return taxon;
	}

}
