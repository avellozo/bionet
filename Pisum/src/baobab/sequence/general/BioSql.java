/*
 * Created on 10/06/2008
 */
package baobab.sequence.general;

import java.util.List;

import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.taxa.NCBITaxon;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class BioSql
{
	public static SessionFactory	sessionFactory	= new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	public static Session			session			= sessionFactory.openSession();

	//	public static void init() {
	static {
		RichObjectFactory.connectToBioSQL(session);
		RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
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

	public static Transaction beginTransaction() {
		return session.beginTransaction();
	}

	public static Namespace getDefaultNamespace() {
		return RichObjectFactory.getDefaultNamespace();
	}

	public static void saveEst(EST est) {
		session.saveOrUpdate("Sequence", est.getSequence());
	}

	public static void restartTransaction() {
		session.flush();
		session.clear();
		commit();
		beginTransaction();
	}

	public static Transaction getTransaction() {
		return beginTransaction();
	}

	public static void commit() {
		getTransaction().commit();
	}

	public static void rollback() {
		getTransaction().rollback();
	}

	public static void endTransactionOK() {
		session.flush();
		session.clear();
		commit();
	}

	public static void finish() {
		rollback();
		session.close();
	}

}
