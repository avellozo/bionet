/*
 * Created on 11/06/2008
 */
package baobab.sequence.general;

import java.util.Set;

import org.biojavax.RichObjectFactory;
import org.biojavax.ontology.ComparableOntology;
import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.SimpleComparableOntology;

public class TermsAndOntologies extends BioSql
{
	//	public static SimpleComparableOntology	ONTOLOGY_GENERAL		= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("ontologyGeneral")});
	//
	//	public static SimpleComparableOntology	ONT_FEATURES			= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("ontologyFeatures")});
	//	public static ComparableTerm			TERM_GENE				= ONT_FEATURES.getOrCreateTerm(Messages.getString("termGene"));
	//	public static ComparableTerm			TERM_EST				= ONT_FEATURES.getOrCreateTerm(Messages.getString("termEst"));
	//
	//	public static SimpleComparableOntology	KO_ONTOLOGY				= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("KO.ontology")});
	//
	//	public static SimpleComparableOntology	ONTOLOGY2LINKS_KO_TO_EC	= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("KO.ontologyToEc")});
	//	public static SimpleComparableOntology	ONT_LINK_GENE2KO		= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("Gene.ontologyToKo")});
	//	//triple predicate to associate KO to EC
	//	public static ComparableTerm			TERM2LINK_KO_TO_EC		= ONTOLOGY_GENERAL.getOrCreateTerm(Messages.getString("KO.termToEc"));
	//
	//	public static SimpleComparableOntology	ONTOLOGY2LINKS_KO_TO_GO	= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("KO.ontologyToGo")});
	//	//triple predicate to associate KO to GO
	//	public static ComparableTerm			TERM2LINK_KO_TO_GO		= ONTOLOGY_GENERAL.getOrCreateTerm(Messages.getString("KO.termToGo"));
	//
	//	public static SimpleComparableOntology	GO_ONTOLOGY				= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("GO.ontology")});
	//
	//	public static SimpleComparableOntology	EC_ONTOLOGY				= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																		SimpleComparableOntology.class,
	//																		new Object[] {Messages.getString("EC.ontology")});
	//
	public static SimpleComparableOntology getOntologyGeneral() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ontologyGeneral")});
	}

	public static SimpleComparableOntology getOntologyFeatures() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ontologyFeatures")});
	}

	public static SimpleComparableOntology getOntologyKO() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("KO.ontology")});
	}

	public static SimpleComparableOntology getOntologyToLinksKOToEC() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("KO.ontologyToEc")});
	}

	public static SimpleComparableOntology getOntologyToLinkGeneToKO() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("Gene.ontologyToKo")});
	}

	public static SimpleComparableOntology getOntologyToLinkORFToKO() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ORF.ontologyToKo")});
	}

	public static SimpleComparableOntology getOntologyMRNAToORF() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ontologyMRNAToORF")});
	}

	public static SimpleComparableOntology getOntologyToLinksKOToGO() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("KO.ontologyToGo")});
	}

	public static SimpleComparableOntology getOntologyGO() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("GO.ontology")});
	}

	public static SimpleComparableOntology getOntologyEC() {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("EC.ontology")});
	}

	public static ComparableOntology getCompilationOnt(Organism org) {
		return (SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("Compilation.ontologyPrefix") + org.getTaxon().getNCBITaxID()});
	}

	public static ComparableTerm getTermToLinkKOToEC() {
		return getOntologyGeneral().getOrCreateTerm(Messages.getString("KO.termToEc"));
	}

	public static ComparableTerm getTermToLinkKOToGO() {
		return getOntologyGeneral().getOrCreateTerm(Messages.getString("KO.termToGo"));
	}

	public static ComparableTerm getTermGene() {
		return getOntologyFeatures().getOrCreateTerm(Messages.getString("termGene"));
	}

	public static ComparableTerm getTermEST() {
		return getOntologyFeatures().getOrCreateTerm(Messages.getString("termEst"));
	}

	public static ComparableTerm getTermMRNA() {
		return getOntologyFeatures().getOrCreateTerm(Messages.getString("termMRNA"));
	}

	public static ComparableTerm getTermORF() {
		return getOntologyFeatures().getOrCreateTerm(Messages.getString("termORF"));
	}

	public static ComparableTerm getTermVR() {
		return getOntologyMRNAToORF().getOrCreateTerm(Messages.getString("termVR"));
	}

	public static ComparableTerm getLastCompilationTerm(Organism org) {
		ComparableOntology ont = getCompilationOnt(org);
		Set<ComparableTerm> terms = ont.getTermSet();
		double version = Double.NEGATIVE_INFINITY;
		ComparableTerm res = null;
		for (ComparableTerm term : terms) {
			if (new Double(term.getDescription()) > version) {
				version = new Double(term.getDescription());
				res = term;
			}
		}
		return res;
	}

	public static Set<ComparableTerm> getAllKosTerms() {
		return ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("KO.ontology")})).getTerms();
	}

	public static ComparableTerm getMethodCompTerm() {
		return getOntologyGeneral().getOrCreateTerm(Messages.getString("termMethodComp"));
	}

}
