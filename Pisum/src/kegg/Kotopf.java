/*
 * Created on 02/04/2008
 */
package kegg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.xml.rpc.ServiceException;

public class Kotopf
{
	private static final ResourceBundle	MESSAGES	= ResourceBundle.getBundle("kegg.messages");
	private static final ResourceBundle	CONFIG		= ResourceBundle.getBundle("kegg.config");

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
			fileOut = new File(fileIn, fileIn.getName() + ".pf"); //$NON-NLS-1$
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
				fileOut = new File(args[0] + ".pf"); //$NON-NLS-1$
			}
			else
			{
				fileOut = new File(args[1]);
			}
		}
		try
		{
			String[] strs = CONFIG.getString("Kotopf.organisms").split(";");
			Organism[] orgs = new Organism[strs.length];
			for (int i = 0; i < strs.length; i++)
			{
				orgs[i] = new Organism(strs[i]);
			}
			Collection<Est> ests = Est.loadFromKoFile(fileIn);

			pfFile = new FileWriter(fileOut);
			PrintStream bufWriter = new PrintStream(fileOut);

			String name, function, ec, go;
			KO ko;
			String koId;
			Collection<Gene> genes;
			String[] synonyms;
			for (Est est : ests)
			{
				estCount++;
				name = est.getName();
				function = est.getFunction();
				ko = est.getKo();
				synonyms = null;
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
					ko.loadGenes(orgs);
					genes = ko.getGenes();
					synonyms = new String[genes.size()];
					int i = 0;
					for (Gene gene : genes)
					{
						synonyms[i] = gene.getOrg().getId() + ":" + gene.getId() + "(" + gene.getName() + ")";
						i++;
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
						name = MESSAGES.getString("Kotopf.EstPrefixName_KONull") + est.getId(); //$NON-NLS-1$
					}
					else if (ko.getDefinition() == null || ko.getDefinition().length() == 0)
					{
						name = MESSAGES.getString("Kotopf.EstPrefixName_KODefNull") + koId; //$NON-NLS-1$
					}
					else
					{
						name = MESSAGES.getString("Kotopf.EstPrefixName") + ko.getDefinition(); //$NON-NLS-1$
					}
				}
				if (function == null)
				{
					if (ko == null)
					{
						function = MESSAGES.getString("Kotopf.FunctionName_KoNull"); //$NON-NLS-1$
					}
					else if (ko.getDefinition() == null || ko.getDefinition().length() == 0)
					{
						function = MESSAGES.getString("Kotopf.EstPrefixFunctionName_KODefNull") + koId; //$NON-NLS-1$
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

				bufWriter.println("ID" + "\t\t" + est.getId()); //$NON-NLS-1$ //$NON-NLS-2$
				bufWriter.println("NAME" + "\t\t" + name); //$NON-NLS-1$ //$NON-NLS-2$
				bufWriter.println("STARTBASE" + "\t" + 1); //$NON-NLS-1$ //$NON-NLS-2$
				bufWriter.println("ENDBASE" + "\t\t" + 2); //$NON-NLS-1$ //$NON-NLS-2$
				bufWriter.println("PRODUCT-TYPE" + "\t" + "P"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (synonyms != null)
				{
					for (String syn : synonyms)
					{
						bufWriter.println("SYNONYM" + "\t\t" + syn); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				if (go != null && go.length() > 0)
				{
					bufWriter.println("DBLINK" + "\t\t" + "GO:" + go); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				if (koId != null && koId.length() > 0)
				{
					bufWriter.println("DBLINK" + "\t\t" + "KO:" + koId); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				bufWriter.println("FUNCTION" + "\t" + function); //$NON-NLS-1$ //$NON-NLS-2$
				if (ec != null && ec.length() > 0)
				{
					bufWriter.println("EC" + "\t\t" + ec); //$NON-NLS-1$ //$NON-NLS-2$
				}
				bufWriter.println();
				bufWriter.println("//"); //$NON-NLS-1$
			}
			bufWriter.flush();

			bufWriter.println(MESSAGES.getString("Kotopf.29") + fileIn.getName()); //$NON-NLS-1$
			bufWriter.println(MESSAGES.getString("Kotopf.30") + estCount); //$NON-NLS-1$
			bufWriter.println(MESSAGES.getString("Kotopf.31") + estKoCount); //$NON-NLS-1$
			bufWriter.println(MESSAGES.getString("Kotopf.32") + estKoDefCount); //$NON-NLS-1$
			bufWriter.println(MESSAGES.getString("Kotopf.33") + estKoDefECCount); //$NON-NLS-1$
			bufWriter.println(MESSAGES.getString("Kotopf.34") + estKoECCount); //$NON-NLS-1$

			bufWriter.flush();

			System.out.println(MESSAGES.getString("Kotopf.29") + fileIn.getName()); //$NON-NLS-1$
			System.out.println(MESSAGES.getString("Kotopf.30") + estCount); //$NON-NLS-1$
			System.out.println(MESSAGES.getString("Kotopf.31") + estKoCount); //$NON-NLS-1$
			System.out.println(MESSAGES.getString("Kotopf.32") + estKoDefCount); //$NON-NLS-1$
			System.out.println(MESSAGES.getString("Kotopf.33") + estKoDefECCount); //$NON-NLS-1$
			System.out.println(MESSAGES.getString("Kotopf.34") + estKoECCount); //$NON-NLS-1$

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
