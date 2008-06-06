/*
 * Created on 05/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.BioException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.bio.taxa.NCBITaxon;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import baobab.sequence.general.Compilation;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;

public class LoadESTs
{

	/**
	 * @param args args[0] =
	 */
	public static void main(String[] args)
	{
		// load Hibernate config
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory(); //$NON-NLS-1$
		// open the session
		Session session = sessionFactory.openSession();
		// connect it to BioJavaX
		Transaction tx = session.beginTransaction();
		int i = 0;
		try
		{
			RichObjectFactory.connectToBioSQL(session);
			// an input FASTA file
			BufferedReader fastaFileReader;

			if (args.length > 0)
			{
				fastaFileReader = new BufferedReader(new FileReader(args[0]));
			}
			else
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose FASTA file");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				fastaFileReader = new BufferedReader(new FileReader(fc.getSelectedFile()));
			}

			// the namespace to override that in the file
			RichObjectFactory.setDefaultNamespaceName(Messages.getString("LoadESTs.nameSpace"));
			Namespace ns = RichObjectFactory.getDefaultNamespace();

			// the ncbiTaxon of organism
			int ncbiTaxonNumber;
			String respDialog;
			if (args.length < 2)
			{
				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
					Messages.getString("LoadESTs.ncbiTaxonNumberDefault"));
				if (respDialog == null)
				{
					return;
				}
				ncbiTaxonNumber = Integer.parseInt(respDialog);
			}
			else
			{
				ncbiTaxonNumber = Integer.parseInt(args[1]);
			}
			Query taxonsQuery = session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber");
			taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
			List taxons = taxonsQuery.list();
			while (taxons.size() != 1)
			{
				respDialog = JOptionPane.showInputDialog("NCBITaxonID not found. Please input the NCBI_Taxon_ID:",
					ncbiTaxonNumber);
				if (respDialog == null)
				{
					return;
				}
				ncbiTaxonNumber = Integer.parseInt(respDialog);
				taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
				taxons = taxonsQuery.list();
			}
			NCBITaxon taxon = (NCBITaxon) taxons.get(0);

			// the version
			Double version;
			if (args.length < 3)
			{
				respDialog = JOptionPane.showInputDialog("Please input the version of the ESTs:", "1");
				if (respDialog == null)
				{
					return;
				}
				version = new Double(respDialog);
			}
			else
			{
				version = new Double(args[2]);
			}

			// we are reading DNA sequences
			RichSequenceIterator seqs = RichSequence.IOTools.readFastaDNA(fastaFileReader, ns);
			RichSequence seq;

			while (seqs.hasNext())
			{
				seq = seqs.nextRichSequence();
				seq.setTaxon(taxon);
				seq.setSeqVersion(version);
				seq.ge
				session.saveOrUpdate("Sequence", seq);
				session.flush();
				session.clear();
				tx.commit();
				tx.begin();
				i++;
				if (i % 1000 == 0)
				{
					session.flush();
					session.clear();
					tx.commit();
					tx.begin();
					System.out.println("EST:" + i);
				}
			}
			session.flush();
			session.clear();
			tx.commit();
			System.out.println("EST:" + i);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchElementException e)
		{
			e.printStackTrace();
		}
		catch (BioException e)
		{
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
	}

	public LoadESTs(String fileFastaName, int taxonNumber, String compilationName)
	{
		this(fileFastaName, taxonNumber, getVersion(compilationName));
	}

	private static int getVersion(String compilationName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public LoadESTs(String fileFastaName, Organism organism, Compilation compilation)
	{

	}

}
