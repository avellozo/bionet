/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

public class PFFileStream
{
	PrintStream	out;

	public PFFileStream(PrintStream out) {
		this.out = out;
	}

	public PFFileStream(File fileOut) throws FileNotFoundException {
		this(new PrintStream(fileOut));
	}

	public PFFileStream(String fileOutName) throws FileNotFoundException {
		this(new PrintStream(new File(fileOutName)));
	}

	public void println(String str) {
		out.println(str);
	}

	public void print(GeneRecord geneRecord) throws GeneRecordInvalidException {
		if (geneRecord == null || !geneRecord.isValid()) {
			throw new GeneRecordInvalidException(geneRecord);
		}
		out.println("ID" + "\t\t" + geneRecord.getId());
		out.println("NAME" + "\t\t" + geneRecord.getName());
		out.println("STARTBASE" + "\t" + geneRecord.getStartBase());
		out.println("ENDBASE" + "\t\t" + geneRecord.getEndBase());
		out.println("PRODUCT-TYPE" + "\t" + geneRecord.getType());
		String comment = geneRecord.getComment();
		if (comment != null && comment.length() != 0) {
			out.println("GENE-COMMENT" + "\t" + comment);
		}
		String productID = geneRecord.getProductID();
		if (productID != null && productID.length() != 0) {
			out.println("PRODUCT-ID" + "\t" + productID);
		}
		Collection<String> synonyms = geneRecord.getSynonyms();
		if (synonyms != null) {
			for (String syn : synonyms) {
				if (syn != null && syn.length() > 0) {
					out.println("SYNONYM" + "\t\t" + syn);
				}
			}
		}

		Collection<DBLink> dbLinks = geneRecord.getDBLinks();
		if (dbLinks != null) {
			for (DBLink dbLink : dbLinks) {
				out.println("DBLINK" + "\t\t" + dbLink.getType() + ":" + dbLink.getValue());
			}
		}

		Collection<Function> functions = geneRecord.getFunctions();
		String synonym;
		for (Function function : functions) {
			out.println("FUNCTION" + "\t" + function.getName());
			comment = function.getComment();
			if (comment != null && comment.length() > 0) {
				out.println("FUNCTION-COMMENT" + "\t" + comment);
			}
			synonym = function.getSynonym();
			if (synonym != null && synonym.length() > 0) {
				out.println("FUNCTION-SYNONYM" + "\t" + synonym);
			}
		}

		Collection<String> ecs = geneRecord.getECs();
		if (ecs != null) {
			for (String ec : ecs) {
				if (ec != null && ec.length() > 0) {
					out.println("EC" + "\t\t" + ec);
				}
			}
		}
		Collection<Intron> introns = geneRecord.getIntrons();
		if (introns != null) {
			for (Intron intron : introns) {
				out.println("INTRON" + "\t\t" + intron.getBegin() + "-" + intron.getEnd());
			}
		}

		out.println("//");
		out.println();

		out.flush();
	}

	public void flush() {
		out.flush();
	}
}
