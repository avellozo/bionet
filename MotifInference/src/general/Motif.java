/*
 * Created on Nov 19, 2010
 */
package general;

import java.util.Comparator;

public class Motif
{
	Color[]	colors;
	int		numberOfOccurrences;
	double	zScore;

	public Motif(Color[] colors, int numberOfOccurrences, double zScore) {
		this.colors = colors;
		this.zScore = zScore;
		this.numberOfOccurrences = numberOfOccurrences;
	}

	public Motif(Color[] colors) {
		this.colors = colors;
	}

	public int getNumberOfOccurrences() {
		return numberOfOccurrences;
	}

	public void setNumberOfOccurrences(int numberOfOccurrences) {
		this.numberOfOccurrences = numberOfOccurrences;
	}

	public double getzScore() {
		return zScore;
	}

	public void setzScore(double zScore) {
		this.zScore = zScore;
	}

	public Color[] getColors() {
		return colors;
	}

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
		double ret = o2.getzScore() - o1.getzScore(); //descending order
		if (ret < 0) {
			return -1;
		}
		else if (ret > 0) {
			return 1;
		}
		else {
			return 0;
		}
	}
}
