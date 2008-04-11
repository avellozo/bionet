/*
 * Created on 09/04/2008
 */
package kegg;

import java.util.Map;

import bionet.general.Gene;

public interface GeneKoMap extends Map<Gene, KO>
{

	public String getMapId();

}
