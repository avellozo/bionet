/*
 * Created on Nov 16, 2010
 */
package general;

public interface StatisticalNumbers
{
	public double getMeanNumber(Color[] motif);

	public double getVariance(Color[] motif);

	//	public int getK();
	//
	//	public void setK(int k);

	public void setGraph(Graph graph);

	public Graph getGraph();
}
