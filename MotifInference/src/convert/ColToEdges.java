/*
 * Created on 02/02/2010
 */
package convert;

import general.EC;
import general.Reaction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class ColToEdges
{

	int	linesPrinted	= 0;

	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("usage:  java -cp motinf.jar general.ColToEdges <Col file name> <taxon id> <threshold> <{L,V,l,v}> <edges file name to create>");
			return;
		}
		PrintStream out = null;
		try {
			String fileName = args[0];
			String taxonID = args[1];
			int thre = (Integer.valueOf(args[2])).intValue();
			boolean considerSubsSubs = (args[3].toLowerCase().equals("v"));
			out = new PrintStream(new FileOutputStream(args[4], false));

			new ColToEdges().generate(fileName, taxonID, thre, considerSubsSubs, out);
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

	public void generate(String fileName, String taxonId, int thre, boolean considerSubsSubs, PrintStream out)
			throws IOException {

		List<Reaction> reactions = getReationsFromFile(fileName, considerSubsSubs, considerSubsSubs);

		out.println("#ID_A\tID_B\ttaxid_A\ttaxid_B\tColor_A\tColor_B");

		for (Reaction reaction : reactions) {
			Collection<Reaction> neighbors = reaction.getNeighbors();

			for (Reaction neighbor : neighbors) {
				printLine(out, reaction, neighbor, taxonId, thre);
				neighbor.removeNeighbor(reaction);
			}

		}
		System.out.println(linesPrinted);
	}

	protected void printLine(PrintStream out, Reaction reaction, Reaction neighbor, String taxonId, int thre) {
		String id1 = reaction.getID();
		String id2 = neighbor.getID();
		EC ec1 = reaction.getEc();
		EC ec2 = neighbor.getEc();
		String color1 = null;
		String color2 = null;
		if (ec1 != null) {
			color1 = ec1.getClasses(thre);
		}
		if (ec2 != null) {
			color2 = ec2.getClasses(thre);
		}

		printLine(out, id1, id2, taxonId, taxonId, color1, color2);
	}

	protected void printLine(PrintStream out, String id1, String id2, String taxonId1, String taxonId2, String color1,
			String color2) {
		if (id1 != null && id2 != null) {
			out.print(id1 + '\t');
			out.print(id2 + '\t');
			if (taxonId1 != null) {
				out.print(taxonId1);
			}
			else {
				out.print("-");
			}
			out.print('\t');
			if (taxonId2 != null) {
				out.print(taxonId2);
			}
			else {
				out.print("-");
			}

			out.print('\t');
			if (color1 != null) {
				out.print(color1);
			}
			else {
				out.print("-");
			}
			out.print('\t');
			if (color2 != null) {
				out.print(color2);
			}
			else {
				out.print("-");
			}
			//					}
			out.println();
			linesPrinted++;
			if (linesPrinted % 1000 == 0) {
				System.out.println(linesPrinted);
			}
		}
	}

	protected List<Reaction> getReationsFromFile(String fileName, boolean considerSubsSubs, boolean considerProdProd)
			throws IOException {
		return Reaction.getReationsFromFileTab(fileName, considerSubsSubs, considerProdProd);
	}

}
