/*
 * Created on 05/06/2008
 */
package baobab.sequence.loads;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.general.BioSql;
import baobab.sequence.general.TermsAndOntologies;

public class LoadEstToKo
{

	/**
	 * @param args args[0] =
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
				fc.setDialogTitle("Choose EST2KO file");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileFastaName = fc.getSelectedFile().getPath();
			}

			//Method
			ComparableTerm method;
			if (args.length < 2) {
				String respDialog = JOptionPane.showInputDialog("Please input the name of method:");
				if (respDialog == null) {
					return;
				}
				method = TermsAndOntologies.ONT_LINK_GENE2KO.getOrCreateTerm(respDialog);
			}
			else {
				method = TermsAndOntologies.ONT_LINK_GENE2KO.getOrCreateTerm(args[2]);
			}

		}
		finally {
			BioSql.finish();
		}
	}
}
