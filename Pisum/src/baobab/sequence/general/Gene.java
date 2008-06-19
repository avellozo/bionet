/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.dbExternal.KO;

public class Gene
{
	SimpleRichFeature	feature;

	public Gene(SimpleRichFeature feature) {
		this.feature = feature;
		if (feature.getTypeTerm() != TermsAndOntologies.TERM_GENE) {
			throw new InvalidFeature(feature);
		}
	}

	public SimpleRichFeature getFeature() {
		return feature;
	}

	public SimpleRichFeature link2KO(KO ko, ComparableTerm method) throws BioException {
		Feature.Template ft = new RichFeature.Template();
		ft.location = feature.getLocation().translate(0);
		ft.sourceTerm = ko.getTerm();
		ft.typeTerm = method;
		ft.annotation = new SimpleRichAnnotation();
		SimpleRichFeature newFeature = (SimpleRichFeature) feature.createFeature(ft);
		newFeature.setName(ko.getId());
		newFeature.setRank(0);
		return newFeature;
	}

	public class InvalidFeature extends RuntimeException
	{
		Feature	feature;

		public InvalidFeature(SimpleRichFeature feature) {
			super(feature.toString());
			this.feature = feature;
		}

	}
}
