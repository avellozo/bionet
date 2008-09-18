/*
 * Created on 16/09/2008
 */
package baobab.sequence.generator;

import java.util.Collection;

public interface GeneRecord
{

	public boolean isValid();

	public String getId();

	public String getName();

	public int getStartBase();

	public int getEndBase();

	public String getType();

	public String getComment();

	public String getProductID();

	public Collection<String> getSynonyms();

	public Collection<DBLink> getDBLinks();

	public Collection<Function> getFunctions();

	public Collection<String> getECs();

	public Collection<Intron> getIntrons();

	public void shiftLocation(int shiftQtty);
}
