/*
 * Created on 02/02/2010
 */
package convert;

import general.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

public class NetAndColorsToEdges
{

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("usage:  java -cp motinf.jar general.NetAndColorsToEdges <network(ID-ID) file name> <tax id> <edges file name to create> <ID-Color file name>");
			return;
		}
		PrintStream out = null;
		try {
			int i = 0;
			BufferedReader netFile = new BufferedReader(new FileReader(args[0]));
			out = new PrintStream(new FileOutputStream(args[2], false));
			out.println("#ID_A\tID_B\ttaxid_A\ttaxid_B\tColor_A\tColor_B");
			String line;
			String[] columnValues;
			Hashtable<String, Color> idsColor = new Hashtable<String, Color>();
			if (args.length > 3) {
				idsColor = new Hashtable<String, Color>(2600000, 0.99f);
				BufferedReader idColorFile = new BufferedReader(new FileReader(args[3]));
				Hashtable<String, Color> colors = new Hashtable<String, Color>(2000000);
				while ((line = idColorFile.readLine()) != null) {
					if (line.startsWith("#")) {
						continue;
					}
					columnValues = line.split("\t");
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

			String id1, id2, tax1, tax2;
			Color color1, color2;
			while ((line = netFile.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				columnValues = line.split("\t");

				id1 = columnValues[0].trim();
				id2 = columnValues[1].trim();

				tax1 = args[1];
				tax2 = args[1];

				if (id1 != null && id2 != null) {
					out.print(id1 + '\t');
					out.print(id2 + '\t');
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

					color1 = idsColor.get(id1);
					color2 = idsColor.get(id2);

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

}
