/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.util.TreeSet;

import org.biojava.bio.BioException;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.seq.SimpleRichLocation;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.exception.InvalidFeature;

public class MRNA
{

	SimpleRichFeature	feature;

	public MRNA(SimpleRichFeature feature) {
		this.feature = feature;
		if (feature.getTypeTerm() != TermsAndOntologies.getTermMRNA()) {
			throw new InvalidFeature(feature);
		}
	}

	public ORF createORF(String name, SimpleRichLocation location, ComparableTerm sourceTerm) throws BioException {
		RichFeature.Template ft = new RichFeature.Template();
		ft.location = location;
		ft.sourceTerm = sourceTerm;
		ft.typeTerm = TermsAndOntologies.getTermORF();
		ft.annotation = new SimpleRichAnnotation();
		ft.featureRelationshipSet = new TreeSet();
		ft.rankedCrossRefs = new TreeSet();
		SimpleRichFeature featureORF = (SimpleRichFeature) feature.createFeature(ft);
		featureORF.setName(name);
		featureORF.setRank(0);
		return new ORF(featureORF);
	}

}
