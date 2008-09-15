/*
 * Created on 08/09/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.program.gff3.GFF3DocumentHandler;
import org.biojava.bio.program.gff3.GFF3Parser;
import org.biojava.bio.program.gff3.GFF3Record;

import baobab.sequence.exception.DBObjectAlreadyExists;
import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.CDS;
import baobab.sequence.general.Compilation;
import baobab.sequence.general.Gene;
import baobab.sequence.general.MRNA;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.Sequence;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadNcbiGeneGff3 implements GFF3DocumentHandler
{
	int			step;
	Progress	progress;
	Compilation	comp;

	Sequence	seq		= null;
	Gene		gene	= null;
	MRNA		mrna	= null;
	CDS			cds		= null;

	public LoadNcbiGeneGff3(int step, Progress progress, Compilation comp) {
		this.step = step;
		this.progress = progress;
		this.comp = comp;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//file Fasta
			String fileFastaName;
			if (args.length > 0) {
				fileFastaName = args[0];
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose FASTA file");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileFastaName = fc.getSelectedFile().getPath();
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

			//compilation
			Compilation comp;
			if (args.length < 3) {
				Compilation lastComp = organism.getLastCompilation();
				String lastNameComp = "";
				if (lastComp != null) {
					lastNameComp = lastComp.getName();
				}
				respDialog = JOptionPane.showInputDialog("Please input the name of compilation:", lastNameComp);
				if (respDialog == null) {
					return;
				}

				String nameComp = respDialog;
				try {
					comp = organism.createCompilation(nameComp);
				}
				catch (DBObjectAlreadyExists ex) {
					comp = null;
				}
				while (comp == null) {
					respDialog = JOptionPane.showInputDialog(
						"Compilation name already exists. Please input the name of compilation:", nameComp);
					if (respDialog == null) {
						return;
					}
					nameComp = respDialog;
					try {
						comp = organism.createCompilation(nameComp);
					}
					catch (DBObjectAlreadyExists ex) {
						comp = null;
					}
				}
			}
			else {
				comp = organism.createCompilation(args[2]);
			}

			int step = Integer.parseInt(Messages.getString("LoadNcbiGeneGff3.printStep"));
			LoadNcbiGeneGff3 loadNcbiGeneGff3 = new LoadNcbiGeneGff3(step, new ProgressPrintInterval(System.out, step,
				Messages.getString("LoadNcbiGeneGff3.initialMessage") + fileFastaName), comp);
			(new GFF3Parser()).parse(new BufferedReader(new FileReader(fileFastaName)), loadNcbiGeneGff3,
				TermsAndOntologies.getOntologyFeatures(), fileFastaName);

		}
		catch (Exception e) {
			e.printStackTrace();
			BioSql.finish();
		}
	}

	public void commentLine(String comment) {

	}

	public void endDocument() {
		BioSql.finish();
	}

	public void recordLine(GFF3Record record) {
		// TODO Auto-generated method stub

	}

	public void startDocument(String locator) {
		BioSql.beginTransaction();
	}

}
