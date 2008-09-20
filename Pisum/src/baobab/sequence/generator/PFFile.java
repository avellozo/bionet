/*
 * Created on 12/09/2008
 */
package baobab.sequence.generator;

import java.io.FileNotFoundException;
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

public class PFFile
{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Transaction tx = BioSql.beginTransaction();
		try {
			String fileName;
			if (args.length > 0) {
				fileName = args[0];
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose file name to save");
				int returnVal = fc.showSaveDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileName = fc.getSelectedFile().getPath();
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
			if (args.length < 3) {
				respDialog = JOptionPane.showInputDialog("Please input the version of sequences:");
				if (respDialog == null) {
					return;
				}
			}
			else {
				respDialog = args[2];
			}
			version = Integer.parseInt(respDialog);

			int step = Integer.parseInt(Messages.getString("PFFile.step"));
			Collection<Integer> seqs = BioSql.getSequencesId(organism, version);
			PFFileStream wr = new PFFileStream(fileName);
			wr.println(Messages.getString("PFFile.heading"));
			String fileFastaName = fileName + ".fsa";
			FastaFileForPFFile fastaFile = new FastaFileForPFFile(fileFastaName);

			System.out.print("Start time:");
			System.out.println(new Date());
			Progress progress = new ProgressPrintInterval(System.out, step, Messages.getString("PFFile.initialMessage")
				+ fileName);
			progress.init();
			int cdsCounter = 0, tRNACounter = 0, miscRNACounter = 0, allCounter = 0;
			int cdsAuxCount = 0;
			boolean writeSeq;
			int pos = 0;

			for (Integer seqId : seqs) {
				RichSequence seq = BioSql.getSequence(seqId);
				writeSeq = false;
				for (RichFeature feature : (Set<RichFeature>) seq.getFeatureSet()) {
					if (feature.getTypeTerm() == TermsAndOntologies.getTermGene()) {
						for (Iterator geneFeatures = feature.features(); geneFeatures.hasNext();) {
							RichFeature geneFeature = (RichFeature) geneFeatures.next();
							if (geneFeature.getTypeTerm() == TermsAndOntologies.getTermMRNA()) {
								for (Iterator mRNAFeatures = geneFeature.features(); mRNAFeatures.hasNext();) {
									RichFeature mRNAFeature = (RichFeature) mRNAFeatures.next();
									if (mRNAFeature.getTypeTerm() == TermsAndOntologies.getTermCDS()) {
										try {
											cdsAuxCount++;
											CDSRecord record = new CDSRecord(mRNAFeature);
											record.shiftLocation(pos);
											wr.print(record);
											writeSeq = true;
											cdsCounter++;
											allCounter++;
											if (allCounter % step == 0) {
												//												wr.restart();
												//												fastaFile.restart();
												BioSql.restartTransaction();
											}
											progress.completeStep();
										}
										catch (GeneRecordInvalidException e) {
											e.printStackTrace();
										}
										catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					else if (feature.getTypeTerm() == TermsAndOntologies.getTermTRNA()) {
						try {
							TRNARecord record = new TRNARecord(feature);
							record.shiftLocation(pos);
							wr.print(record);
							writeSeq = true;
							tRNACounter++;
							allCounter++;
							if (allCounter % step == 0) {
								//								wr.restart();
								//								fastaFile.restart();
								BioSql.restartTransaction();
							}
							progress.completeStep();
						}
						catch (GeneRecordInvalidException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if (feature.getTypeTerm() == TermsAndOntologies.getTermMiscRNA()) {
						try {
							MiscRNARecord record = new MiscRNARecord(feature);
							record.shiftLocation(pos);
							wr.print(record);
							writeSeq = true;
							miscRNACounter++;
							allCounter++;
							if (allCounter % step == 0) {
								//								wr.restart();
								//								fastaFile.restart();
								BioSql.restartTransaction();
							}
							progress.completeStep();
						}
						catch (GeneRecordInvalidException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				//write sequence to fasta file
				//				if (writeSeq) {
				//					fastaFile.write(seq);
				//					pos += seq.length();
				//				}
			}

			BioSql.endTransactionOK();
			Object[] a = {allCounter, cdsCounter, tRNACounter, miscRNACounter};
			progress.finish(MessageFormat.format(Messages.getString("PFFile.finalMessage"), a));
			System.out.print("End time:");
			System.out.println(new Date());
			System.out.println(cdsAuxCount);
		}
		catch (DBObjectNotFound e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			BioSql.finish();
		}
	}
}
