/*
 * Created on 25/02/2008
 */
package metabolicNetwork;

public class Motif extends LinkedListReverse<Color>
{

	public Motif(Motif parent, Color color)
	{
		super(parent, color);
	}

	public Color getColor()
	{
		return super.getData();
	}

}
