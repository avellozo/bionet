/*
 * Created on Nov 17, 2010
 */
package general;

public class ErdosRenyiModel implements StatisticalNumbers
{

	private Graph	graph;
	private int		k;
	private int		m;
	private int		n;
	private double	p;
	private long	kFactorial;
	private long	combineNK;
	private double	gkp;

	public ErdosRenyiModel(Graph graph, int k) {
		this.graph = graph;
		this.k = k;
		initialize(graph, k);
	}

	private void initialize(Graph graph, int k) {
		m = graph.getNumberOfEdges();
		n = graph.getNumberOfNodes();
		p = graph.p();
		kFactorial = factorial(k);
		combineNK = combine(n, k);
		gkp = gkp(k, p);
	}

	public double getMeanNumber(Color[] motifSorted) {
		return combineNK * gkp * gamma(motifSorted);
	}

	public double getVariance(Color[] motifSorted) {
		return 0;
	}

	public double gamma(Color[] motifSorted) {
		double ret = kFactorial;
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
		initialize(graph, k);
	}

	public Graph getGraph() {
		return graph;
	}

	public static long factorial(int n) {
		long ret = 1;
		for (int i = 2; i <= n; i++) {
			ret = ret * i;
		}
		return ret;
	}

	public static double gkp(int k, double p) {
		double ret = 0;
		for (int i = 1; i < k; i++) {
			ret = ret + combine(k - 1, i - 1) * gkp(i, p) * Math.pow(1 - p, i * (k - i));
		}
		return 1 - ret;
	}

	public static long combine(int n, int k) {
		if (k > n || k < 0) {
			throw new RuntimeException("Invalid n,k for the combination: n=" + n + " and k=" + k);
		}
		else {
			if (k > (n / 2)) {
				k = n - k;
			}
			long ret = 1;
			long fact = 1;
			for (int i = 0; i < k; i++) {
				ret = ret * (n - i);
				fact = fact * (k - i);
			}
			return ret / fact;
		}
	}
}
