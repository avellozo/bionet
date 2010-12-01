/*
 * Created on 02/02/2010
 */
package convert;

import general.Reaction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class SBMLToEdges extends ColToEdges
{

	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("usage:  java -cp motinf.jar general.SBMLToEdges <SBML file name> <taxon id> <threshold> <{L,V,l,v}> <edges file name to create>");
			return;
		}
		PrintStream out = null;
		try {
			String fileName = args[0];
			String taxonID = args[1];
			int thre = (Integer.valueOf(args[2])).intValue();
			boolean considerSubsSubs = (args[3].toLowerCase().equals("v"));
			out = new PrintStream(new FileOutputStream(args[4], false));

			new SBMLToEdges().generate(fileName, taxonID, thre, considerSubsSubs, out);
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

	@Override
	protected List<Reaction> getReationsFromFile(String fileName, boolean considerSubsSubs, boolean considerProdProd)
			throws IOException {
		try {
			return Reaction.getReationsFromFileSBML(fileName, considerSubsSubs, considerProdProd);
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
	}

}
