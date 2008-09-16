/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.util.TreeSet;

import org.biojava.bio.BioException;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.exception.InvalidFeature;

public class CDS
{
	SimpleRichFeature	feature;

	public CDS(SimpleRichFeature feature) {
		this.feature = feature;
		if (feature.getTypeTerm() != TermsAndOntologies.getTermCDS()) {
			throw new InvalidFeature(feature);
		}
	}

	public String getSequenceStr() {
		return feature.getSymbols().seqString();
	}

	public SimpleRichFeature getFeature() {
		return feature;
	}

	public SimpleRichFeature link2KO(KO ko, ComparableTerm method) throws BioException {
		RichFeature.Template ft = new RichFeature.Template();
		ft.location = feature.getLocation().translate(0);
		ft.sourceTerm = ko.getTerm();
		ft.typeTerm = method;
		ft.annotation = new SimpleRichAnnotation();
		ft.featureRelationshipSet = new TreeSet();
		ft.rankedCrossRefs = new TreeSet();
		SimpleRichFeature newFeature = (SimpleRichFeature) feature.createFeature(ft);
		newFeature.setName(ko.getId());
		return newFeature;
	}

	public void link2KO(ComparableTerm method, String ko) {
		feature.getAnnotation().setProperty(method, ko);
	}
}
