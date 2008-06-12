/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.ComparableTriple;

import baobab.sequence.general.TermsAndOntologies;

public class KO
{
	String			id;
	ComparableTerm	term;

	public KO(String id) {
		this.id = id;
		term = TermsAndOntologies.KO_ONTOLOGY.getOrCreateTerm(getId());
	}

	public String getId() {
		return id;
	}

	public ComparableTerm getTerm() {
		return term;
	}

	public ComparableTriple link2Ec(EC ec) {
		return TermsAndOntologies.ONTOLOGY2LINKS_KO_TO_EC.getOrCreateTriple(getTerm(), ec.getTerm(),
			TermsAndOntologies.TERM2LINK_KO_TO_EC);
	}

	public ComparableTriple link2Go(GO go) {
		return TermsAndOntologies.ONTOLOGY2LINKS_KO_TO_GO.getOrCreateTriple(getTerm(), go.getTerm(),
			TermsAndOntologies.TERM2LINK_KO_TO_GO);
	}

}
