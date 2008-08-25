/*
 * Created on 24/06/2008
 */
package baobab.sequence.exception;

import org.biojava.bio.seq.Feature;
import org.biojavax.bio.seq.SimpleRichFeature;

public class InvalidFeature extends RuntimeException
{
	Feature	feature;

	public InvalidFeature(SimpleRichFeature feature) {
		super(feature.toString());
		this.feature = feature;
	}

}
