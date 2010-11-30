/*
 * Created on Nov 19, 2010
 */
package general;

import java.util.Arrays;
import java.util.Comparator;

public class Motif
{
	Color[]				colors;
	StatisticalNumbers	statisticalNumbers;

	//	public Motif(Color[] colors, int numberOfOccurrences, double meanNumberOfOccurrences, double zScore) {
	//		this.colors = colors;
	//		this.numberOfOccurrences = numberOfOccurrences;
	//		this.meanNumberOfOccurrences = meanNumberOfOccurrences;
	//		this.zScore = zScore;
	//	}

	//	public Motif(Color[] colors, int numberOfOccurrences, double zScore) {
	//		this.colors = colors;
	//		this.zScore = zScore;
	//		this.numberOfOccurrences = numberOfOccurrences;
	//	}

	public Motif(Color[] colors, StatisticalModel statisticalModel, int numberOfOccurrences) {
		this.colors = colors;
		statisticalNumbers = new StatisticalNumbers(this, statisticalModel, numberOfOccurrences);
	}

	public StatisticalNumbers getStatisticalNumbers() {
		return statisticalNumbers;
	}

	//	public void setStatisticalNumbers(StatisticalNumbers statisticalNumbers) {
	//		this.statisticalNumbers = statisticalNumbers;
	//	}

	public Color[] getColors() {
		return colors;
	}

	public Color[] getColorsSorted() {
		Arrays.sort(colors);
		return colors;
	}

	public String colorsToString() {
		StringBuffer ret = new StringBuffer();
		for (Color color : colors) {
			ret.append(color.getDescription());
			ret.append("; ");
		}
		return ret.substring(0, ret.length() - 2);
	}

	//	public double getMeanNumber() {
	//		return getStatisticalNumbers().getMeanNumber();
	//	}
	//
	//	public double getVariance() {
	//		return getStatisticalNumbers().getVariance();
	//	}
	//
	//	public double getZScore() {
	//		return getStatisticalNumbers().getZScore();
	//	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Motif)) {
			return false;
		}
		return (colors.equals(((Motif) obj).getColors()));
	}
}

class ComparatorByZScore implements Comparator<Motif>
{
	public int compare(Motif o1, Motif o2) {
		double ret = o2.getStatisticalNumbers().getZScore() - o1.getStatisticalNumbers().getZScore(); //descending order
		if (ret != 0) {
			return ret < 0 ? -1 : 1;
		}
		else {
			if (!o1.equals(o2)) {
				double ret1 = o2.getStatisticalNumbers().getMeanNumber() - o1.getStatisticalNumbers().getMeanNumber();
				if (ret1 != 0) {
					return ret1 < 0 ? -1 : 1;
				}
				else {
					int ret2 = o2.getStatisticalNumbers().getNumberOfOccurrences()
						- o1.getStatisticalNumbers().getNumberOfOccurrences();
					if (ret2 != 0) {
						return ret2;
					}
					else {
						int hash1 = o1.hashCode(), hash2 = o2.hashCode();
						return (hash1 > hash2 ? 1 : (hash1 == hash2 ? 0 : -1));
					}
				}
			}
			else {
				return 0;
			}
		}
	}
}

class ComparatorByMeanNumber implements Comparator<Motif>
{
	public int compare(Motif o1, Motif o2) {
		double ret = o2.getStatisticalNumbers().getMeanNumber() - o1.getStatisticalNumbers().getMeanNumber(); //descending order
		if (ret != 0) {
			return ret < 0 ? -1 : 1;
		}
		else {
			if (!o1.equals(o2)) {
				double ret1 = o2.getStatisticalNumbers().getZScore() - o1.getStatisticalNumbers().getZScore();
				if (ret1 != 0) {
					return ret1 < 0 ? -1 : 1;
				}
				else {
					int ret2 = o2.getStatisticalNumbers().getNumberOfOccurrences()
						- o1.getStatisticalNumbers().getNumberOfOccurrences();
					if (ret2 != 0) {
						return ret2;
					}
					else {
						int hash1 = o1.hashCode(), hash2 = o2.hashCode();
						return (hash1 > hash2 ? 1 : (hash1 == hash2 ? 0 : -1));
					}
				}
			}
			else {
				return 0;
			}
		}
	}
}

class ComparatorByNumberOfOccurrences implements Comparator<Motif>
{
	public int compare(Motif o1, Motif o2) {
		int ret = o2.getStatisticalNumbers().getNumberOfOccurrences()
			- o1.getStatisticalNumbers().getNumberOfOccurrences(); //descending order
		if (ret != 0) {
			return ret;
		}
		else {
			if (!o1.equals(o2)) {
				double ret1 = o2.getStatisticalNumbers().getZScore() - o1.getStatisticalNumbers().getZScore();
				if (ret1 != 0) {
					return ret1 < 0 ? -1 : 1;
				}
				else {
					double ret2 = o2.getStatisticalNumbers().getMeanNumber()
						- o1.getStatisticalNumbers().getMeanNumber();
					if (ret2 != 0) {
						return ret2 < 0 ? -1 : 1;
					}
					else {
						int hash1 = o1.hashCode(), hash2 = o2.hashCode();
						return (hash1 > hash2 ? 1 : (hash1 == hash2 ? 0 : -1));
					}
				}
			}
			else {
				return 0;
			}
		}
	}
}
