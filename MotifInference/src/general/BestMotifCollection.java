/*
 * Created on Nov 19, 2010
 */
package general;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class BestMotifCollection implements MotifCollection
{

	TreeSet<Motif>	motifCollection	= new TreeSet<Motif>(new ComparatorByZScore());
	int				maxSize;

	public BestMotifCollection(int maxSize, Comparator<Motif> comparator) {
		this.maxSize = maxSize;
		motifCollection = new TreeSet<Motif>(comparator);
	}

	public void add(Motif motif) {
		if (motifCollection.size() < maxSize) {
			motifCollection.add(motif);
		}
		else {
			Motif last = motifCollection.last();
			if (motif.getStatisticalNumbers().getZScore() > last.getStatisticalNumbers().getZScore()) {
				motifCollection.pollLast();
				motifCollection.add(motif);
			}
		}
	}

	public Collection<Motif> getMotifs() {
		return motifCollection;
	}

}
