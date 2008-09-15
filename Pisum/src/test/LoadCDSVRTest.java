/*
 * Created on 05/06/2008
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeSet;

import org.biojava.bio.BioException;
import org.biojavax.RichObjectFactory;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.SimplePosition;
import org.biojavax.bio.seq.SimpleRichFeature;
import org.biojavax.bio.seq.SimpleRichLocation;
import org.biojavax.bio.seq.RichLocation.Strand;
import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.ontology.SimpleComparableOntology;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import baobab.sequence.general.Messages;

public class LoadCDSVRTest
{
	public static void main(String[] args) {
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		Session session = sessionFactory.openSession();
		//		session.setFlushMode(FlushMode.MANUAL);
		RichObjectFactory.connectToBioSQL(session);
		RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
		Transaction tx = session.beginTransaction();
		try {
			//file CDSs
			File fileCDSs;
			fileCDSs = new File(args[0]);

			String cdsName, geneName = "";
			BufferedReader br = new BufferedReader(new FileReader(fileCDSs));
			String line, line2, line3, lineAmino;
			int countCDSs = 0;
			int beginPos = -1, endPos = -1, nextPos = -1;
			int strand = 0;
			int stepCDS = Integer.parseInt(Messages.getString("LoadCDSVR.printCDS"));
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if (line.startsWith(">")) { //CDS heading
						//new CDS
						//save last CDS
						if (strand != 0) {
							saveCDS(session, strand, beginPos, endPos, nextPos - 1, geneName, Integer.parseInt(args[1]));
							countCDSs++;
						}
						if (countCDSs % stepCDS == 0) {
							System.out.println(countCDSs);
							session.flush();
							tx.commit();
							RichObjectFactory.clearLRUCache();
							session.clear();
							RichObjectFactory.clearLRUCache();
							session.close();
							RichObjectFactory.clearLRUCache();
							//							sessionFactory.close();
							//							sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
							session = sessionFactory.openSession();
							//							session.setFlushMode(FlushMode.MANUAL);
							RichObjectFactory.clearLRUCache();
							RichObjectFactory.connectToBioSQL(session);
							RichObjectFactory.clearLRUCache();
							RichObjectFactory.setDefaultNamespaceName(Messages.getString("nameSpaceDefault"));
							tx = session.beginTransaction();
						}
						cdsName = line.substring(1);
						geneName = cdsName.substring(0, cdsName.indexOf("_"));
						line = br.readLine();

						if (line.startsWith("Reading frame: ")) {
							strand = Integer.parseInt(line.substring(15));
							if (strand == 0) {
								System.out.println("Format error, strand = 0");
							}
							else {
								nextPos = 1;
								beginPos = -1;
								endPos = -1;
							}
						}
						else {
							System.out.println("Format error in line 'Reading frame':" + line);
							strand = 0;
						}
						br.readLine(); // empty line
					}
					else if (strand != 0) { //CDS sequence
						line2 = br.readLine();
						line3 = br.readLine();
						br.readLine(); // empty line
						if (strand < 0) {
							lineAmino = line3;
						}
						else {
							lineAmino = line;
						}
						lineAmino = lineAmino.substring(3, lineAmino.length() - 1);
						if (lineAmino.trim().length() != 0) {
							if (beginPos < 0) {
								beginPos = nextPos + firstPosNotSpace(lineAmino) - 1;
							}
							endPos = nextPos + lastPosNotSpace(lineAmino) + 1;
						}
						nextPos += lineAmino.length();
					}
				}
			}
			if (strand != 0) {
				saveCDS(session, strand, beginPos, endPos, nextPos - 1, geneName, Integer.parseInt(args[1]));
			}
			session.flush();
			tx.commit();
			session.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			session.close();
		}

	}

	public static void saveCDS(Session session, int strand, int beginPos, int endPos, int lastPos, String geneName,
			int ncbiTaxonId) throws BioException {
		SimplePosition beginPosition, endPosition;
		if (strand < 0 && beginPos < 4) {
			beginPosition = new SimplePosition(true, false, beginPos);
		}
		else {
			beginPosition = new SimplePosition(beginPos);
		}
		if (strand > 0 && (endPos == lastPos)) {
			endPosition = new SimplePosition(false, true, endPos);
		}
		else {
			endPosition = new SimplePosition(endPos);
		}
		//							save;
		NCBITaxon taxon = (NCBITaxon) session.createQuery("from Taxon where ncbi_taxon_id=:ncbiTaxonNumber").setInteger(
			"ncbiTaxonNumber", ncbiTaxonId).uniqueResult();

		SimpleComparableOntology ontFeatures = (SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {Messages.getString("ontologyFeatures")});
		SimpleComparableOntology ontGeneral = ((SimpleComparableOntology) RichObjectFactory.getObject(
			SimpleComparableOntology.class, new Object[] {Messages.getString("ontologyGeneral")}));
		//		((SimpleComparableOntology) RichObjectFactory.getObject(SimpleComparableOntology.class,
		//			new Object[] {"biojavax"})).getOrCreateTerm("contains");
		SimpleRichFeature featureGene = (SimpleRichFeature) session.createQuery(
			"select f from Feature as f join f.parent as b where "
				+ "f.name=:geneName and f.typeTerm=:geneTerm and b.taxon=:taxonId ").setString("geneName", geneName).setParameter(
			"taxonId", taxon).setParameter("geneTerm", ontFeatures.getOrCreateTerm(Messages.getString("termGene"))).uniqueResult();
		RichFeature.Template ft = new RichFeature.Template();
		ft.location = featureGene.getLocation().translate(0);
		ft.sourceTerm = ontGeneral.getOrCreateTerm(Messages.getString("termVR"));
		ft.typeTerm = ontFeatures.getOrCreateTerm(Messages.getString("termMRNA"));
		ft.annotation = new SimpleRichAnnotation();
		ft.featureRelationshipSet = new TreeSet();
		ft.rankedCrossRefs = new TreeSet();
		SimpleRichFeature featureMRNA = (SimpleRichFeature) featureGene.createFeature(ft);
		session.flush();
		featureMRNA.setName(geneName);
		//		session.save("Feature", featureMRNA);

		ft = new RichFeature.Template();
		if (strand < 0) {
			ft.location = new SimpleRichLocation(beginPosition, endPosition, 0, Strand.NEGATIVE_STRAND);
		}
		else {
			ft.location = new SimpleRichLocation(beginPosition, endPosition, 0, Strand.POSITIVE_STRAND);
		}
		ft.sourceTerm = ontGeneral.getOrCreateTerm(Messages.getString("termVR"));
		ft.typeTerm = ontFeatures.getOrCreateTerm(Messages.getString("termCDS"));
		ft.annotation = new SimpleRichAnnotation();
		ft.featureRelationshipSet = new TreeSet();
		ft.rankedCrossRefs = new TreeSet();
		SimpleRichFeature featureCDS = (SimpleRichFeature) featureMRNA.createFeature(ft);
		featureCDS.setName(geneName);
		//		session.save("Feature", featureCDS);
	}

	public static int firstPosNotSpace(String str) {
		int i = 0;
		while (i < str.length() && str.charAt(i) == ' ') {
			i++;
		}
		return i;
	}

	public static int lastPosNotSpace(String str) {
		int i = str.length() - 1;
		while (i >= 0 && str.charAt(i) == ' ') {
			i--;
		}
		return i;
	}
}
