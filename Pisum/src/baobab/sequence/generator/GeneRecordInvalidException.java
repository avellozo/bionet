/*
 * Created on 16/09/2008
 */
package baobab.sequence.generator;

public class GeneRecordInvalidException extends Exception
{

	public GeneRecordInvalidException(GeneRecord geneRecord) {
		super("Gene record invalid: " + geneRecord.getId() + " " + geneRecord.getName());
	}

}
