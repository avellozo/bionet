/*
 * Created on Nov 19, 2010
 */
package general;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class BestMotifCollection implements MotifCollection
{

	TreeSet<Motif>		motifCollection	= new TreeSet<Motif>(new ComparatorByZScore());
	int					maxSize;
	Comparator<Motif>	comparator;

	public BestMotifCollection(int maxSize, Comparator<Motif> comparator) {
		this.maxSize = maxSize;
		this.comparator = comparator;
		motifCollection = new TreeSet<Motif>(comparator);
	}

	public void add(Motif motif) {
		//		if (motif.getStatisticalNumbers().getNumberOfOccurrences() != 1) {
		if (motifCollection.size() < maxSize) {
			motifCollection.add(motif);
		}
		else {
			Motif last = motifCollection.last();
			if (comparator.compare(motif, last) > 0) {
				motifCollection.pollLast();
				motifCollection.add(motif);
			}
		}
		//		}
	}

	public Collection<Motif> getMotifs() {
		return motifCollection;
	}

}
