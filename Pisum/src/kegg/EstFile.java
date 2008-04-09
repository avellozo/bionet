/*
 * Created on 09/04/2008
 */
package kegg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.rpc.ServiceException;

public class EstFile
{

	Collection<Est>	ests;
	String			fileName;

	public EstFile(Collection<Est> ests, String fileName)
	{
		this.ests = ests;
		this.fileName = fileName;
	}

	public Collection<Est> getEsts()
	{
		return ests;
	}

	public void setEsts(Collection<Est> ests)
	{
		this.ests = ests;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public static EstFile loadFromFastaFile(File fileIn) throws IOException, ServiceException
	{
		ArrayList<Est> ests = new ArrayList<Est>();
		KOCollection kos = new KOCollection();
		Est est;
		KO ko;

		FileReader file = null;
		file = new FileReader(fileIn);
		BufferedReader buffer = new BufferedReader(file);

		// Each line of the file contains one relation between Est and KO
		String line = buffer.readLine();
		String[] estkoStr;
		String koId;
		while (line != null)
		{
			estkoStr = line.split("\t");
			est = new Est(estkoStr[0]);
			if (estkoStr.length > 1)
			{
				koId = estkoStr[1];
				ko = kos.getKo(koId);
				if (ko == null)
				{
					ko = new KO(koId);
					kos.add(ko);
				}
				est.setKo(ko);
			}
			ests.add(est);
			line = buffer.readLine();
		}
		file.close();
		kos.updateFromKegg();
		return new EstFile(ests, fileIn.getName());
	}
}
