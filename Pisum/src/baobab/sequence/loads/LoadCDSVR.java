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

public class LoadCDSVR
{
	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args) {
		Organism organism;
		Transaction tx = BioSql.beginTransaction();
		try {
			//file cdss
			File fileCDSs;
			if (args.length > 0) {
				fileCDSs = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose CDS file ");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileCDSs = fc.getSelectedFile();
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

			String cdsName, geneName;
			BufferedReader br = new BufferedReader(new FileReader(fileCDSs));
			String line, line2, line3, lineAmino;
			//			String lineDNA, lineCodes;
			SimpleCDS sCDS = null;
			int countCDSs = 0;
			int beginPos = -1, endPos = -1, nextPos = -1;
			int strand = 0;
			int countCDSsNotFound = 0;
			Object[] a = {fileCDSs.getPath()};
			String msgInitial = MessageFormat.format(Messages.getString("LoadCDSVR.initialMessage"), a);
			int stepCDS = Integer.parseInt(Messages.getString("LoadCDSVR.printCDS"));
			Progress progress = new ProgressPrintInterval(System.out, stepCDS, msgInitial);
			progress.init();
			System.out.print("Start time:");
			System.out.println(new Date());

			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if (line.startsWith(">")) { //CDS heading
						//new CDS
						//save last CDS
						if (strand != 0) {
							if (strand < 0 && beginPos < 4) {
								sCDS.setBeginLocation(new SimplePosition(true, false, beginPos));
							}
							else {
								sCDS.setBeginLocation(new SimplePosition(beginPos));
							}
							if (strand > 0 && (endPos == (nextPos - 1))) {
								sCDS.setEndLocation(new SimplePosition(false, true, endPos));
							}
							else {
								sCDS.setEndLocation(new SimplePosition(endPos));
							}
							sCDS.save();
							progress.completeStep();
							countCDSs++;
						}
						if (countCDSs % stepCDS == 0) {
							BioSql.restartTransaction();
						}
						cdsName = line.substring(1);
						geneName = cdsName.substring(0, cdsName.indexOf("_"));
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
								sCDS = new SimpleCDS(organism, geneName, cdsName, TermsAndOntologies.getTermVR());
								sCDS.setStrand(strand);
							}
						}
						else {
							System.out.println("Format error in line 'Reading frame':" + line);
							strand = 0;
						}
						br.readLine(); // empty line
					}
					else if (line.startsWith("NO CDS FOUND")) {
						if (strand != 0) {
							if (strand < 0 && beginPos < 4) {
								sCDS.setBeginLocation(new SimplePosition(true, false, beginPos));
							}
							else {
								sCDS.setBeginLocation(new SimplePosition(beginPos));
							}
							if (strand > 0 && (endPos == (nextPos - 1))) {
								sCDS.setEndLocation(new SimplePosition(false, true, endPos));
							}
							else {
								sCDS.setEndLocation(new SimplePosition(endPos));
							}
							sCDS.save();
							progress.completeStep();
							countCDSs++;
						}
						if (countCDSs % stepCDS == 0) {
							BioSql.restartTransaction();
						}
						countCDSsNotFound++;
						strand = 0;
						br.readLine();
						br.readLine();
						br.readLine();
						br.readLine();
					}
					else if (strand != 0) { //CDS sequence
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
					sCDS.setBeginLocation(new SimplePosition(true, false, beginPos));
				}
				else {
					sCDS.setBeginLocation(new SimplePosition(beginPos));
				}
				if (strand > 0 && (endPos == (nextPos - 1))) {
					sCDS.setEndLocation(new SimplePosition(false, true, endPos));
				}
				else {
					sCDS.setEndLocation(new SimplePosition(endPos));
				}
				sCDS.save();
				progress.completeStep();
				countCDSs++;
			}
			BioSql.endTransactionOK();
			Object[] a1 = {countCDSs, countCDSsNotFound};
			progress.finish(MessageFormat.format(Messages.getString("LoadCDSVR.finalMessage"), a1));
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
