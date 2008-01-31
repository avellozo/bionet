/*
 * Created on 31/01/2008
 */
package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class MotifNode extends MotifSeed
{
	List<MotifNode>	children		= null;
	Reaction		lastVisitedFor	= null;

	public MotifNode(Reaction reaction, MotifNode parent)
	{
		super(reaction, parent);
		reaction.addNode(this);
	}

	public void createMotifs(Reaction r, int targetHeight, MotifList targetHeightNodes)
	{
		if (r != lastVisitedFor)
		{
			lastVisitedFor = r;
			if (height == targetHeight - 1)
			{
				MotifSeed motif = new MotifSeed(r, this);
				targetHeightNodes.add(motif);
			}
			else
			{
				for (MotifNode child : children)
				{
					child.createMotifs(r, targetHeight, targetHeightNodes);
				}
				createChild(r);
			}
		}
	}

	public MotifNode createChild(Reaction r)
	{
		MotifNode node = new MotifNode(r, this);
		if (children == null)
			children = new ArrayList<MotifNode>();
		children.add(node);
		return node;
	}
}
