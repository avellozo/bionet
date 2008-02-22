/*
 * Created on 06/02/2008
 */
package metabolicNetwork;

import java.io.PrintStream;
import java.util.ArrayList;

public class MotifArrayList implements MotifList
{
	ArrayList<Subgraph>	list	= new ArrayList<Subgraph>();

	public void add(Subgraph motif)
	{
		list.add(motif);
	}

	public int size()
	{
		return list.size();
	}
	public void print(PrintStream p)
	{
		for (Subgraph n : list)
		{
			p.println(n.toString());
		}
	}

}