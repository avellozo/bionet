/*
 * Created on 19/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojavax.RichObjectFactory;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.SimpleComparableOntology;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import baobab.sequence.general.Messages;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class Test
{
	public static SessionFactory	sessionFactory	= new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	public static Session			session;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		Transaction tx = session.beginTransaction();

		try {
			//file .ko
			File fileEstKo;
			if (args.length > 0) {
				fileEstKo = new File(args[0]);
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose EST2KO file (.ko)");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileEstKo = fc.getSelectedFile();
			}

			// the ncbiTaxon of organism
			String respDialog;
			int ncbiTaxonNumber;
			NCBITaxon org;
			Query taxonsQuery;
			taxonsQuery = session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber");
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
					Messages.getString("LoadESTToKo.ncbiTaxonNumberDefault"));
				if (respDialog == null) {
					return;
				}
				ncbiTaxonNumber = Integer.parseInt(respDialog);
				taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
				if (taxonsQuery.list().size() == 0) {
					org = null;
				}
				else {
					org = (NCBITaxon) taxonsQuery.list().get(0);
				}
				while (org == null) {
					respDialog = JOptionPane.showInputDialog("NCBITaxonID not found. Please input the NCBI_Taxon_ID:",
						ncbiTaxonNumber);
					if (respDialog == null) {
						return;
					}
					ncbiTaxonNumber = Integer.parseInt(respDialog);
					taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
					if (taxonsQuery.list().size() == 0) {
						org = null;
					}
					else {
						org = (NCBITaxon) taxonsQuery.list().get(0);
					}
				}
			}
			else {
				ncbiTaxonNumber = Integer.parseInt(args[1]);
				taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
				if (taxonsQuery.list().size() == 0) {
					System.out.println("NCBITaxonID not found. Please input the correct NCBI_Taxon_ID.");
					return;
				}
				else {
					org = (NCBITaxon) taxonsQuery.list().get(0);
				}
			}

			//Method
			ComparableTerm method;
			if (args.length < 2) {
				respDialog = JOptionPane.showInputDialog("Please input the name of the method:");
				if (respDialog == null) {
					return;
				}
				method = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
					new Object[] {Messages.getString("Gene.ontologyToKo")})).getOrCreateTerm(respDialog);
			}
			else {
				method = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
					new Object[] {Messages.getString("Gene.ontologyToKo")})).getOrCreateTerm(args[2]);
			}

			String estName, koId;
			BufferedReader br = new BufferedReader(new FileReader(fileEstKo));
			String line;
			String[] splits;
			int linkCounter = 0, linkErrorGene = 0;
			Progress progress = new ProgressPrintInterval(System.out,
				Integer.parseInt(Messages.getString("LoadEstToKo.printEstKo")),
				Messages.getString("LoadEstToKo.initialMessage") + fileEstKo.getPath());
			progress.init();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					progress.completeStep();
					splits = line.split("\t");
					if (splits.length == 2) {
						estName = splits[0];
						koId = splits[1];
						SimpleRichFeature gene = getGene(estName, org, session);
						ComparableTerm ko = ((SimpleComparableOntology) RichObjectFactory.getObject(
							SimpleComparableOntology.class, new Object[] {Messages.getString("KO.ontology")})).getOrCreateTerm(koId);
						if (gene == null) {
							linkErrorGene++;
							System.out.println("Gene not found:" + estName);
						}
						else {
							link2KO(gene, ko, method);
							//								BioSql.save(gene);
							linkCounter++;
							if (linkCounter % 10 == 0) {
								tx.commit();
								session.flush();
								session.clear();
								session.close();
								RichObjectFactory.clearLRUCache();
								//		sessionFactory.close();
								//		sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
								init();
								tx = session.beginTransaction();
								System.out.println("OK restart");
							}
						}
					}
				}
			}
			tx.commit();
			session.flush();
			session.clear();
			session.close();
			//				BioSql.endTransactionOK();
			progress.finish(linkCounter + Messages.getString("LoadEstToKo.finalMessage") + ", gene:" + linkErrorGene);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (BioException e) {
			e.printStackTrace();
		}
		finally {
			//				BioSql.finish();
		}

		//			ComparableTerm method = ((SimpleComparableOntology) RichObjectFactory.getObject(
		//				SimpleComparableOntology.class, new Object[] {Messages.getString("Gene.ontologyToKo")})).getOrCreateTerm("test");
		//			String geneName = "APF00001";
		//			SimpleRichFeature gene = getGene(geneName, org, session);
		//			ComparableTerm ko = ((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
		//				new Object[] {Messages.getString("KO.ontology")})).getOrCreateTerm("K07830");
		//			link2KO(gene, ko, method);
		//			tx.commit();
		//			session.flush();
		//			session.clear();
		//			session.close();
		//			RichObjectFactory.clearLRUCache();
		//			//		sessionFactory.close();
		//			//		sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		//			init();
		//			tx = session.beginTransaction();
		//
		//			geneName = "APF00013";
		//			ComparableTerm ko1 = ((SimpleComparableOntology) RichObjectFactory.getObject(
		//				SimpleComparableOntology.class, new Object[] {Messages.getString("KO.ontology")})).getOrCreateTerm("K04409");
		//			link2KO(gene, ko1, method);
		//
		//			tx.commit();
		//			session.flush();
		//			session.clear();
		//			session.close();
		//			RichObjectFactory.clearLRUCache();
		//			//		sessionFactory.close();
		//			//		sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		//			init();
		//			tx = session.beginTransaction();

	}

	public static SimpleRichFeature getGene(String geneName, NCBITaxon organism, Session session) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b where "
			+ "f.name=:geneName and f.typeTerm=:geneTerm and b.taxon=:taxonId ");
		query.setString("geneName", geneName);
		query.setParameter("taxonId", organism);
		query.setParameter("geneTerm",
			((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
				new Object[] {Messages.getString("ontologyFeatures")})).getOrCreateTerm(Messages.getString("termGene")));
		List features = query.list();
		if (features.size() != 1) {
			return null;
		}
		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		feature.toString();
		return feature;
	}

	public static SimpleRichFeature link2KO(SimpleRichFeature gene, ComparableTerm ko, ComparableTerm method)
			throws BioException {
		Feature.Template ft = new RichFeature.Template();
		ft.location = gene.getLocation().translate(0);
		ft.sourceTerm = ko;
		ft.typeTerm = method;
		ft.annotation = new SimpleRichAnnotation();
		SimpleRichFeature newFeature = (SimpleRichFeature) gene.createFeature(ft);
		newFeature.setName(ko.getName());
		newFeature.setRank(0);
		return newFeature;
	}

	public static void init() {
		session = sessionFactory.openSession();
		RichObjectFactory.connectToBioSQL(session);
		RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
		//		session.setFlushMode(FlushMode.ALWAYS);
	}

}
