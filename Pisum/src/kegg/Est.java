/*
 * Created on 02/04/2008
 */
package kegg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.rpc.ServiceException;

public class Est
{
	String	id;
	String	name;
	String	function;

	GO		go;

	EC		ec;

	KO		ko;

	public Est(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public KO getKo()
	{
		return ko;
	}

	public void setKo(KO ko)
	{
		this.ko = ko;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public GO getGo()
	{
		return go;
	}

	public void setGo(GO go)
	{
		this.go = go;
	}

	public EC getEc()
	{
		return ec;
	}

	public void setEc(EC ec)
	{
		this.ec = ec;
	}

	public String toString()
	{
		return getId();
	}

	public String getFunction()
	{
		return function;
	}

	public void setFunction(String function)
	{
		this.function = function;
	}

	public static Collection<Est> loadFromKoFile(File fileIn) throws IOException, ServiceException
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
		return ests;
	}
}
