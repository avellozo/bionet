/*
 * Created on 12/09/2008
 */
package baobab.sequence.generator;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojavax.bio.seq.RichFeature;
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

			int stepShow = Integer.parseInt(Messages.getString("PFFile.stepShow"));
			int stepRestart = Integer.parseInt(Messages.getString("PFFile.stepRestart"));
			//			Collection<Integer> seqs = BioSql.getSequencesIdCDStRNAmRNA(organism, version);
			PFFileStream wr = new PFFileStream(fileName);
			wr.println(Messages.getString("PFFile.heading"));
			String fileFastaName = fileName + ".fsa";
			FastaFileForPFFile fastaFile = new FastaFileForPFFile(fileFastaName);

			System.out.print("Start time:");
			System.out.println(new Date());
			Progress progress = new ProgressPrintInterval(System.out, stepShow,
				Messages.getString("PFFile.initialMessage") + fileName);
			progress.init();
			int cdsCounter = 0, tRNACounter = 0, miscRNACounter = 0;
			int pos = 0;

			Collection<Integer> cdsIds = BioSql.getFeaturesId(TermsAndOntologies.getTermCDS(), organism, version);
			//			System.out.println(cdsIds.size());
			//			System.exit(0);
			for (Integer cdsId : cdsIds) {
				RichFeature cDS = BioSql.getFeature(cdsId);
				try {
					CDSRecord record = new CDSRecord(cDS);
					record.shiftLocation(pos);
					wr.print(record);
					cdsCounter++;
					if (cdsCounter % stepRestart == 0) {
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
			Collection<Integer> tRNAIds = BioSql.getFeaturesId(TermsAndOntologies.getTermTRNA(), organism, version);
			for (Integer tRNAId : tRNAIds) {
				RichFeature tRNA = BioSql.getFeature(tRNAId);
				try {
					TRNARecord record = new TRNARecord(tRNA);
					record.shiftLocation(pos);
					wr.print(record);
					tRNACounter++;
					if (tRNACounter % stepRestart == 0) {
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
			Collection<Integer> miscRNAIds = BioSql.getFeaturesId(TermsAndOntologies.getTermMiscRNA(), organism,
				version);
			for (Integer miscRNAId : miscRNAIds) {
				RichFeature miscRNA = BioSql.getFeature(miscRNAId);
				try {
					MiscRNARecord record = new MiscRNARecord(miscRNA);
					record.shiftLocation(pos);
					wr.print(record);
					miscRNACounter++;
					if (miscRNACounter % stepRestart == 0) {
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

			//			for (Integer seqId : seqs) {
			//				RichSequence seq = BioSql.getSequence(seqId);
			//				for (RichFeature gene : (Set<RichFeature>) seq.getFeatureSet()) {
			//					if (gene.getTypeTerm() == TermsAndOntologies.getTermGene()) {
			//						for (Iterator geneFeatures = gene.features(); geneFeatures.hasNext();) {
			//							RichFeature mRNA = (RichFeature) geneFeatures.next();
			//							if (mRNA.getTypeTerm() == TermsAndOntologies.getTermMRNA()) {
			//								for (Iterator mRNAFeatures = mRNA.features(); mRNAFeatures.hasNext();) {
			//									RichFeature cDS = (RichFeature) mRNAFeatures.next();
			//									if (cDS.getTypeTerm() == TermsAndOntologies.getTermCDS()) {
			//										try {
			//											CDSRecord record = new CDSRecord(cDS);
			//											//											record.shiftLocation(pos);
			//											wr.print(record);
			//											cdsCounter++;
			//											allCounter++;
			//											//											if (allCounter % step == 0) {
			//											//												//												wr.restart();
			//											//												//												fastaFile.restart();
			//											//												BioSql.restartTransaction();
			//											//											}
			//											progress.completeStep();
			//										}
			//										//										catch (GeneRecordInvalidException e) {
			//										//											e.printStackTrace();
			//										//										}
			//										catch (Exception e) {
			//											e.printStackTrace();
			//											System.out.println(cDS);
			//										}
			//										break;
			//									}
			//								}
			//							}
			//						}
			//					}
			//					else if (gene.getTypeTerm() == TermsAndOntologies.getTermTRNA()) {
			//						try {
			//							TRNARecord record = new TRNARecord(gene);
			//							//							record.shiftLocation(pos);
			//							wr.print(record);
			//							tRNACounter++;
			//							allCounter++;
			//							//												if (allCounter % step == 0) {
			//							//													//								wr.restart();
			//							//													//								fastaFile.restart();
			//							//													BioSql.restartTransaction();
			//							//												}
			//							progress.completeStep();
			//						}
			//						//						catch (GeneRecordInvalidException e) {
			//						//							e.printStackTrace();
			//						//						}
			//						catch (Exception e) {
			//							e.printStackTrace();
			//						}
			//					}
			//					else if (gene.getTypeTerm() == TermsAndOntologies.getTermMiscRNA()) {
			//						try {
			//							MiscRNARecord record = new MiscRNARecord(gene);
			//							//							record.shiftLocation(pos);
			//							wr.print(record);
			//							miscRNACounter++;
			//							allCounter++;
			//							//												if (allCounter % step == 0) {
			//							//													//								wr.restart();
			//							//													//								fastaFile.restart();
			//							//													BioSql.restartTransaction();
			//							//												}
			//							progress.completeStep();
			//						}
			//						//						catch (GeneRecordInvalidException e) {
			//						//							e.printStackTrace();
			//						//						}
			//						catch (Exception e) {
			//							e.printStackTrace();
			//						}
			//					}
			//				}
			//				//write sequence to fasta file
			//				//				if (writeSeq) {
			//				//					fastaFile.write(seq);
			//				//					pos += seq.length();
			//				//				}
			//			}
			//
			BioSql.endTransactionOK();
			int allCounter = cdsCounter + tRNACounter + miscRNACounter;
			Object[] a = {allCounter, cdsCounter, tRNACounter, miscRNACounter};
			progress.finish(MessageFormat.format(Messages.getString("PFFile.finalMessage"), a));
			System.out.print("End time:");
			System.out.println(new Date());
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
