/*
 * Created on 26/08/2008
 */
package baobab.sequence.generator;

import baobab.sequence.dbExternal.EC;
import baobab.sequence.general.Gene;

public interface AssociationGeneToEC
{
	public Gene getGene();

	public EC getEC();

	public float getWeight();
}
