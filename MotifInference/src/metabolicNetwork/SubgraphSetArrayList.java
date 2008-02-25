/*
 * Created on 06/02/2008
 */
package metabolicNetwork;

import java.io.PrintStream;
import java.util.ArrayList;

public class SubgraphSetArrayList implements SubgraphSet
{
	ArrayList<Subgraph>	list	= new ArrayList<Subgraph>();

	public void add(Subgraph subg)
	{
		list.add(subg);
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
