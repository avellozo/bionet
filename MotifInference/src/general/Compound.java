/*
 * Created on 30/04/2008
 */
package general;

import java.util.Collection;
import java.util.HashSet;

public class Compound
{
	String				id;
	HashSet<Reaction>	reactionsAsSubstrate	= new HashSet<Reaction>();
	HashSet<Reaction>	reactionsAsProduct		= new HashSet<Reaction>();

	public Compound(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	protected boolean addAsSubstrate(Reaction r)
	{
		return reactionsAsSubstrate.add(r);
	}

	protected boolean addAsProduct(Reaction r)
	{
		return reactionsAsProduct.add(r);
	}

	public Collection<Reaction> getReactionsAsSubstrate()
	{
		return reactionsAsSubstrate;
	}

	public Collection<Reaction> getReactionsAsProduct()
	{
		return reactionsAsProduct;
	}
}
