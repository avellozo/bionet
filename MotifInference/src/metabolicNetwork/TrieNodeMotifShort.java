/*
 * Created on 27/02/2008
 */
package metabolicNetwork;

public interface TrieNodeMotifShort
{

	public TrieNodeMotifShort addChild(short color, boolean terminal);

	public short getColor();

	public TrieNodeMotifShort getBrother();

	public void setBrother(TrieNodeMotifShort node);
}
