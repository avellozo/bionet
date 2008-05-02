/*
 * Created on 26/02/2008
 */
package trie;

public interface TrieNodeComparator<O>
{

	public int compareTo(TrieNode<O> node1, TrieNode<O> node2);

	public int compareTo(TrieNode<O> node, O data, boolean terminal);

	public int compareTo(O data1, boolean terminal1, O data2, boolean terminal2);

}
