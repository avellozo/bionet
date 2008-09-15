/*
 * Created on 05/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojavax.bio.seq.RichSequence;
import org.biojavax.ontology.ComparableTerm;
import org.hibernate.Transaction;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.CDS;
import baobab.sequence.general.Compilation;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadCDSToKo
{

	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		Transaction tx = BioSql.beginTransaction();
		try {
			//file .ko
			File fileCDSKo;
			if (args.length > 0) {
				fileCDSKo = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose CDS2KO file (.ko)");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileCDSKo = fc.getSelectedFile();
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

			//Method
			ComparableTerm method;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the name of the method:");
				if (respDialog == null) {
					return;
				}
				method = TermsAndOntologies.getOntologyToLinkCDSToKO().getOrCreateTerm(respDialog);
			}
			else {
				method = TermsAndOntologies.getOntologyToLinkCDSToKO().getOrCreateTerm(args[2]);
			}
			Object[] a = {method.getName()};
			method.setDescription(MessageFormat.format(Messages.getString("LoadCDSToKo.propertyDescription"), a));

			String protName, koId;
			BufferedReader br = new BufferedReader(new FileReader(fileCDSKo));
			String line;
			String[] splits;
			CDS cds;
			KO ko;
			Compilation comp = null;
			int linkCounter = 0, linkErrorCDS = 0;
			int stepCDS = Integer.parseInt(Messages.getString("LoadCDSToKo.printCDSKo"));
			System.out.print("Start time:");
			System.out.println(new Date());
			Progress progress = new ProgressPrintInterval(System.out, stepCDS,
				Messages.getString("LoadCDSToKo.initialMessage") + fileCDSKo.getPath());
			progress.init();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					splits = line.split("\t");
					if (splits.length == 2) {
						protName = splits[0].split("\\|")[3];
						koId = splits[1];
						cds = BioSql.getCDSByProtID(protName, organism);
						if (cds == null) {
							linkErrorCDS++;
							System.out.println("CDS not found:" + protName);
						}
						else {
							//							ko = new KO(koId);
							//							cds.link2KO(ko, method);
							cds.link2KO(koId, method);
							if (comp == null) {
								comp = organism.getOrCreateCompilation(((RichSequence) cds.getFeature().getSequence()).getVersion());
								(TermsAndOntologies.getOntologyGeneral()).getOrCreateTriple(method, comp.getTerm(),
									TermsAndOntologies.getMethodCompTerm());
							}
							progress.completeStep();
							linkCounter++;
							if (linkCounter % stepCDS == 0) {
								BioSql.restartTransaction();
							}
						}
					}
				}
			}
			BioSql.endTransactionOK();
			Object[] a1 = {linkCounter, linkErrorCDS};
			progress.finish(MessageFormat.format(Messages.getString("LoadCDSToKo.finalMessage"), a1));
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
