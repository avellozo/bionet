/*
 * Created on Nov 19, 2010
 */
package general;

import java.util.Collection;
import java.util.TreeSet;

public class BestMotifCollection implements MotifCollection
{

	TreeSet<Motif>	motifCollection	= new TreeSet<Motif>(new ComparatorByZScore());
	int				maxSize;

	public BestMotifCollection(int maxSize) {
		this.maxSize = maxSize;
	}

	public void add(Motif motif) {
		if (motifCollection.size() < maxSize) {
			motifCollection.add(motif);
		}
		else {
			Motif last = motifCollection.last();
			if (motif.getzScore() > last.getzScore()) {
				motifCollection.pollLast();
				motifCollection.add(motif);
			}
		}
	}

	public Collection<Motif> getMotifs() {
		return motifCollection;
	}

}
