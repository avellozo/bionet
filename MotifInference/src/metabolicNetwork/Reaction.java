package metabolicNetwork;


public class Reaction
{

	String	ID;
	EC		ec;

	public Reaction(String ID)
	{
		this.ID = ID;
	}

	public String toString()
	{
		return ID;
	}

	public EC getEc()
	{
		return ec;
	}

	public void setEc(EC ec)
	{
		this.ec = ec;
	}

	public String getID()
	{
		return ID;
	}

}
