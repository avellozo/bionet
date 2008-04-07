/*
 * Created on 02/04/2008
 */
package kegg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.xml.rpc.ServiceException;

public class Kotopf
{
	/**
	 * @param args args[0] = ko file (source) args[1] = pf file (target)
	 */
	public static void main(String[] args)
	{
		File fileIn, fileOut;
		FileWriter pfFile;
		int estCount = 0, estKoCount = 0, estKoDefCount = 0, estKoECCount = 0, estKoDefECCount = 0;
		if (args.length == 0)
		{
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
			{
				System.exit(0);
			}
			fileIn = fc.getSelectedFile();
			fileOut = new File(fileIn, fileIn.getName() + ".pf");
			fc.setSelectedFile(fileOut);
			returnVal = fc.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
			{
				System.exit(0);
			}
			fileOut = fc.getSelectedFile();
			//			System.out.println("Usage: java Kotopf <ko file> <pf file>");
		}
		else
		{
			fileIn = new File(args[0]);
			if (args.length < 2)
			{
				fileOut = new File(args[0] + ".pf");
			}
			else
			{
				fileOut = new File(args[1]);
			}
		}
		try
		{
			Collection<Est> ests = Est.loadFromKoFile(fileIn);
			pfFile = new FileWriter(fileOut);
			PrintStream bufWriter = new PrintStream(fileOut);

			String name, function, ec, go;
			KO ko;
			String koId;
			for (Est est : ests)
			{
				estCount++;
				name = est.getName();
				function = est.getFunction();
				ko = est.getKo();
				if (ko != null)
				{
					koId = ko.getId();
					estKoCount++;
					if (ko.getDefinition() != null && ko.getDefinition().length() > 0)
					{
						estKoDefCount++;
						if (ko.ec != null)
						{
							estKoDefECCount++;
						}
					}
					if (ko.ec != null)
					{
						estKoECCount++;
					}
				}
				else
				{
					koId = null;
				}
				if (name == null)
				{
					if (ko == null)
					{
						name = "Undefined_" + est.getId();
					}
					else if (ko.getDefinition() == null || ko.getDefinition().length() == 0)
					{
						name = "Undefined_" + koId;
					}
					else
					{
						name = "Putative_" + ko.getDefinition();
					}
				}
				if (function == null)
				{
					if (ko == null)
					{
						function = "ORF";
					}
					else if (ko.getDefinition() == null || ko.getDefinition().length() == 0)
					{
						function = "Undefined_" + koId;
					}
					else
					{
						function = ko.getDefinition();
					}
				}

				if (est.getEc() != null)
				{
					ec = est.getEc().getId();
				}
				else if (ko != null && ko.getEc() != null)
				{
					ec = ko.getEc().getId();
				}
				else
				{
					ec = null;
				}

				if (est.getGo() != null)
				{
					go = est.getGo().getId();
				}
				else if (ko != null && ko.getGo() != null)
				{
					go = ko.getGo().getId();
				}
				else
				{
					go = null;
				}

				bufWriter.println("ID" + "\t\t" + est.getId());
				bufWriter.println("NAME" + "\t\t" + name);
				bufWriter.println("STARTBASE" + "\t" + 1);
				bufWriter.println("ENDBASE" + "\t\t" + 2);
				bufWriter.println("PRODUCT-TYPE" + "\t" + "P");
				if (go != null && go.length() > 0)
				{
					bufWriter.println("DBLINK" + "\t\t" + "GO:" + go);
				}
				if (koId != null && koId.length() > 0)
				{
					bufWriter.println("DBLINK" + "\t\t" + "KO:" + koId);
				}
				bufWriter.println("FUNCTION" + "\t" + function);
				if (ec != null && ec.length() > 0)
				{
					bufWriter.println("EC" + "\t\t" + ec);
				}
				bufWriter.println();
				bufWriter.println("//");
			}
			bufWriter.flush();

			bufWriter.println(";; File:" + fileIn.getName());
			bufWriter.println(";; ESTs:" + estCount);
			bufWriter.println(";; ESTs with KO:" + estKoCount);
			bufWriter.println(";; ESTs with KO and definition of KO:" + estKoDefCount);
			bufWriter.println(";; ESTs with KO, definition of KO and EC:" + estKoDefECCount);
			bufWriter.println(";; ESTs with KO and EC:" + estKoECCount);

			bufWriter.flush();

			System.out.println("File:" + fileIn.getName());
			System.out.println(estCount + " ESTs");
			System.out.println(estKoCount + " ESTs with KO");
			System.out.println(estKoDefCount + " ESTs with KO and definition of KO");
			System.out.println(estKoDefECCount + " ESTs with KO, definition of KO and EC");
			System.out.println(estKoECCount + " ESTs with KO and EC");

			pfFile.flush();
			pfFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}
}
