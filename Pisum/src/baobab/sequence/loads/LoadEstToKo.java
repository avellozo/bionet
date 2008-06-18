/*
 * Created on 05/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.BioException;
import org.biojavax.ontology.ComparableTerm;
import org.hibernate.Transaction;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Gene;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.Sequence;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadEstToKo
{

	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		Transaction tx = BioSql.beginTransaction();
		try {
			//file .ko
			File fileEstKo;
			if (args.length > 0) {
				fileEstKo = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose EST2KO file (.ko)");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileEstKo = fc.getSelectedFile();
			}

			// the ncbiTaxon of organism
			Organism organism;
			String respDialog;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
					Messages.getString("LoadESTToKo.ncbiTaxonNumberDefault"));
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
				method = TermsAndOntologies.ONT_LINK_GENE2KO.getOrCreateTerm(respDialog);
			}
			else {
				method = TermsAndOntologies.ONT_LINK_GENE2KO.getOrCreateTerm(args[2]);
			}

			String estName, koId;
			BufferedReader br = new BufferedReader(new FileReader(fileEstKo));
			String line;
			String[] splits;
			Sequence seq;
			Gene gene;
			KO ko;
			int linkCounter = 0, linkErrorGene = 0;
			Progress progress = new ProgressPrintInterval(System.out,
				Integer.parseInt(Messages.getString("LoadEstToKo.printEstKo")),
				Messages.getString("LoadEstToKo.initialMessage") + fileEstKo.getPath());
			progress.init();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					progress.completeStep();
					splits = line.split("\t");
					if (splits.length == 2) {
						estName = splits[0];
						koId = splits[1];
						gene = BioSql.getGene(estName, organism);
						ko = new KO(koId);
						if (gene == null) {
							linkErrorGene++;
							System.out.println("Gene not found:" + estName);
						}
						else {
							gene.link2KO(ko, method);
							BioSql.save(gene);
							linkCounter++;
							if (linkCounter % 1000 == 0) {
								BioSql.restartTransaction(tx);
							}
						}
					}
				}
			}
			BioSql.endTransactionOK(tx);
			progress.finish(linkCounter + Messages.getString("LoadEstToKo.finalMessage") + ", gene:" + linkErrorGene);
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
			BioSql.finish(tx);
		}
	}
}
