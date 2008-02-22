package metabolicNetwork;

import java.io.PrintStream;

public interface MotifList
{

	public void add(Subgraph motif);

	public int size();
	
	public void print(PrintStream p);
}
