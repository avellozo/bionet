/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;

public class Scaffold extends Sequence
{

	public Scaffold(RichSequence seq, Compilation comp) throws BioException {
		super(seq, comp);
	}
}
