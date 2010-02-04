/*
 * Created on 02/02/2010
 */
package general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

public class Mitab25ToEdges
{

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("usage:  java -jar motinf.jar general.Intact2Edges <Mitab file name> <edges file name to create> <ID-Color file name>");
			return;
		}
		PrintStream out = null;
		try {
			int i = 0;
			BufferedReader intactFile = new BufferedReader(new FileReader(args[0]));
			out = new PrintStream(new FileOutputStream(args[1], false));
			out.println("#ID_A\tID_B\ttaxid_A\ttaxid_B\tColor_A\tColor_B");
			String line;
			String[] columnValues;
			Hashtable<String, Color> idsColor = new Hashtable<String, Color>();
			if (args.length > 2) {
				idsColor = new Hashtable<String, Color>(2600000, 0.99f);
				BufferedReader idColorFile = new BufferedReader(new FileReader(args[2]));
				Hashtable<String, Color> colors = new Hashtable<String, Color>(2000000);
				while ((line = idColorFile.readLine()) != null) {
					if (line.startsWith("#")) {
						continue;
					}
					columnValues = line.split("  ");
					if (columnValues.length == 2) {
						String idStr = columnValues[0];
						String colorStr = columnValues[1];
						Color color = colors.get(colorStr);
						if (color == null) {
							color = new Color(colorStr);
							colors.put(colorStr, color);
						}
						idsColor.put(idStr, color);
					}
				}
			}

			String uniprot1, uniprot2, tax1, tax2;
			Color color1, color2;
			while ((line = intactFile.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				columnValues = line.split("\t");

				uniprot1 = getUniprotkb(columnValues[0].trim().split("\\|"));
				uniprot2 = getUniprotkb(columnValues[1].trim().split("\\|"));

				tax1 = getTaxid(columnValues[9]);
				tax2 = getTaxid(columnValues[10]);

				if (uniprot1 != null && uniprot2 != null) {
					out.print(uniprot1 + '\t');
					out.print(uniprot2 + '\t');
					if (tax1 != null) {
						out.print(tax1);
					}
					else {
						out.print("-");
					}
					out.print('\t');
					if (tax2 != null) {
						out.print(tax2);
					}
					else {
						out.print("-");
					}

					color1 = idsColor.get(uniprot1);
					color2 = idsColor.get(uniprot2);

					//					if (color1 != null || color2 != null) {
					out.print('\t');
					if (color1 != null) {
						out.print(color1.getDescription());
					}
					else {
						out.print("-");
					}
					out.print('\t');
					if (color2 != null) {
						out.print(color2.getDescription());
					}
					else {
						out.print("-");
					}
					//					}
					out.println();
					i++;
					if (i % 1000 == 0) {
						System.out.println(i);
					}
				}
			}
			System.out.println(i);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return;
	}

	private static String getUniprotkb(String[] dbxrefValues) {
		for (String dbxref : dbxrefValues) {
			if (dbxref.startsWith("uniprotkb:")) {
				return dbxref.substring(10);
			}
		}
		return null;
	}

	private static String getTaxid(String taxidStr) {
		taxidStr = taxidStr.trim();
		int i = taxidStr.indexOf('(');
		if (i == -1) {
			i = taxidStr.length();
		}
		if (!taxidStr.startsWith("taxid:") || i < 7) {
			return "-3";
		}
		return taxidStr.substring(6, i);
	}

}
