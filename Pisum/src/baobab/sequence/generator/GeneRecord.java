/*
 * Created on 16/09/2008
 */
package baobab.sequence.generator;

import java.util.Collection;

public interface GeneRecord
{

	boolean isValid();

	String getId();

	String getName();

	int getStartBase();

	int getEndBase();

	String getType();

	String getComment();

	String getProductID();

	String[] getSynonyms();

	Collection<DBLink> getDBLinks();

	Collection<Function> getFunctions();

	String[] getECs();

}
