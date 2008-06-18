/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.util.Set;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.utils.ChangeVetoException;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.seq.SimpleRichLocation;
import org.biojavax.ontology.ComparableTerm;

public class Sequence
{
	private RichSequence	seq;
	Compilation				comp;

	public Sequence(RichSequence seq, Compilation comp) {
		this.seq = seq;
		this.comp = comp;
		seq.setTaxon(comp.getOrganism().getTaxon());
		seq.setSeqVersion(comp.getVersion());
	}

	public RichSequence getSequence() {
		return seq;
	}

	public Compilation getCompilation() {
		return comp;
	}

	public String getName() {
		return seq.getName();
	}

	public Gene createGene(String name, SimpleRichLocation location, ComparableTerm sourceTerm)
			throws ChangeVetoException, BioException {
		Feature.Template ft = new RichFeature.Template();
		ft.location = location;
		ft.sourceTerm = sourceTerm;
		ft.typeTerm = TermsAndOntologies.TERM_GENE;
		ft.annotation = new SimpleRichAnnotation();
		SimpleRichFeature feature = (SimpleRichFeature) seq.createFeature(ft);
		feature.setName(name);
		feature.setRank(0);
		return new Gene(feature);
	}

	public int getlength() {
		return seq.length();
	}

	public Gene getGene(String geneName) {

		Set<SimpleRichFeature> features = seq.getFeatureSet();
		for (SimpleRichFeature feature : features) {
			if (feature.getTypeTerm() == TermsAndOntologies.TERM_GENE && feature.getName().equals(geneName)) {
				return new Gene(feature);
			}
		}
		//		System.out.println("Seq name: " + getName() + " gene name: " + geneName);
		//		for (SimpleRichFeature feature : features) {
		//			if (feature.getTypeTerm() == TermsAndOntologies.TERM_GENE && feature.getName().equals(geneName)) {
		//				System.out.println("	feature term name: " + feature.getTypeTerm().getName() + " term gene name: "
		//					+ TermsAndOntologies.TERM_GENE.getName());
		//				System.out.println("	feature name: " + feature.getName() + " gene name: " + geneName);
		//			}
		//		}
		return null;
	}
}
