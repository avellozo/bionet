/*
 * Created on 06/06/2008
 */
package baobab.sequence.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import baobab.sequence.ui.Progress;

public class Compilation
{
	Organism	organism;
	String		name;
	double		version;

	public Organism getOrganism() {
		return organism;
	}

	public String getName() {
		return name;
	}

	public double getVersion() {
		return version;
	}

	protected Compilation(Organism organism, String name, double version) {
		this.organism = organism;
		this.name = name;
		this.version = version;
	}

	public void LoadESTs(String fileFastaName, Progress progress) throws FileNotFoundException, BioException {
		// an input FASTA file
		BufferedReader fastaFileReader = new BufferedReader(new FileReader(fileFastaName));

		if (progress != null) {
			progress.init();
		}

		int i = 0;

		BioSql.beginTransaction();
		// we are reading DNA sequences
		RichSequenceIterator seqs = RichSequence.IOTools.readFastaDNA(fastaFileReader, BioSql.getDefaultNamespace());
		while (seqs.hasNext()) {
			EST est = new EST(seqs.nextRichSequence(), this);
			BioSql.saveEst(est);
			if (progress != null) {
				progress.completeStep();
			}
			i++;
			if (i % 1000 == 0) {
				BioSql.restartTransaction();
			}
		}
		BioSql.endTransactionOK();
		if (progress != null) {
			progress.finish();
		}
	}
}
