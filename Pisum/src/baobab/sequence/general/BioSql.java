/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.biojava.bio.BioException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichFeatureRelationship;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.seq.SimpleRichFeatureRelationship;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.ComparableOntology;
import org.biojavax.ontology.ComparableTerm;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import baobab.sequence.ui.Progress;

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
		return new Sequence(seq, getCompilation(organism, seq.getVersion()));
	}

	public static Collection<Integer> getSequencesIdCDStRNAmRNA(Organism organism, int version) {
		Query query = session.createQuery("select distinct(b.id) from Feature as f join f.parent as b where "
			+ "b.version=:version and b.taxon=:taxonId and (f.typeTerm=:typeCDS or f.typeTerm=:typeMiscRNA or f.typeTerm=:typetRNA)");
		query.setInteger("version", version);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("typeCDS", TermsAndOntologies.getTermCDS());
		query.setParameter("typeMiscRNA", TermsAndOntologies.getTermMiscRNA());
		query.setParameter("typetRNA", TermsAndOntologies.getTermTRNA());
		return (Collection<Integer>) query.list();
	}

	public static Collection<Integer> getSequencesId(Organism organism, int version) {
		Query query = session.createQuery("select id from ThinSequence where version=:version and taxon=:taxonId");
		query.setInteger("version", version);
		query.setParameter("taxonId", organism.getTaxon());
		return (Collection<Integer>) query.list();
	}

	public static RichSequence getSequence(Integer id) {
		Query query = session.createQuery("from ThinSequence where id=:id");
		query.setInteger("id", id);
		return (RichSequence) query.uniqueResult();
	}

	public static Compilation getCompilation(Organism organism, int seqVersion) {
		ComparableOntology ont = TermsAndOntologies.getCompilationOnt(organism);
		Set<ComparableTerm> comps = ont.getTermSet();
		for (ComparableTerm comp : comps) {
			if (Double.parseDouble(comp.getDescription()) == seqVersion) {
				return new Compilation(organism, comp);
			}
		}
		return null;
	}

	public static List<RichFeature> getFeatures(ComparableTerm type, Organism organism, int version) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b where "
			+ "b.version=:version and f.typeTerm=:typeTerm and b.taxon=:taxonId ");
		query.setInteger("version", version);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("typeTerm", type);
		return query.list();
	}

	public static List<Integer> getFeaturesId(ComparableTerm type, Organism organism, int version) {
		Query query = session.createQuery("select f.id from Feature as f join f.parent as b where "
			+ "b.version=:version and f.typeTerm=:typeTerm and b.taxon=:taxonId ");
		query.setInteger("version", version);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("typeTerm", type);
		return query.list();
	}

	public static RichFeature getFeature(Integer id) {
		Query query = session.createQuery("from Feature where id=:id");
		query.setInteger("id", id);
		return (RichFeature) query.uniqueResult();
	}

	public static Gene getGene(String geneName, Organism organism) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b where "
			+ "f.name=:geneName and f.typeTerm=:geneTerm and b.taxon=:taxonId ");
		query.setString("geneName", geneName);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("geneTerm", TermsAndOntologies.getTermGene());
		List features = query.list();
		if (features.size() != 1) {
			return null;
		}
		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		feature.toString();
		return new Gene(feature);
	}

	public static CDS getCDS(String cdsName, Organism organism) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b where "
			+ "f.name=:cdsName and f.typeTerm=:cdsTerm and b.taxon=:taxonId ");
		query.setString("cdsName", cdsName);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("cdsTerm", TermsAndOntologies.getTermCDS());
		List features = query.list();
		if (features.size() != 1) {
			return null;
		}
		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		feature.toString();
		return new CDS(feature);
	}

	public static CDS getCDSByProtID(String protName, Organism organism) {
		Query query = session.createQuery("select f from Feature as f join f.parent as b join f.noteSet as prop "
			+ "where f.typeTerm=:cdsTerm and b.taxon=:taxonId and "
			+ "prop.term=:termProteinID and prop.value=:protName");
		query.setString("protName", protName);
		query.setParameter("taxonId", organism.getTaxon());
		query.setParameter("cdsTerm", TermsAndOntologies.getTermCDS());
		query.setParameter("termProteinID", TermsAndOntologies.getTermProteinID());
		List features = query.list();
		if (features.size() != 1) {
			return null;
		}
		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		feature.toString();
		return new CDS(feature);
	}

	public static RichFeature getParent(RichFeature feature) {
		Query query = session.createQuery("from FeatureRelationship as f "
			+ "where f.term=:containsTerm and f.subject=:feature");
		query.setParameter("containsTerm", SimpleRichFeatureRelationship.getContainsTerm());
		query.setParameter("feature", feature);
		//		RichFeatureRelationship featureRelationship = (RichFeatureRelationship) query.uniqueResult();

		List features = query.list();
		if (features.size() < 1) {
			return null;
		}
		RichFeatureRelationship featureRelationship = (RichFeatureRelationship) features.get(0);
		if (featureRelationship != null) {
			return featureRelationship.getObject();
		}
		return null;
	}

	public static void LoadScaffolds(String fileName, Progress progress, int stepToSave)
			throws FileNotFoundException, BioException {
		// an input FASTA file
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

		if (progress != null) {
			progress.init();
		}

		int i = 0;

		BioSql.beginTransaction();
		RichSequenceIterator seqs;
		// we are reading DNA sequences
		if (fileName.endsWith("gbk")) {
			seqs = RichSequence.IOTools.readGenbankDNA(fileReader, BioSql.getDefaultNamespace());
		}
		else {
			seqs = RichSequence.IOTools.readFastaDNA(fileReader, BioSql.getDefaultNamespace());
		}
		while (seqs.hasNext()) {
			RichSequence seq = seqs.nextRichSequence();
			Set<RichFeature> features = seq.getFeatureSet();
			ArrayList<RichFeature> genes = new ArrayList<RichFeature>();
			ArrayList<RichFeature> mRNAs = new ArrayList<RichFeature>();
			ArrayList<RichFeature> cDSs = new ArrayList<RichFeature>();
			for (RichFeature feature : features) {
				if (feature.getType().equalsIgnoreCase("gene")) {
					genes.add(feature);
				}
				else if (feature.getType().equalsIgnoreCase("mRNA")) {
					mRNAs.add(feature);
				}
				else if (feature.getType().equalsIgnoreCase("CDS")) {
					cDSs.add(feature);
				}
			}
			for (RichFeature gene : genes) {
				for (RichFeature mRNA : mRNAs) {
					for (RichFeature cDS : cDSs) {
						if (mRNA.getLocation().contains(cDS.getLocation())) {
							mRNA.addFeatureRelationship(new SimpleRichFeatureRelationship(mRNA, cDS,
								SimpleRichFeatureRelationship.getContainsTerm(), 0));
						}
					}
					if (gene.getLocation().contains(mRNA.getLocation())) {
						gene.addFeatureRelationship(new SimpleRichFeatureRelationship(gene, mRNA,
							SimpleRichFeatureRelationship.getContainsTerm(), 0));
					}
				}
			}
			session.saveOrUpdate("Sequence", seq);
			if (progress != null) {
				progress.completeStep();
			}
			i++;
			if (i % stepToSave == 0) {
				BioSql.restartTransaction();
			}
		}
		BioSql.endTransactionOK();
		if (progress != null) {
			progress.finish();
		}
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
		//		session.close();
		RichObjectFactory.clearLRUCache();
		//		init();
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
