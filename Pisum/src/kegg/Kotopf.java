/*
 * Created on 02/04/2008
 */
package kegg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import javax.xml.rpc.ServiceException;

public class Kotopf
{
	/**
	 * @param args args[0] = ko file (source) args[1] = pf file (target)
	 */
	public static void main(String[] args)
	{

		if (args.length < 2)
		{
			System.out.println("Usage: java Kotopf <ko file> <pf file>");
		}
		else
		{
			try
			{
				Collection<Est> ests = Est.loadFromKoFile(args[0]);
				FileWriter pfFile = new FileWriter(args[1]);
				BufferedWriter bufWriter = new BufferedWriter(pfFile);

				String name, function, ec, go;
				KO ko;
				String koId;
				for (Est est : ests)
				{
					name = est.getName();
					function = est.getFunction();
					ko = est.getKo();
					if (ko != null)
					{
						koId = ko.getId();
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

					bufWriter.write("ID" + "\t\t" + est.getId());
					bufWriter.newLine();
					bufWriter.write("NAME" + "\t\t" + name);
					bufWriter.newLine();
					bufWriter.write("STARTBASE" + "\t" + 1);
					bufWriter.newLine();
					bufWriter.write("ENDBASE" + "\t\t" + 2);
					bufWriter.newLine();
					bufWriter.write("PRODUCT-TYPE" + "\t" + "P");
					bufWriter.newLine();
					if (go != null && go.length() > 0)
					{
						bufWriter.write("DBLINK" + "\t\t" + "GO:" + go);
						bufWriter.newLine();
					}
					if (koId != null && koId.length() > 0)
					{
						bufWriter.write("DBLINK" + "\t\t" + "KO:" + koId);
						bufWriter.newLine();
					}
					bufWriter.write("FUNCTION" + "\t" + function);
					bufWriter.newLine();
					if (ec != null && ec.length() > 0)
					{
						bufWriter.write("EC" + "\t\t" + ec);
						bufWriter.newLine();
					}
					bufWriter.newLine();
					bufWriter.write("//");
					bufWriter.newLine();
				}
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
}
