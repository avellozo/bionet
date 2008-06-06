/*
 * Created on 06/06/2008
 */
package baobab.sequence.exception;

public class DBObjectAlreadyExists extends DBException
{

	public DBObjectAlreadyExists()
	{
		super();
	}

	public DBObjectAlreadyExists(String message)
	{
		super(message);
	}

	public DBObjectAlreadyExists(Object[] keys)
	{
		super("Object with keys " + getString(keys) + " already exists in database.");
	}

}
