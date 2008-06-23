/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.util.List;
import java.util.Set;

import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.ComparableOntology;
import org.biojavax.ontology.ComparableTerm;
import org.biojavax.ontology.SimpleComparableOntology;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BioSql
{
	public static SessionFactory	sessionFactory	= new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	public static Session			session;
	static {
		init();
	}

	public static void init() {
		//		session = sessionFactory.openStatelessSession();
		session = sessionFactory.openSession();
		RichObjectFactory.connectToBioSQL(session);
		RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
		//		RichObjectFactory.setDefaultRichSequenceHandler(new BioSQLRichSequenceHandler(session));
		//		session.setFlushMode(FlushMode.ALWAYS);
	}

	public static NCBITaxon getTaxon(int ncbiTaxonNumber) {
		Query taxonsQuery = session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber");
		taxonsQuery.setInteger("ncbiTaxonNumber", ncbiTaxonNumber);
		List taxons = taxonsQuery.list();
		if (taxons.size() != 1) {
			return null;
		}
		return (NCBITaxon) taxons.get(0);
	}

	public static Sequence getSequence(Organism organism, String seqName) {
		Query query = session.createQuery("from Sequence where name=:seqName and taxon=:taxonId");
		query.setString("seqName", seqName);
		query.setParameter("taxonId", organism.getTaxon());
		List seqs = query.list();
		if (seqs.size() != 1) {
			return null;
		}
		RichSequence seq = (RichSequence) seqs.get(0);
		return new Sequence(seq, getCompilation(organism, seq.getSeqVersion()));
	}

	private static Compilation getCompilation(Organism organism, Double seqVersion) {
		ComparableOntology ont = TermsAndOntologies.getCompilationOnt(organism);
		Set<ComparableTerm> comps = ont.getTerms();
		for (ComparableTerm comp : comps) {
			if (Double.parseDouble(comp.getDescription()) == seqVersion) {
				return new Compilation(organism, comp.getName(), seqVersion);
			}
		}
		return null;
	}

	public static Gene getGene(String geneName, Organism organism) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b where "
			+ "f.name=:geneName and f.typeTerm=:geneTerm and b.taxon=:taxonId ");
		query.setString("geneName", geneName);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("geneTerm",
			((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
				new Object[] {Messages.getString("ontologyFeatures")})).getOrCreateTerm(Messages.getString("termGene")));
		List features = query.list();
		if (features.size() != 1) {
			return null;
		}
		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		feature.toString();
		return new Gene(feature);
	}

	public static Namespace getDefaultNamespace() {
		return RichObjectFactory.getDefaultNamespace();
	}

	public static void save(EST est) {
		session.saveOrUpdate("Sequence", est.getSequence());
	}

	public static void save(Sequence seq) {
		session.saveOrUpdate("Sequence", seq.getSequence());
	}

	public static void save(Gene gene) {
		session.saveOrUpdate("Sequence", gene.getFeature().getSequence());
		session.saveOrUpdate("Feature", gene.getFeature());
	}

	public static void restartTransaction() {
		restartTransaction(getTransaction());
	}

	public static Transaction restartTransaction(Transaction tx) {
		tx.commit();
		session.flush();
		session.clear();
		session.close();
		RichObjectFactory.clearLRUCache();
		//		sessionFactory.close();
		//		sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		init();
		return beginTransaction();
	}

	public static Transaction beginTransaction() {
		return session.beginTransaction();
	}

	public static Transaction getTransaction() {
		return beginTransaction();
	}

	public static void commit() {
		commit(getTransaction());
	}

	public static void commit(Transaction tx) {
		tx.commit();
	}

	public static void rollback() {
		rollback(getTransaction());
	}

	public static void rollback(Transaction tx) {
		tx.rollback();
	}

	public static void endTransactionOK() {
		endTransactionOK(getTransaction());
	}

	public static void endTransactionOK(Transaction tx) {
		session.flush();
		session.clear();
		tx.commit();
	}

	public static void finish() {
		finish(getTransaction());
	}

	public static void finish(Transaction tx) {
		if (tx.isActive()) {
			tx.rollback();
		}
		session.close();
	}

}
