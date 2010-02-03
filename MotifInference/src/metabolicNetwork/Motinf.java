// works with hash table with mapping of colors to reduce key size
// Design and implementation Cinzia Pizzi, 2007

package metabolicNetwork;

import general.Color;
import general.Reaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import trie.MotifTrie;

public class Motinf
{
	static long	motCount;

	//file.col/file.xml, k, threshold {L,V,l,v} {y,n}
	//args[3] = {L,V,l,v} Method neighbors.
	// L consider only neighbors reactions that share compounds as substract and procuct
	// V consider subst-subst and prod-prod also
	//args[4] = {y,n} Print details? Default is false
	public static void main(String args[]) {

		long time = System.currentTimeMillis();

		if (args.length < 5) {
			System.out.println("usage:  java -jar motinf.jar [file.col,file.xml] k threshold {L,V,l,v} {y,n}");
			return;
		}
		String fileName = args[0];
		int k = (Integer.valueOf(args[1])).intValue();
		int thre = (Integer.valueOf(args[2])).intValue();
		boolean considerSubsSubs = (args[3].toLowerCase().equals("v"));
		boolean printDetails = args.length > 4 && args[4].equals("y");

		List<Reaction> reactions;

		if (fileName.endsWith(".col")) {
			try {
				reactions = Reaction.getReationsFromFileTab(fileName, considerSubsSubs, considerSubsSubs);
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		else {
			try {
				reactions = Reaction.getReationsFromFileSBML(fileName, considerSubsSubs, considerSubsSubs);
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
				return;
			}
			catch (SAXException e) {
				e.printStackTrace();
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		Color[] colors = assignColors(reactions, thre);

		Color.sortByNumNodes(colors);
		for (short i = 0; i < colors.length; i++) {
			colors[i].setId(i);
		}

		int maxSizeTrie = (int) (Runtime.getRuntime().maxMemory() * 2 / 3);
		if (maxSizeTrie < 0) {
			maxSizeTrie = Integer.MAX_VALUE - 1;
		}
		//		System.out.println("Array size " + maxSizeTrie);
		MotifTrie trie = new MotifTrie(maxSizeTrie);

		//		System.out.println("Time to create structures " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

		int n = 0;
		motCount = 0;
		Reaction[] motifPrefix = new Reaction[k];
		//		Motinf m = new Motinf();
		Reaction.sortByColorId(reactions);
		Color colorOld = null;
		int totalLeafs = 0;
		for (Reaction r : reactions) {
			if (r.isValid()) {
				if (colorOld != r.getColor()) {
					totalLeafs += trie.totalLeafs;
					//					System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
					trie.clear();
					colorOld = r.getColor();
				}
				n++;
				r.setInvalid();
				motifPrefix[0] = r;
				createMotif(motifPrefix, 1, trie);
			}
		}

		totalLeafs += trie.totalLeafs;
		//		System.out.println("Trie color: " + colorOld + " with " + trie.totalLeafs + " leafs.");
		//		System.out.println();
		System.out.println("Time to calculate motifs " + (System.currentTimeMillis() - time) + "ms");
		System.out.println("Total motifs of size " + k + ": " + motCount);
		System.out.println("Reactions: " + n);
		if (considerSubsSubs) {
			System.out.println("Considering the edges substract-substract and product-product.");
		}
		System.out.println("Colors with threshold " + thre + ": " + colors.length);
		System.out.println("Trie with " + totalLeafs + " leafs.");
		if (printDetails) {
			for (Color color : colors) {
				System.out.println(color.getDescription() + "\t" + color.getNumNodes());
			}
			//			System.out.println("Motifs: ");
			//			trie.print(System.out, colors, k, n, motCount);
			//			System.out.println("Occurrences:");
			//			int repeats[] = trie.repeats;
			//			for (int j = 0; j < repeats.length; j++)
			//			{
			//				if (repeats[j] != 0)
			//					System.out.println((j + 1) + " " + repeats[j]);
			//			}
		}

		//			System.out.println("motifs leaves " + TrieLeafMotifShort.counterLeafs);
		//			System.out.println("motifs internal nodes " + TrieInternalNodeMotifShort.counterInternalNodes);
		//			int repeats[] = TrieLeafMotifShort.repeats;
		System.out.println();

	}

	private static void createMotif(Reaction[] motifPrefix, int k1, MotifTrie trie) {
		if (k1 == motifPrefix.length) {
			short[] motif = new short[motifPrefix.length];
			for (int i = 0; i < motifPrefix.length; i++) {
				motif[i] = motifPrefix[i].getColor().getId();
			}
			Arrays.sort(motif);
			motCount++;
			trie.addMotif(motif);
		}
		else {
			ArrayList<Reaction> returnValids = new ArrayList<Reaction>();
			Collection<Reaction> neighborsReactionI;
			for (int i = 0; i < k1; i++) {
				neighborsReactionI = motifPrefix[i].getNeighbors();
				for (Reaction r : neighborsReactionI) {
					if (r.isValid()) {
						returnValids.add(r);
						r.setValid(false);
						motifPrefix[k1] = r;
						createMotif(motifPrefix, k1 + 1, trie);
					}
				}
			}
			for (Reaction r : returnValids) {
				r.setValid(true);
			}
		}
	}

	//return in each position of String[] the EC number truncated
	private static Color[] assignColors(List<Reaction> reactions, int thre) {
		String[] ecs;
		Hashtable<String, Color> mapECColor = new Hashtable<String, Color>();
		ArrayList<Color> colors = new ArrayList<Color>();
		String ec;
		short nextColor = 0;
		Color color;
		int totalSize = reactions.size();
		Reaction r;
		for (int j = 0; j < totalSize; j++) {
			r = reactions.get(j);
			if (r.getEc() != null && (ecs = r.getEc().getId().split("\\.")).length >= thre) {
				ec = ecs[0];
				for (int i = 1; i < thre; i++) {
					ec = ec + "." + ecs[i];
				}
				if (ec.indexOf("-") == -1) {
					color = mapECColor.get(ec);
					if (color == null) {
						color = new Color(nextColor++, ec);
						colors.add(color);
						mapECColor.put(ec, color);
					}
					r.setColor(color);
					r.setValid(true);
				}
				else {
					r.setInvalid();
					reactions.remove(r);
					j--;
					totalSize--;
				}
			}
			else {
				r.setInvalid();
				reactions.remove(r);
				j--;
				totalSize--;
			}
		}
		return colors.toArray(new Color[colors.size()]);
	}
}
