/*
 * Created on 12/09/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.seq.Sequence;
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

			int stepShow = Integer.parseInt(Messages.getString("PFFile.stepShow"));
			int stepRestart = Integer.parseInt(Messages.getString("PFFile.stepRestart"));
			//			Collection<Integer> seqs = BioSql.getSequencesIdCDStRNAmRNA(organism, version);
			PFFileStream pfFile = new PFFileStream(fileName);
			pfFile.println(Messages.getString("PFFile.heading"));
			String fileFastaName = fileName + ".fsa";
			FastaFileForPFFile fastaFile = new FastaFileForPFFile(fileFastaName,
				Messages.getString("FastaFileForPFFile.header"),
				Integer.parseInt(Messages.getString("FastaFileForPFFile.lineWidth")));
			String fileIdsName = fileName + ".BioCycIds";
			PrintStream ids;
			ids = new PrintStream(new File(fileIdsName));

			System.out.print("Start time:");
			System.out.println(new Date());
			Progress progress = new ProgressPrintInterval(System.out, stepShow,
				Messages.getString("PFFile.initialMessage") + fileName);
			progress.init();
			int cdsCounter = 0, tRNACounter = 0, miscRNACounter = 0, allCounter = 0;
			int pos = 0;

			Collection<Integer> featureIds;
			Collection<Integer> seqIds = BioSql.getSequencesIdCDStRNAmRNA(organism, version);
			GeneRecord record;

			for (Integer seqId : seqIds) {
				featureIds = BioSql.getCDSMiscRNATRNABySeqId(seqId);

				for (Integer featureId : featureIds) {
					try {
						RichFeature feature = BioSql.getFeature(featureId);
						Sequence seq = feature.getSequence();
						if (feature.getTypeTerm() == TermsAndOntologies.getTermCDS()) {
							record = new CDSRecord(feature);
							cdsCounter++;
						}
						else if (feature.getTypeTerm() == TermsAndOntologies.getTermMiscRNA()) {
							record = new MiscRNARecord(feature);
							miscRNACounter++;
						}
						else {
							record = new TRNARecord(feature);
							tRNACounter++;
						}
						allCounter++;
						record.shiftLocation(pos);
						pfFile.print(record);

						if (record.getProductID() != null) {
							ids.println(record.getProductID() + "\t" + record.getId());
						}
						else {
							ids.println(record.getName() + ";" + ((RichSequence) feature.getSequence()).getAccession()
								+ "\t" + record.getId());
						}
						if (allCounter % stepRestart == 0) {
							BioSql.restartTransaction();
							SimpleGeneRecord.nextBiocycId = TermsAndOntologies.getNextBiocycId();
							fastaFile.restart();
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
				pos += fastaFile.write(seqId);
			}

			BioSql.endTransactionOK();
			fastaFile.flush();
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
