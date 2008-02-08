/*
 * Created on 06/02/2008
 */
package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;

public class MotifArrayList implements MotifList
{
	ArrayList<Subgraph>	list	= new ArrayList<Subgraph>();

	@Override
	public void add(Subgraph motif)
	{
		list.add(motif);
	}

}
