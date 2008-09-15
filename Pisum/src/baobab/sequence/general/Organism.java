/*
 * Created on 06/06/2008
 */
package baobab.sequence.general;

import java.text.MessageFormat;

import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.ComparableOntology;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.exception.DBObjectNotFound;

public class Organism
{
	NCBITaxon	taxon;

	public Organism(NCBITaxon taxon) {
		this.taxon = taxon;
	}

	public Organism(int ncbiTaxonNumber) throws DBObjectNotFound {
		this.taxon = BioSql.getTaxon(ncbiTaxonNumber);
		if (this.taxon == null) {
			throw new DBObjectNotFound(new Object[] {ncbiTaxonNumber});
		}
	}

	//	public Compilation getLastCompilation() {
	//		ComparableTerm term = TermsAndOntologies.getLastCompilationTerm(this);
	//		if (term != null) {
	//			return new Compilation(this, term);
	//		}
	//		else {
	//			return null;
	//		}
	//	}
	//
	//	public Compilation getCompilation(String name) {
	//		ComparableTerm term = (ComparableTerm) TermsAndOntologies.getCompilationOnt(this).getTerm(name);
	//		if (term != null) {
	//			return new Compilation(this, term);
	//		}
	//		return null;
	//	}
	//
	//	public Compilation createCompilation(String name) throws DBObjectAlreadyExists {
	//		ComparableOntology ont = TermsAndOntologies.getCompilationOnt(this);
	//		String version = "1.0";
	//		ComparableTerm term;
	//		if (ont.getDescription() != null && ont.getDescription().length() != 0) {
	//			version = ont.getDescription();
	//		}
	//		try {
	//			term = (ComparableTerm) ont.createTerm(name, version);
	//			ont.setDescription("" + (new Double(version) + 1));
	//		}
	//		catch (AlreadyExistsException e) {
	//			throw new DBObjectAlreadyExists(new Object[] {ont, name});
	//		}
	//		return new Compilation(this, term);
	//	}
	//

	public Compilation getOrCreateCompilation(int version) {
		ComparableOntology ont = TermsAndOntologies.getCompilationOnt(this);
		ComparableTerm term = ont.getOrCreateTerm("" + version);
		Object[] a = {version};
		term.setDescription(MessageFormat.format(Messages.getString("Compilation.description"), a));
		return new Compilation(this, term);
	}

	public String getName() {
		return taxon.getDisplayName();
	}

	public NCBITaxon getTaxon() {
		return taxon;
	}

	public Sequence getSequence(String seqName) {
		return BioSql.getSequence(this, seqName);
	}

}
