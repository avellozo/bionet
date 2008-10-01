/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.general.TermsAndOntologies;

public class EC implements Comparable<EC>
{
	ComparableTerm	term;

	public EC(String id) {
		term = TermsAndOntologies.getOntologyEC().getOrCreateTerm(id);
	}

	public EC(ComparableTerm term) {
		this.term = term;
	}

	public String getId() {
		return term.getName();
	}

	public ComparableTerm getTerm() {
		return term;
	}

	public int compareTo(EC ec) {
		return term.compareTo(ec.getTerm());
	}

}
