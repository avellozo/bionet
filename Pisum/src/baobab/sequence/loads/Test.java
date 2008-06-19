/*
 * Created on 19/06/2008
 */
package baobab.sequence.loads;

import java.util.List;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.ComparableTerm;
import org.hibernate.Query;
import org.hibernate.Transaction;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Gene;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;

public class Test
{
	//	public static SessionFactory					sessionFactory		= new Configuration().configure(
	//																			"hibernate.cfg.xml").buildSessionFactory();
	//	public static Session							session				= sessionFactory.openSession();
	//
	//	//	public static void init() {
	//	static {
	//		RichObjectFactory.connectToBioSQL(session);
	//		RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
	//		//		RichObjectFactory.setDefaultRichSequenceHandler(new BioSQLRichSequenceHandler(session));
	//		//		session.setFlushMode(FlushMode.ALWAYS);
	//	}
	//
	//	public static final SimpleComparableOntology	ONT_FEATURES		= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																			SimpleComparableOntology.class,
	//																			new Object[] {Messages.getString("ontologyFeatures")});
	//	public static final ComparableTerm				TERM_GENE			= ONT_FEATURES.getOrCreateTerm(Messages.getString("termGene"));
	//
	//	public static final SimpleComparableOntology	ONT_LINK_GENE2KO	= (SimpleComparableOntology) RichObjectFactory.getObject(
	//																			SimpleComparableOntology.class,
	//																			new Object[] {Messages.getString("Gene.ontologyToKo")});
	//
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Transaction tx = BioSql.beginTransaction();
		//		Query taxonsQuery = TermsAndOntologies.session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber");
		//		taxonsQuery.setInteger("ncbiTaxonNumber", 7029);
		//		List taxons = taxonsQuery.list();
		//		NCBITaxon org = (NCBITaxon) taxons.get(0);
		Organism organism;
		try {
			organism = new Organism(Integer.parseInt("7029"));
		}
		catch (DBObjectNotFound e1) {
			e1.printStackTrace();
			return;
		}
		NCBITaxon org = organism.getTaxon();
		ComparableTerm method = TermsAndOntologies.ONT_LINK_GENE2KO.getOrCreateTerm("test");
		String geneName = "APF00001";

		//		Query query = TermsAndOntologies.session.createQuery("select f from Feature as f join f.parent as bioentry where "
		//			+ "f.name=:geneName and f.typeTerm=:geneTerm and bioentry.taxon=:taxonId ");
		//		query.setString("geneName", "APF00001");
		//		query.setParameter("taxonId", org);
		//		query.setParameter("geneTerm", TermsAndOntologies.TERM_GENE);
		//		List features = query.list();
		//		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		//		System.out.println(feature.toString());
		//		Gene gene = new Gene(feature);

		Gene gene = BioSql.getGene("APF00001", organism);

		//		Query query = TermsAndOntologies.session.createQuery("select f from Feature as f join f.parent as bioentry where "
		//			+ "f.name=:geneName and f.typeTerm=:geneTerm and bioentry.taxon=:taxonId ");
		//		query.setString("geneName", geneName);
		//		query.setParameter("taxonId", organism.getTaxon());
		//		query.setParameter("geneTerm", TermsAndOntologies.TERM_GENE);
		//		List features = query.list();
		//		if (features.size() != 1) {
		//			return;
		//		}
		//		SimpleRichFeature feature = (SimpleRichFeature) features.get(0);
		//		Gene gene = new Gene(feature);
		//		feature.toString();

		KO ko = new KO("K07830");
		try {
			gene.link2KO(ko, method);
		}
		catch (BioException e) {
			e.printStackTrace();
		}

		Query query1 = TermsAndOntologies.session.createQuery("select f from Feature as f join f.parent as bioentry where "
			+ "f.name=:geneName and f.typeTerm=:geneTerm and bioentry.taxon=:taxonId ");
		query1.setString("geneName", "APF00013");
		query1.setParameter("taxonId", org);
		query1.setParameter("geneTerm", TermsAndOntologies.TERM_GENE);
		List features1 = query1.list();
		SimpleRichFeature feature1 = (SimpleRichFeature) features1.get(0);
		System.out.println(feature1.toString());

	}

}
