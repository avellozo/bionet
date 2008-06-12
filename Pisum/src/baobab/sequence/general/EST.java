/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.SimplePosition;
import org.biojavax.bio.seq.SimpleRichLocation;
import org.biojavax.bio.seq.RichLocation.Strand;

public class EST extends Sequence
{

	public EST(RichSequence seq, Compilation comp) throws BioException {
		super(seq, comp);
		createGene(getName(), new SimpleRichLocation(new SimplePosition(1), new SimplePosition(getlength()), 0,
			Strand.UNKNOWN_STRAND), TermsAndOntologies.TERM_EST);
	}

}