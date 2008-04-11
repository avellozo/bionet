/*
 * Created on 10/04/2008
 */
package bionet.general;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.xml.rpc.ServiceException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

public class FastatoGene
{

	private static final ResourceBundle	MESSAGES	= ResourceBundle.getBundle("kegg.messages");
	private static final ResourceBundle	CONFIG		= ResourceBundle.getBundle("kegg.config");

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ObjectContainer db = Db4o.openFile(CONFIG.getString("dbfile"));
		try
		{
			Organism org = Organism.getOrCreate(args[1], db);
			Compilation comp = Compilation.getOrCreate(args[2], org, db);
			Gene.loadFromFastaFile(new File(args[0]), comp, db);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
		finally
		{
			db.close();
		}
	}

}
