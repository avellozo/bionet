package metabolicNetwork;

import java.io.PrintStream;

public interface SubgraphSet
{

	public void add(Subgraph subg);

	public int size();

	public void print(PrintStream p);
}
