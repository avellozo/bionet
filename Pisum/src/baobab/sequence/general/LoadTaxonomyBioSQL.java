/*
 * Created on 02/06/2008
 */
package baobab.sequence.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.biojava.bio.seq.io.ParseException;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.bio.taxa.io.NCBITaxonomyLoader;
import org.biojavax.bio.taxa.io.SimpleNCBITaxonomyLoader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class LoadTaxonomyBioSQL
{

	// command to run: java LoadTaxonomyBioSQL <nodes.dmp file> <names.dmp file>
	public static void main(String[] args)
	{
		// load Hibernate config
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		// open the session
		Session session = sessionFactory.openSession();
		// connect it to BioJavaX
		RichObjectFactory.connectToBioSQL(session);
		Transaction tx = session.beginTransaction();
		NCBITaxonomyLoader l = new SimpleNCBITaxonomyLoader();
		BufferedReader nodes;
		BufferedReader names;
		String fileNodes = "nodes.dmp";
		String fileNames = "names.dmp";
		if (args.length > 1)
		{
			fileNodes = args[0];
			fileNames = args[1];
		}
		try
		{
			nodes = new BufferedReader(new FileReader(fileNodes));
			names = new BufferedReader(new FileReader(fileNames));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		NCBITaxon t;
		try
		{
			int i = 0;
			while ((t = l.readNode(nodes)) != null)
			{
				if (i % 1000 == 0)
				{
					session.flush();
					session.clear();
					tx.commit();
					tx.begin();
					System.out.println("Node:" + i);
				}
				i++;
			}
			i = 0;
			while ((t = l.readName(names)) != null)
			{
				if (i % 1000 == 0)
				{
					session.flush();
					session.clear();
					tx.commit();
					tx.begin();
					System.out.println("Name:" + i);
				}
				i++;
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		session.flush();
		session.clear();
		tx.commit();
		session.close();
	}
}
