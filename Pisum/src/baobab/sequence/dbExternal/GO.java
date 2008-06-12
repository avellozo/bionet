/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.general.TermsAndOntologies;

public class GO
{
	String			id;
	ComparableTerm	term;

	public GO(String id) {
		this.id = id;
		term = TermsAndOntologies.GO_ONTOLOGY.getOrCreateTerm(getId());
	}

	public String getId() {
		return id;
	}

	public ComparableTerm getTerm() {
		return term;
	}

}
