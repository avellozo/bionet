/*
 * Created on Nov 17, 2010
 */
package general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class ErdosRenyiModel implements StatisticalModel
{

	private Graph													graph;
	private int														m;
	private int														n;
	private double													p;
	private static ArrayList<Long>									factorial	= new ArrayList<Long>();
	private static Hashtable<Integer, Hashtable<Integer, Long>>		combine		= new Hashtable<Integer, Hashtable<Integer, Long>>();
	private static Hashtable<Integer, Hashtable<Integer, Double>>	kAlfaBeta	= new Hashtable<Integer, Hashtable<Integer, Double>>();
	private ArrayList<Double>										gkp			= new ArrayList<Double>();

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

	public double getMeanNumber(Color[] motif) {
		Color[] motifSorted = motif.clone();
		Arrays.sort(motifSorted);
		return getMeanNumberOfMotifSorted(motifSorted);
	}

	public double getMeanNumberOfMotifSorted(Color[] motifSorted) {
		int k = motifSorted.length;
		return combine(n, k) * gkp(k) * getGammaOfMotifSorted(motifSorted);
	}

	public double getVariance(Color[] motif) {
		Color[] motifSorted = motif.clone();
		Arrays.sort(motifSorted);
		return getVarianceOfMotifSorted(motifSorted);
	}

	public double getVarianceOfMotifSorted(Color[] motifSorted) {
		double ret = 0;
		int k = motifSorted.length;
		for (int l = 0; l <= k; l++) {
			ret = ret
				+ (combine(n, l) * combine(n - l, k - l) * combine(n - k, k - l) * getKAlfaBeta(k, l) * getQAlfaBeta(
					motifSorted, l));
		}
		return ret;
	}

	private double getKAlfaBeta(int k, int l) {
		if (k > 5) {
			throw new RuntimeException("Doesn't know the formulae of K(Alfa,Beta) for k = " + k);
		}
		if (l < 0 || l > k || k < 0) {
			throw new RuntimeException("Invalid k = " + k + " or l = " + l);
		}

		Hashtable<Integer, Double> kAlfaBetaK = kAlfaBeta.get(k);
		if (kAlfaBetaK == null) {
			kAlfaBetaK = new Hashtable<Integer, Double>();
			kAlfaBeta.put(k, kAlfaBetaK);
		}
		Double ret = kAlfaBetaK.get(l);
		if (ret == null) {
			if (l == 0 || l == 1) {
				ret = gkp(k) * gkp(k);
			}
			else if (l == k) {
				ret = gkp(k);
			}
			else if (k == 3) {
				if (l == 2) {
					ret = 4 * Math.pow(p, 3) - 3 * Math.pow(p, 4);
				}
			}
			else if (k == 4) {
				if (l == 2) {
					ret = 64 * Math.pow(p, 5) - 160 * Math.pow(p, 6) + 100 * Math.pow(p, 7) + 77 * Math.pow(p, 8) - 136
						* Math.pow(p, 9) + 68 * Math.pow(p, 10) - 12 * Math.pow(p, 11);
				}
				else if (l == 3) {
					ret = 27 * Math.pow(p, 4) - 60 * Math.pow(p, 5) + 46 * Math.pow(p, 6) - 12 * Math.pow(p, 7);
				}
			}
			else if (k == 5) {
				if (l == 2) {
					ret = -288 * Math.pow(p, 19) + 3852 * Math.pow(p, 18) - 23436 * Math.pow(p, 17) + 85452
						* Math.pow(p, 16) - 206736 * Math.pow(p, 15) + 346092 * Math.pow(p, 14) - 403332
						* Math.pow(p, 13) + 316276 * Math.pow(p, 12) - 146892 * Math.pow(p, 11) + 17492
						* Math.pow(p, 10) + 22296 * Math.pow(p, 9) - 13275 * Math.pow(p, 8) + 2500 * Math.pow(p, 7);
				}
				else if (l == 3) {
					ret = -24 * Math.pow(p, 17) + 156 * Math.pow(p, 16) - 156 * Math.pow(p, 15) - 1616
						* Math.pow(p, 14) + 7568 * Math.pow(p, 13) - 16061 * Math.pow(p, 12) + 19174 * Math.pow(p, 11)
						- 11783 * Math.pow(p, 10) + 516 * Math.pow(p, 9) + 4582 * Math.pow(p, 8) - 3030
						* Math.pow(p, 7) + 675 * Math.pow(p, 6);
				}
				else if (l == 4) {
					ret = 60 * Math.pow(p, 11) - 440 * Math.pow(p, 10) + 1360 * Math.pow(p, 9) - 2275 * Math.pow(p, 8)
						+ 2180 * Math.pow(p, 7) - 1140 * Math.pow(p, 6) + 256 * Math.pow(p, 5);
				}
			}
			kAlfaBetaK.put(l, ret);
		}
		return ret;
	}

	private double getQAlfaBeta(Color[] motifSorted, int l) {
		int k = motifSorted.length;
		if (l < 0 || l > k || k < 0) {
			throw new RuntimeException("Invalid k = " + k + " or l = " + l);
		}
		if (l == 0) {
			return Math.pow(getGammaOfMotifSorted(motifSorted), 2);
		}
		if (l == k) {
			return getGammaOfMotifSorted(motifSorted);
		}

		int[] indexMStar = new int[l];
		indexMStar[0] = 0;
		double ret = getQAlfaBeta(motifSorted, indexMStar, 0);
		for (int i = 1; i <= k - l; i++) {
			if (!motifSorted[i].equals(motifSorted[i - 1])) {
				indexMStar[0] = i;
				ret += getQAlfaBeta(motifSorted, indexMStar, 0);
			}
		}
		return ret;
	}

	private double getQAlfaBeta(Color[] motifSorted, int[] indexInMotifSortedMStar, int pos) {
		//calculate QAlfaBeta with mStar[0..pos] filled
		int l = indexInMotifSortedMStar.length;
		int k = motifSorted.length;
		//		if (pos < 0 || pos > l) {
		//			throw new RuntimeException("Invalid pos = " + pos + " in getQAlfaBeta.");
		//		}
		//		if (l == 0) {
		//			return Math.pow(getGammaOfMotifSorted(motifSorted), 2);
		//		}
		int toFinishMStar = l - 1 - pos;
		if (toFinishMStar == 0) {
			//get mStar and mMinus
			Color[] mStar = new Color[l];
			Color[] mMinus = new Color[k - l];
			int j = 0;
			int i = 0;
			for (; i < l; i++) {
				mStar[i] = motifSorted[indexInMotifSortedMStar[i]];
				for (; i + j < indexInMotifSortedMStar[i]; j++) {
					mMinus[j] = motifSorted[i + j];
				}
			}
			for (; i + j < k; j++) {
				mMinus[j] = motifSorted[i + j];
			}
			//Calculate getQAlfaBeta for this mStar
			return getGammaOfMotifSorted(mStar) * Math.pow(getGammaOfMotifSorted(mMinus), 2);
		}
		else {
			int lastIndex = indexInMotifSortedMStar[pos];
			//			if (k - 1 - lastIndex < toFinishMStar) {
			//				return 0.0;
			//			}
			indexInMotifSortedMStar[pos + 1] = lastIndex + 1;
			double ret = getQAlfaBeta(motifSorted, indexInMotifSortedMStar, pos + 1);
			for (int i = 2; lastIndex + i <= k - toFinishMStar; i++) {
				if (!motifSorted[lastIndex + i].equals(motifSorted[lastIndex + i - 1])) {
					indexInMotifSortedMStar[pos + 1] = lastIndex + i;
					ret += getQAlfaBeta(motifSorted, indexInMotifSortedMStar, pos + 1);
				}
			}
			return ret;
		}
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
			Hashtable<Integer, Long> combineN = combine.get(n);
			if (combineN == null) {
				combineN = new Hashtable<Integer, Long>();
				combine.put(n, combineN);
			}
			Long ret = combineN.get(k);
			if (ret == null) {
				ret = 1L;
				for (int i = 0; i < k; i++) {
					ret = ret * (n - i);
				}
				ret = ret / factorial(k);
				combineN.put(k, ret);
			}
			return ret;
		}
	}

	//	public double getZScore(Motif motif, int occurrences) {
	//		double meanNumber = motif.getMeanNumber();
	//		double zScore = occurrences / meanNumber;
	//		if (zScore < 0) {
	//			zScore = zScore * -1;
	//		}
	//		return zScore;
	//	}
}
