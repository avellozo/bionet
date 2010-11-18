/*
 * Created on Nov 16, 2010
 */
package general;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings("serial")
public class Graph extends ArrayList<Node>
{

	private static final char	UNIQUE_COLOR	= 'U';
	private static final char	REMOVE_NODE		= 'R';
	private static final char	PROTEIN_ID		= 'P';

	protected int				edgesQtty		= 0;

	protected ArrayList<Color>	colors			= new ArrayList<Color>();

	protected Graph() {
		super();
	}

	protected Graph(Collection< ? extends Node> c) {
		super(c);
	}

	protected Graph(int initialCapacity) {
		super(initialCapacity);
	}

	public Graph(String fileName, String[] organismsArray, String withoutColorStr) throws IOException {
		super();
		char withoutColor;
		if (!withoutColorStr.equals(PROTEIN_ID + "") && !withoutColorStr.equals(REMOVE_NODE + "")
			&& !withoutColorStr.equals(UNIQUE_COLOR + "")) {
			throw new RuntimeException("Parameter 'withoutColor' invalid:" + withoutColorStr);
		}
		else {
			withoutColor = withoutColorStr.charAt(0);
		}

		List<String> organisms = Arrays.asList(organismsArray);
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		String[] columnValues;
		String idA, idB, taxA, taxB, colorIdA, colorIdB;
		Hashtable<String, Color> colorsHash = new Hashtable<String, Color>();
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		Color colorA, colorB;
		Node nodeA, nodeB;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
			columnValues = line.split("\t");
			if (columnValues.length != 6) {
				System.out.println("Error at line:" + line);
				continue;
			}
			taxA = columnValues[2];
			taxB = columnValues[3];
			if (organisms.isEmpty() || (organisms.contains(taxA) && organisms.contains(taxB))) {
				idA = columnValues[0];
				idB = columnValues[1];
				colorIdA = columnValues[4];
				colorIdB = columnValues[5];
				if (colorIdA == null || colorIdA.length() == 0 || colorIdA.equals("-")) {
					switch (withoutColor){
						case PROTEIN_ID:
							colorIdA = idA;
							break;
						case REMOVE_NODE:
							continue;
						case UNIQUE_COLOR:
							colorIdA = "-";
							break;
					}
				}
				if (colorIdB == null || colorIdB.length() == 0 || colorIdB.equals("-")) {
					switch (withoutColor){
						case PROTEIN_ID:
							colorIdB = idB;
							break;
						case REMOVE_NODE:
							continue;
						case UNIQUE_COLOR:
							colorIdB = "-";
							break;
					}
				}
				colorA = colorsHash.get(colorIdA);
				if (colorA == null) {
					colorA = new Color(colorIdA);
					colorsHash.put(colorIdA, colorA);
					colors.add(colorA);
				}
				colorB = colorsHash.get(colorIdB);
				if (colorB == null) {
					colorB = new Color(colorIdB);
					colorsHash.put(colorIdB, colorB);
					colors.add(colorB);
				}
				nodeA = nodes.get(idA);
				if (nodeA == null) {
					nodeA = new Node(idA, colorA, this);
					nodes.put(idA, nodeA);
					this.add(nodeA);
				}
				nodeB = nodes.get(idB);
				if (nodeB == null) {
					nodeB = new Node(idB, colorB, this);
					nodes.put(idB, nodeB);
					this.add(nodeB);
				}
				if (nodeA.addNeighbor(nodeB)) {
					edgesQtty++;
					nodeB.addNeighbor(nodeA);
				}
			}
		}
		if (getNumberOfColors() >= Short.MAX_VALUE) {
			throw new RuntimeException("Error: more than 2^15-1 colors.");
		}
		colors.trimToSize();
	}

	public int getNumberOfNodes() {
		return this.size();
	}

	public double p() {
		return getNumberOfEdges() * 2 / (getNumberOfNodes() * (getNumberOfNodes() - 1));
	}

	public int getNumberOfEdges() {
		return edgesQtty;
	}

	public int getNumberOfColors() {
		return colors.size();
	}

	private static ColorComparatorByNodeQtty	colorComparatorByNodeQtty	= new ColorComparatorByNodeQtty();

	public void setColorIdByColorOccurrences() {
		Collections.sort(colors, colorComparatorByNodeQtty);
		short i = 1;
		for (Color color : colors) {
			color.setId(i++);
		}
	}

	private static ColorComparatorId	colorComparatorId	= new ColorComparatorId();

	public void sortByColorId() {
		Collections.sort(this, colorComparatorId);
	}
}

class ColorComparatorByNodeQtty implements Comparator<Color>
{
	public int compare(Color o1, Color o2) {
		return o1.getNumNodes() - o2.getNumNodes();
	}
}

class ColorComparatorId implements Comparator<Node>
{
	public int compare(Node o1, Node o2) {
		return o1.getColor().getId() - o2.getColor().getId();
	}
}
