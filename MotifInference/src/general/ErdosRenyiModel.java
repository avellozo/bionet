/*
 * Created on Nov 17, 2010
 */
package general;

import java.util.ArrayList;
import java.util.Arrays;

public class ErdosRenyiModel implements StatisticalNumbers
{

	private Graph					graph;
	private int						m;
	private int						n;
	private double					p;
	private static ArrayList<Long>	factorial	= new ArrayList<Long>();
	private ArrayList<Long>			combineN	= new ArrayList<Long>();
	private ArrayList<Double>		gkp			= new ArrayList<Double>();

	{
		factorial.add((long) 1); //0!=1
		gkp.add((double) 0); //g(0,p)=0
	}

	public ErdosRenyiModel(Graph graph) {
		this.graph = graph;
		initialize(graph);
	}

	private void initialize(Graph graph) {
		m = graph.getNumberOfEdges();
		n = graph.getNumberOfNodes();
		p = graph.p();
	}

	public double getMeanNumberOfMotifSorted(Color[] motifSorted) {
		int k = motifSorted.length;
		return combine(n, k) * gkp(k) * getGammaOfMotifSorted(motifSorted);
	}

	public double getMeanNumber(Color[] motif) {
		int k = motif.length;
		return combine(n, k) * gkp(k) * getGamma(motif);
	}

	public double getVariance(Color[] motifSorted) {
		return 0;
	}

	public double getGamma(Color[] motif) {
		Color[] motifSorted = motif.clone();
		Arrays.sort(motifSorted);
		return getGammaOfMotifSorted(motifSorted);
	}

	public double getGammaOfMotifSorted(Color[] motifSorted) {
		int k = motifSorted.length;
		double ret = factorial(k) * 1.0;
		int sc = 1;
		for (int i = 0; i < k; i++) {
			ret = ret * motifSorted[i].getF();
			if (i < k - 1 && motifSorted[i].getId() == motifSorted[i + 1].getId()) {
				sc++;
			}
			else {
				if (sc > 1) {
					ret = ret / factorial(sc);
					sc = 1;
				}
			}
		}
		return ret;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
		initialize(graph);
	}

	public Graph getGraph() {
		return graph;
	}

	public static long factorial(int n) {
		int lastN = factorial.size() - 1;
		if (n > lastN) {
			long lastFact = factorial.get(lastN);
			for (int i = lastN + 1; i <= n; i++) {
				lastFact = i * lastFact;
				factorial.add(lastFact);
			}
		}
		return factorial.get(n);
	}

	public double gkp(int k) {
		if (k > gkp.size() - 1) {
			double ret = 0;
			for (int i = 1; i < k; i++) {
				ret = ret + combine(k - 1, i - 1) * gkp(i) * Math.pow(1 - p, i * (k - i));
			}
			gkp.add(1 - ret);
		}
		return gkp.get(k);
	}

	public static long combine(int n, int k) {
		if (k > n || k < 0) {
			return 0;
			//			throw new RuntimeException("Invalid n,k for the combination: n=" + n + " and k=" + k);
		}
		else {
			if (k > (n / 2)) {
				k = n - k;
			}
			long ret = 1;
			for (int i = 0; i < k; i++) {
				ret = ret * (n - i);
			}
			return ret / factorial(k);
		}
	}

	public double getZScore(Color[] motif, int occurrences) {
		double meanNumber = getMeanNumberOfMotifSorted(motif);
		double zScore = occurrences - meanNumber;
		if (zScore < 0) {
			zScore = zScore * -1;
		}
		return zScore;
	}
}
