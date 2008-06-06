/*
 * Created on 06/06/2008
 */
package baobab.sequence.exception;

public class DBObjectNotFound extends DBException
{

	public DBObjectNotFound()
	{
	}

	public DBObjectNotFound(String message)
	{
		super(message);
	}

	public DBObjectNotFound(Object[] keys)
	{
		super("Object with keys " + getString(keys) + " not found");
	}

}
