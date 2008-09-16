/*
 * Created on 12/09/2008
 */
package baobab.sequence.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.hibernate.Transaction;

import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class PFFile1
{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Transaction tx = BioSql.beginTransaction();
		try {
			File fileName;
			if (args.length > 0) {
				fileName = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose file name to save");
				int returnVal = fc.showSaveDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileName = fc.getSelectedFile();
			}

			// the ncbiTaxon of organism
			Organism organism;
			String respDialog;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
					Messages.getString("ncbiTaxonNumberDefault"));
				if (respDialog == null) {
					return;
				}
				int ncbiTaxonNumber = Integer.parseInt(respDialog);
				try {
					organism = new Organism(ncbiTaxonNumber);
				}
				catch (DBObjectNotFound ex) {
					organism = null;
				}
				while (organism == null) {
					respDialog = JOptionPane.showInputDialog("NCBITaxonID not found. Please input the NCBI_Taxon_ID:",
						ncbiTaxonNumber);
					if (respDialog == null) {
						return;
					}
					ncbiTaxonNumber = Integer.parseInt(respDialog);
					try {
						organism = new Organism(ncbiTaxonNumber);
					}
					catch (DBObjectNotFound ex) {
					}
				}
			}
			else {
				organism = new Organism(Integer.parseInt(args[1]));
			}

			//Version
			int version;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the version of sequences:");
				if (respDialog == null) {
					return;
				}
			}
			else {
				respDialog = args[2];
			}
			version = Integer.parseInt(respDialog);

			int step = Integer.parseInt(Messages.getString("CDSFile.step"));
			Collection<Integer> seqs = BioSql.getSequencesId(organism, version);
			PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			wr.print(Messages.getString("CDSFile.heading"));

			System.out.print("Start time:");
			System.out.println(new Date());
			Progress progress = new ProgressPrintInterval(System.out, step,
				Messages.getString("CDSFile.initialMessage") + fileName);
			progress.init();
			int i = 0;

			for (Integer seqId : seqs) {
				RichSequence seq = BioSql.getSequence(seqId);
				for (RichFeature gene : (Set<RichFeature>) seq.getFeatureSet()) {
					if (gene.getTypeTerm() == TermsAndOntologies.getTermGene()) {
						for (Iterator geneFeatures = gene.features(); geneFeatures.hasNext();) {
							RichFeature mRNA = (RichFeature) geneFeatures.next();
							if (mRNA.getTypeTerm() == TermsAndOntologies.getTermMRNA()) {
								for (Iterator mRNAFeatures = mRNA.features(); mRNAFeatures.hasNext();) {
									RichFeature CDS = (RichFeature) mRNAFeatures.next();
									if (CDS.getTypeTerm() == TermsAndOntologies.getTermCDS()) {
										wr.println();
										wr.print(CDS.getAnnotation().getProperty(TermsAndOntologies.getTermProteinID())
											+ "\t");
										wr.print(mRNA.getAnnotation().getProperty(TermsAndOntologies.getTermMRNAID())
											+ "\t");
										wr.print(CDS.getAnnotation().getProperty(TermsAndOntologies.getTermGene())
											+ "\t");
										wr.print(CDS.getAnnotation().getProperty(TermsAndOntologies.getTermCDSName())
											+ "\t");
										wr.print(seq.getAccession() + "\t");
										i++;
										if (i % step == 0) {
											wr.flush();
											BioSql.restartTransaction();
										}
										progress.completeStep();
									}
								}
							}
						}
					}
				}
			}

			BioSql.endTransactionOK();
			Object[] a = {i};
			progress.finish(MessageFormat.format(Messages.getString("CDSFile.finalMessage"), a));
			System.out.print("End time:");
			System.out.println(new Date());
		}
		catch (DBObjectNotFound e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			BioSql.finish();
		}
	}
}
