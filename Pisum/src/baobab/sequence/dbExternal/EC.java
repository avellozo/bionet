/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.RichObjectFactory;
import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.SimpleComparableOntology;

import baobab.sequence.general.Messages;

public class EC
{
	String			id;
	ComparableTerm	term;

	public EC(String id) {
		this.id = id;
		term = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("EC.ontology")})).getOrCreateTerm(getId());
	}

	public String getId() {
		return id;
	}

	public ComparableTerm getTerm() {
		return term;
	}

}
