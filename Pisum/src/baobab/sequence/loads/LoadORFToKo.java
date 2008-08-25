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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.ontology.ComparableTerm;
import org.hibernate.Transaction;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Compilation;
import baobab.sequence.general.Messages;
import baobab.sequence.general.ORF;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadORFToKo
{

	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		Transaction tx = BioSql.beginTransaction();
		try {
			//file .ko
			File fileORFKo;
			if (args.length > 0) {
				fileORFKo = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose ORF2KO file (.ko)");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileORFKo = fc.getSelectedFile();
			}

			// the ncbiTaxon of organism
			Organism organism;
			String respDialog;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
					Messages.getString("LoadORFToKo.ncbiTaxonNumberDefault"));
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
				method = TermsAndOntologies.getOntologyToLinkORFToKO().getOrCreateTerm(respDialog);
			}
			else {
				method = TermsAndOntologies.getOntologyToLinkORFToKO().getOrCreateTerm(args[2]);
			}

			String orfName, koId;
			BufferedReader br = new BufferedReader(new FileReader(fileORFKo));
			String line;
			String[] splits;
			ORF orf;
			KO ko;
			Compilation comp = null;
			int linkCounter = 0, linkErrorORf = 0;
			int stepORF = Integer.parseInt(Messages.getString("LoadORFToKo.printORFKo"));
			Progress progress = new ProgressPrintInterval(System.out, stepORF,
				Messages.getString("LoadORFToKo.initialMessage") + fileORFKo.getPath());
			progress.init();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					splits = line.split("\t");
					if (splits.length == 2) {
						orfName = splits[0];
						koId = splits[1];
						orf = BioSql.getORF(orfName, organism);
						ko = new KO(koId);
						if (orf == null) {
							linkErrorORf++;
							System.out.println("ORF not found:" + orfName);
						}
						else {
							orf.link2KO(ko, method);
							if (comp == null) {
								comp = BioSql.getCompilation(organism,
									((RichSequence) orf.getFeature().getSequence()).getSeqVersion());
								(TermsAndOntologies.getOntologyGeneral()).getOrCreateTriple(method, comp.getTerm(),
									TermsAndOntologies.getMethodCompTerm());
							}
							progress.completeStep();
							linkCounter++;
							if (linkCounter % stepORF == 0) {
								BioSql.restartTransaction();
								//								System.out.println("OK restart");
							}
						}
					}
				}
			}
			BioSql.endTransactionOK();
			Object[] a = {linkCounter, linkErrorORf};
			progress.finish(MessageFormat.format(Messages.getString("LoadORFToKo.finalMessage"), a));
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
		catch (BioException e) {
			e.printStackTrace();
		}
		finally {
			BioSql.finish();
		}
	}
}
