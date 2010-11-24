/*
 * Created on Nov 16, 2010
 */
package general;

public interface StatisticalModel
{
	public double getMeanNumber(Color[] motif);

	public double getMeanNumberOfMotifSorted(Color[] motifSorted);

	public double getVariance(Color[] motif);

	public void setGraph(Graph graph);

	public Graph getGraph();
}
