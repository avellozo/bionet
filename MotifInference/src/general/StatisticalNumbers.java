/*
 * Created on Nov 16, 2010
 */
package general;

import java.util.List;

public interface StatisticalNumbers
{
	public float getMeanNumber();

	public int getK();

	public void setK(int k);

	public void setGraph(List<Node> graph);

	public List<Node> getGraph();
}
