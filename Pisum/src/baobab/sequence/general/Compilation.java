/*
 * Created on 06/06/2008
 */
package baobab.sequence.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.ontology.AlreadyExistsException;
import org.biojava.ontology.Term;
import org.biojava.utils.ChangeVetoException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.ontology.SimpleComparableOntology;
import org.hibernate.Session;
import org.hibernate.Transaction;

import baobab.sequence.exception.DBObjectAlreadyExists;
import baobab.sequence.ui.Progress;

public class Compilation
{
	public static final String	ONTO_PREFIX	= Messages.getString("Compilation.ontologyPrefix");

	Organism					organism;
	String						name;
	double						version;

	public Organism getOrganism()
	{
		return organism;
	}

	public String getName()
	{
		return name;
	}

	public double getVersion()
	{
		return version;
	}

	private Compilation(Organism organism, String name, double version)
	{
		this.organism = organism;
		this.name = name;
		this.version = version;
	}

	public static Compilation create(Organism organism, String name) throws DBObjectAlreadyExists
	{
		SimpleComparableOntology ont = (SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {ONTO_PREFIX + organism.getTaxon().getNCBITaxID()});
		Term term = ont.getTerm(name);
		if (term != null)
		{
			throw new DBObjectAlreadyExists(new Object[] {ont, name});
		}
		String version = "1";
		if (ont.getDescription() != null || ont.getDescription().length() != 0)
		{
			version = ont.getDescription();
		}
		try
		{
			ont.createTerm(name, version);
			ont.setDescription("" + (new Double(version) + 1));
		}
		catch (AlreadyExistsException e)
		{
			e.printStackTrace();
		}
		catch (ChangeVetoException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		return new Compilation(organism, name, new Double(version));
	}

	public static Compilation getOfDB(Organism organism, String name)
	{
		SimpleComparableOntology ont = (SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {ONTO_PREFIX + organism.getTaxon().getNCBITaxID()});
		Term term = ont.getTerm(name);
		if (term != null)
		{
			return new Compilation(organism, name, new Double(term.getDescription()));
		}
		return null;
	}

	public void LoadESTs(String fileFastaName, Session session, Progress progress)
	{
		Transaction tx = session.beginTransaction();
		try
		{
			RichObjectFactory.connectToBioSQL(session);
			// an input FASTA file
			BufferedReader fastaFileReader = new BufferedReader(new FileReader(fileFastaName));

			// the namespace to override that in the file
			RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
			Namespace ns = RichObjectFactory.getDefaultNamespace();

			// we are reading DNA sequences
			RichSequenceIterator seqs = RichSequence.IOTools.readFastaDNA(fastaFileReader, ns);
			RichSequence seq;
			progress.init();

			int i = 0;
			while (seqs.hasNext())
			{
				seq = seqs.nextRichSequence();
				seq.setTaxon(getOrganism().getTaxon());
				seq.setSeqVersion(getVersion());
				session.save("Sequence", seq);
				progress.completeStep();
				i++;
				if (i % 1000 == 0)
				{
					session.flush();
					session.clear();
					tx.commit();
					tx.begin();
				}
			}
			session.flush();
			session.clear();
			tx.commit();
			progress.finish();
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
}
