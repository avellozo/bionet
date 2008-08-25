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

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.SimplePosition;
import org.hibernate.Transaction;

import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadORFVR
{
	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		Organism organism;
		Transaction tx = BioSql.beginTransaction();
		try {
			//file orfs
			File fileOrfs;
			if (args.length > 0) {
				fileOrfs = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose ORF file ");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileOrfs = fc.getSelectedFile();
			}

			// the ncbiTaxon of organism

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

			String orfName, geneName;
			BufferedReader br = new BufferedReader(new FileReader(fileOrfs));
			String line, line2, line3, lineAmino;
			//			String lineDNA, lineCodes;
			SimpleORF sOrf = null;
			int countOrfs = 0;
			int beginPos = -1, endPos = -1, nextPos = -1;
			int strand = 0;
			int countOrfsNotFound = 0;
			Object[] a = {fileOrfs.getPath()};
			String msgInitial = MessageFormat.format(Messages.getString("LoadORFVR.initialMessage"), a);
			int stepORF = Integer.parseInt(Messages.getString("LoadORFVR.printORF"));
			Progress progress = new ProgressPrintInterval(System.out, stepORF, msgInitial);
			progress.init();
			System.out.print("Start time:");
			System.out.println(new Date());

			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if (line.startsWith(">")) { //ORF heading
						//new ORF
						//save last ORF
						if (strand != 0) {
							if (strand < 0 && beginPos < 4) {
								sOrf.setBeginLocation(new SimplePosition(true, false, beginPos));
							}
							else {
								sOrf.setBeginLocation(new SimplePosition(beginPos));
							}
							if (strand > 0 && (endPos == (nextPos - 1))) {
								sOrf.setEndLocation(new SimplePosition(false, true, endPos));
							}
							else {
								sOrf.setEndLocation(new SimplePosition(endPos));
							}
							sOrf.save();
							progress.completeStep();
							countOrfs++;
						}
						if (countOrfs % stepORF == 0) {
							BioSql.restartTransaction();
						}
						orfName = line.substring(1);
						geneName = orfName.substring(0, orfName.indexOf("_"));
						line = br.readLine();

						if (line.startsWith("Reading frame: ")) {
							strand = Integer.parseInt(line.substring(15));
							if (strand == 0) {
								System.out.println("Format error, strand = 0");
							}
							else {
								nextPos = 1;
								beginPos = -1;
								endPos = -1;
								sOrf = new SimpleORF(organism, geneName, orfName, TermsAndOntologies.getTermVR());
								sOrf.setStrand(strand);
							}
						}
						else {
							System.out.println("Format error in line 'Reading frame':" + line);
							strand = 0;
						}
						br.readLine(); // empty line
					}
					else if (line.startsWith("NO ORF FOUND")) {
						if (strand != 0) {
							if (strand < 0 && beginPos < 4) {
								sOrf.setBeginLocation(new SimplePosition(true, false, beginPos));
							}
							else {
								sOrf.setBeginLocation(new SimplePosition(beginPos));
							}
							if (strand > 0 && (endPos == (nextPos - 1))) {
								sOrf.setEndLocation(new SimplePosition(false, true, endPos));
							}
							else {
								sOrf.setEndLocation(new SimplePosition(endPos));
							}
							sOrf.save();
							progress.completeStep();
							countOrfs++;
						}
						if (countOrfs % stepORF == 0) {
							BioSql.restartTransaction();
						}
						countOrfsNotFound++;
						strand = 0;
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
					}
					else if (strand != 0) { //ORF sequence
						line2 = br.readLine();
						line3 = br.readLine();
						br.readLine(); // empty line
						if (strand < 0) {
							lineAmino = line3;
						}
						else {
							lineAmino = line;
						}
						lineAmino = lineAmino.substring(3, lineAmino.length() - 1);
						if (lineAmino.trim().length() != 0) {
							if (beginPos < 0) {
								beginPos = nextPos + firstPosNotSpace(lineAmino) - 1;
							}
							endPos = nextPos + lastPosNotSpace(lineAmino) + 1;
						}
						nextPos += lineAmino.length();
					}
				}
			}
			if (strand != 0) {
				if (strand < 0 && beginPos < 4) {
					sOrf.setBeginLocation(new SimplePosition(true, false, beginPos));
				}
				else {
					sOrf.setBeginLocation(new SimplePosition(beginPos));
				}
				if (strand > 0 && (endPos == (nextPos - 1))) {
					sOrf.setEndLocation(new SimplePosition(false, true, endPos));
				}
				else {
					sOrf.setEndLocation(new SimplePosition(endPos));
				}
				sOrf.save();
				progress.completeStep();
				countOrfs++;
			}
			BioSql.endTransactionOK();
			Object[] a1 = {countOrfs, countOrfsNotFound};
			progress.finish(MessageFormat.format(Messages.getString("LoadORFVR.finalMessage"), a1));
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
		catch (BioException e) {
			e.printStackTrace();
		}
		finally {
			BioSql.finish();
		}

	}

	public static int firstPosNotSpace(String str) {
		int i = 0;
		while (i < str.length() && str.charAt(i) == ' ') {
			i++;
		}
		return i;
	}

	public static int lastPosNotSpace(String str) {
		int i = str.length() - 1;
		while (i >= 0 && str.charAt(i) == ' ') {
			i--;
		}
		return i;
	}
}
