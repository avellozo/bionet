/*
 * Created on Nov 24, 2010
 */
package general;

public class MotifSorted extends Motif
{

	public MotifSorted(Color[] colors, StatisticalModel statisticalModel, int numberOfOccurrences) {
		super(colors, statisticalModel, numberOfOccurrences);
	}

	public MotifSorted(Motif motif) {
		super(motif.getColorsSorted(), motif.getStatisticalNumbers().getStatisticalModel(),
			motif.getStatisticalNumbers().getNumberOfOccurrences());
	}

}
