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
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.ui.Progress;

public class Compilation
{
	Organism				organism;
	private ComparableTerm	term;

	public ComparableTerm getTerm() {
		return term;
	}

	public Organism getOrganism() {
		return organism;
	}

	public String getName() {
		return term.getName();
	}

	public double getVersion() {
		return new Double(term.getDescription());
	}

	protected Compilation(Organism organism, ComparableTerm term) {
		this.organism = organism;
		this.term = term;
	}

	public void LoadESTs(String fileFastaName, Progress progress, int stepToSave)
			throws FileNotFoundException, BioException {
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
			BioSql.save(est);
			if (progress != null) {
				progress.completeStep();
			}
			i++;
			if (i % stepToSave == 0) {
				BioSql.restartTransaction();
			}
		}
		BioSql.endTransactionOK();
		if (progress != null) {
			progress.finish();
		}
	}

}
