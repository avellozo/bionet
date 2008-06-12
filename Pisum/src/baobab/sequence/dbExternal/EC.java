/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.general.TermsAndOntologies;

public class EC
{
	String			id;
	ComparableTerm	term;

	public EC(String id) {
		this.id = id;
		term = TermsAndOntologies.EC_ONTOLOGY.getOrCreateTerm(getId());
	}

	public String getId() {
		return id;
	}

	public ComparableTerm getTerm() {
		return term;
	}

}
