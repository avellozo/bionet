/*
 * Created on Nov 16, 2010
 */
package general;

public class StatisticalNumbers
{
	private Motif				motif;
	private StatisticalModel	statisticalModel;
	private int					numberOfOccurrences;
	private Double				meanNumber	= null;
	private Double				variance	= null;
	private Double				zScore		= null;

	public StatisticalNumbers(Motif motif, StatisticalModel statisticalModel, int numberOfOccurrences) {
		if (!(motif instanceof MotifSorted)) {
			motif = new MotifSorted(motif);
		}
		this.motif = motif;
		this.statisticalModel = statisticalModel;
		this.numberOfOccurrences = numberOfOccurrences;
	}

	public double getMeanNumber() {
		if (meanNumber == null) {
			meanNumber = getStatisticalModel().getMeanNumberOfMotifSorted(getMotif().getColors());
		}
		return meanNumber;
	}

	public double getVariance() {
		if (variance == null) {
			variance = getStatisticalModel().getVariance(getMotif().getColors());
		}
		return variance;
	}

	public double getZScore() {
		if (zScore == null) {
			zScore = (getNumberOfOccurrences() - getMeanNumber()) / getVariance();
		}
		return zScore;
	}

	public Motif getMotif() {
		return motif;
	}

	public StatisticalModel getStatisticalModel() {
		return statisticalModel;
	}

	public int getNumberOfOccurrences() {
		return numberOfOccurrences;
	}

}
