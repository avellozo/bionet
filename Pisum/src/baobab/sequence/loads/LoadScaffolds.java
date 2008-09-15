/*
 * Created on 05/06/2008
 */
package baobab.sequence.loads;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

import javax.swing.JFileChooser;

import org.biojava.bio.BioException;

import baobab.sequence.general.BioSql;
import baobab.sequence.general.Messages;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadScaffolds
{

	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		try {
			//file Fasta
			String fileName;
			if (args.length > 0) {
				fileName = args[0];
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose file");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileName = fc.getSelectedFile().getPath();
			}

			//			// the ncbiTaxon of organism
			//			Organism organism;
			//			String respDialog;
			//			if (args.length < 2) {
			//				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
			//					Messages.getString("ncbiTaxonNumberDefault"));
			//				if (respDialog == null) {
			//					return;
			//				}
			//				int ncbiTaxonNumber = Integer.parseInt(respDialog);
			//				try {
			//					organism = new Organism(ncbiTaxonNumber);
			//				}
			//				catch (DBObjectNotFound ex) {
			//					organism = null;
			//				}
			//				while (organism == null) {
			//					respDialog = JOptionPane.showInputDialog("NCBITaxonID not found. Please input the NCBI_Taxon_ID:",
			//						ncbiTaxonNumber);
			//					if (respDialog == null) {
			//						return;
			//					}
			//					ncbiTaxonNumber = Integer.parseInt(respDialog);
			//					try {
			//						organism = new Organism(ncbiTaxonNumber);
			//					}
			//					catch (DBObjectNotFound ex) {
			//					}
			//				}
			//			}
			//			else {
			//				organism = new Organism(Integer.parseInt(args[1]));
			//			}
			//
			//			//compilation
			//			Compilation comp;
			//			if (args.length < 3) {
			//				Compilation lastComp = organism.getLastCompilation();
			//				String lastNameComp = "";
			//				if (lastComp != null) {
			//					lastNameComp = lastComp.getName();
			//				}
			//				respDialog = JOptionPane.showInputDialog("Please input the name of compilation:", lastNameComp);
			//				if (respDialog == null) {
			//					return;
			//				}
			//
			//				String nameComp = respDialog;
			//				try {
			//					comp = organism.createCompilation(nameComp);
			//				}
			//				catch (DBObjectAlreadyExists ex) {
			//					comp = null;
			//				}
			//				while (comp == null) {
			//					respDialog = JOptionPane.showInputDialog(
			//						"Compilation name already exists. Please input the name of compilation:", nameComp);
			//					if (respDialog == null) {
			//						return;
			//					}
			//					nameComp = respDialog;
			//					try {
			//						comp = organism.createCompilation(nameComp);
			//					}
			//					catch (DBObjectAlreadyExists ex) {
			//						comp = null;
			//					}
			//				}
			//			}
			//			else {
			//				comp = organism.createCompilation(args[2]);
			//			}

			int stepScaffold = Integer.parseInt(Messages.getString("LoadScaffolds.printScaffolds"));
			BioSql.LoadScaffolds(fileName, new ProgressPrintInterval(System.out, stepScaffold,
				Messages.getString("LoadScaffolds.initialMessage") + fileName), stepScaffold);

		}
		catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
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
