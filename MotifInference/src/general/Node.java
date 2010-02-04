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

public class Node
{

	private static final char	UNIQUE_COLOR	= 'U';
	private static final char	REMOVE_NODE		= 'R';
	private static final char	PROTEIN_ID		= 'P';
	String						ID;
	boolean						valid			= true;
	Color						color;
	ArrayList<Node>				neighbors		= new ArrayList<Node>(3);

	//	public Node(String ID) {
	//		this.ID = ID;
	//	}
	//
	public Node(String ID, Color color) {
		this.ID = ID;
		setColor(color);
	}

	public String toString() {
		return ID;
	}

	public String getID() {
		return ID;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		color.incNumNodes();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid() {
		//		if (!isValid()) {
		valid = true;
		//			ArrayList<Node> nodesToRemove = new ArrayList<Node>();
		//			for (Node node : neighbors) {
		//				node.addNeighbor(this);
		//				if (!node.isValid()) {
		//					nodesToRemove.add(node);
		//				}
		//			}
		//			for (Node node : nodesToRemove) {
		//				removeNeighbor(node);
		//			}
		//		}
	}

	public void setInvalid() {
		//		for (Node node : neighbors) {
		//			node.removeNeighbor(this);
		//		}
		this.valid = false;
	}

	public void addNeighbor(Node node) {
		if (node != this) {
			neighbors.add(node);
		}
	}

	public boolean removeNeighbor(Node r) {
		return neighbors.remove(r);
	}

	public Collection<Node> getNeighbors() {
		return neighbors;
	}

	public int getDegree() {
		return getNeighbors().size();
	}

	public void trimToSize() {
		neighbors.trimToSize();
	}

	public static List<Node> createGraph(String fileName, String[] organismsArray, String withoutColorStr)
			throws IOException {
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
		Hashtable<String, Color> colors = new Hashtable<String, Color>();
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		List<Node> ret = new ArrayList<Node>();
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
				colorA = colors.get(colorIdA);
				if (colorA == null) {
					colorA = new Color(colorIdA);
					colors.put(colorIdA, colorA);
				}
				colorB = colors.get(colorIdB);
				if (colorB == null) {
					colorB = new Color(colorIdB);
					colors.put(colorIdB, colorB);
				}
				nodeA = nodes.get(idA);
				if (nodeA == null) {
					nodeA = new Node(idA, colorA);
					nodes.put(idA, nodeA);
					ret.add(nodeA);
				}
				nodeB = nodes.get(idB);
				if (nodeB == null) {
					nodeB = new Node(idB, colorB);
					nodes.put(idB, nodeB);
					ret.add(nodeB);
				}
				nodeA.addNeighbor(nodeB);
				nodeB.addNeighbor(nodeA);
			}
		}
		return ret;
	}

	public static void sortByColorQtty(List<Node> nodes) {
		Collections.sort(nodes, new NodeComparatorByColorQtty());
	}

	public static void sortByColorId(List<Node> nodes) {
		Collections.sort(nodes, new NodeComparatorByColorId());
	}

}

class NodeComparatorByColorQtty implements Comparator<Node>
{
	public int compare(Node o1, Node o2) {
		return o2.getColor().getNumNodes() - o1.getColor().getNumNodes();
	}
}

class NodeComparatorByColorId implements Comparator<Node>
{
	public int compare(Node o1, Node o2) {
		return o1.getColor().getId() - o2.getColor().getId();
	}
}
