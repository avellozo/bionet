/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import org.biojavax.RichObjectFactory;
import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.ComparableTriple;
import org.biojavax.ontology.SimpleComparableOntology;

import baobab.sequence.general.Messages;

public class KO
{
	ComparableTerm	term;

	public KO(String id) {
		term = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("KO.ontology")})).getOrCreateTerm(id);
	}

	public KO(ComparableTerm term) {
		this.term = term;
	}

	public String getId() {
		return term.getName();
	}

	public ComparableTerm getTerm() {
		return term;
	}

	public ComparableTriple link2Ec(EC ec) {
		SimpleComparableOntology ont = (SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {Messages.getString("KO.ontologyToEc")});
		ComparableTerm term = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ontologyGeneral")})).getOrCreateTerm(Messages.getString("KO.termToEc"));
		return ont.getOrCreateTriple(getTerm(), ec.getTerm(), term);
	}

	public ComparableTriple link2Go(GO go) {
		SimpleComparableOntology ont = (SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {Messages.getString("KO.ontologyToGo")});
		ComparableTerm term = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
			new Object[] {Messages.getString("ontologyGeneral")})).getOrCreateTerm(Messages.getString("KO.termToGo"));
		return ont.getOrCreateTriple(getTerm(), go.getTerm(), term);
	}

	public String getDefinition() {
		return term.getDescription();
	}

	public void setDefinition(String definition) {
		term.setDescription(definition);
	}

}
