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

	public CDS createCDS(String name, SimpleRichLocation location, ComparableTerm sourceTerm) throws BioException {
		RichFeature.Template ft = new RichFeature.Template();
		ft.location = location;
		ft.sourceTerm = sourceTerm;
		ft.typeTerm = TermsAndOntologies.getTermCDS();
		ft.annotation = new SimpleRichAnnotation();
		ft.featureRelationshipSet = new TreeSet();
		ft.rankedCrossRefs = new TreeSet();
		SimpleRichFeature featureCDS = (SimpleRichFeature) feature.createFeature(ft);
		featureCDS.setName(name);
		featureCDS.setRank(0);
		return new CDS(featureCDS);
	}

}
