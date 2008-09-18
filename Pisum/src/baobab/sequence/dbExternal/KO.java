/*
 * Created on 09/06/2008
 */
package baobab.sequence.dbExternal;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.ComparableTriple;
import org.biojavax.ontology.SimpleComparableOntology;

import baobab.sequence.general.TermsAndOntologies;

public class KO
{
	ComparableTerm	term;

	public KO(String id) {
		term = TermsAndOntologies.getOntologyKO().getOrCreateTerm(id);
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
		SimpleComparableOntology ont = TermsAndOntologies.getOntologyToLinksKOToEC();
		ComparableTerm term = TermsAndOntologies.getTermToLinkKOToEC();
		return ont.getOrCreateTriple(getTerm(), ec.getTerm(), term);
	}

	public Collection<EC> getECs() {
		SimpleComparableOntology ont = TermsAndOntologies.getOntologyToLinksKOToEC();
		ComparableTerm term = TermsAndOntologies.getTermToLinkKOToEC();
		Set<ComparableTriple> triples = ont.getTriples(getTerm(), null, term);
		Set<EC> ecs = new TreeSet<EC>();
		for (ComparableTriple triple : triples) {
			ecs.add(new EC((ComparableTerm) triple.getObject()));
		}
		return ecs;
	}

	public ComparableTriple link2Go(GO go) {
		SimpleComparableOntology ont = TermsAndOntologies.getOntologyToLinksKOToGO();
		ComparableTerm term = TermsAndOntologies.getTermToLinkKOToGO();
		return ont.getOrCreateTriple(getTerm(), go.getTerm(), term);
	}

	public String getDefinition() {
		return term.getDescription();
	}

	public void setDefinition(String definition) {
		term.setDescription(definition);
	}

}
