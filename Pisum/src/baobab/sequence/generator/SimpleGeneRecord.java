/*
 * Created on 16/09/2008
 */
package baobab.sequence.generator;

import java.util.Collection;

public class SimpleGeneRecord implements GeneRecord
{
	String	id, name, type;

	public SimpleGeneRecord(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<DBLink> getDBLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getECs() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getEndBase() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<Function> getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProductID() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStartBase() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getSynonyms() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
