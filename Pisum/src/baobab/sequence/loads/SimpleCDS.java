/*
 * Created on 19/08/2008
 */
package baobab.sequence.loads;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.Position;
import org.biojavax.bio.seq.SimpleRichLocation;
import org.biojavax.bio.seq.RichLocation.Strand;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.general.BioSql;
import baobab.sequence.general.CDS;
import baobab.sequence.general.Gene;
import baobab.sequence.general.MRNA;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;

public class SimpleCDS
{
	String			geneName, cdsName;
	Strand			strand;
	Position		beginLocation	= null, endLocation = null;
	CDS				cds;
	Organism		organism;
	ComparableTerm	method;

	public SimpleCDS(Organism organism, String geneName, String cdsName, ComparableTerm method) {
		this.geneName = geneName;
		this.cdsName = cdsName;
		this.organism = organism;
		this.method = method;
	}

	public CDS save() throws BioException {
		if (beginLocation != null && endLocation != null) {
			Gene gene = BioSql.getGene(getGeneName(), organism);
			MRNA mrna = gene.createMRNA(getGeneName(),
				(SimpleRichLocation) gene.getFeature().getLocation().translate(0), TermsAndOntologies.getTermVR());
			return cds = mrna.createCDS(getCDSName(), new SimpleRichLocation(beginLocation, endLocation, 0, strand),
				method);
		}
		return null;
	}

	public Strand getStrand() {
		return strand;
	}

	public void setStrand(Strand strand) {
		this.strand = strand;
	}

	public void setStrand(int strand) {
		if (strand < 0) {
			this.strand = Strand.NEGATIVE_STRAND;
		}
		else {
			this.strand = Strand.POSITIVE_STRAND;
		}
	}

	public String getGeneName() {
		return geneName;
	}

	public String getCDSName() {
		return cdsName;
	}

	public Position getBeginLocation() {
		return beginLocation;
	}

	public void setBeginLocation(Position beginLocation) {
		this.beginLocation = beginLocation;
	}

	public Position getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(Position endLocation) {
		this.endLocation = endLocation;
	}

	public void print() {
		System.out.println("CDS:" + cdsName);
		System.out.println("Strand:" + strand);
		System.out.println("Sequence:" + cds.getSequenceStr());
	}

}
