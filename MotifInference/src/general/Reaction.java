package general;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Reaction
{

	String				ID;
	EC					ec;

	HashSet<Compound>	substrates	= new HashSet<Compound>(2);
	HashSet<Compound>	products	= new HashSet<Compound>(2);

	boolean				valid		= true;
	Color				color;

	HashSet<Reaction>	neighbors	= new HashSet<Reaction>(3);

	public Reaction(String ID)
	{
		this.ID = ID;
	}

	public Reaction(String ID, EC ec)
	{
		this.ID = ID;
		this.ec = ec;
	}

	public String toString()
	{
		return ID;
	}

	public EC getEc()
	{
		return ec;
	}

	public void setEc(EC ec)
	{
		this.ec = ec;
	}

	public String getID()
	{
		return ID;
	}

	public void addSubstrate(Compound c)
	{
		substrates.add(c);
		c.addAsSubstrate(this);
	}

	public void addProduct(Compound c)
	{
		products.add(c);
		c.addAsProduct(this);
	}

	public HashSet<Compound> getSubstrates()
	{
		return substrates;
	}

	public HashSet<Compound> getProducts()
	{
		return products;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	public void addNeighbor(Reaction r)
	{
		neighbors.add(r);
	}

	public HashSet<Reaction> getNeighbors()
	{
		//		if (neighbors != null)
		//		{
		//			return neighbors;
		//		}
		//		neighbors = new HashSet<Reaction>();
		//		for (Compound c : getSubstrates())
		//		{
		//			for (Reaction r : c.getReactionsAsProduct())
		//			{
		//				neighbors.add(r);
		//			}
		//		}
		//		for (Compound c : getProducts())
		//		{
		//			for (Reaction r : c.getReactionsAsSubstrate())
		//			{
		//				neighbors.add(r);
		//			}
		//		}
		return neighbors;
	}

	public static List<Reaction> getReationsFromFileTab(String fileName, boolean considerSubstSubst,
			boolean considerProdProd) throws IOException
	{
		ArrayList<Reaction> reactions = new ArrayList<Reaction>(1000);
		Hashtable<String, Integer> idReactions = new Hashtable<String, Integer>(1000);
		Hashtable<String, Compound> compounds = new Hashtable<String, Compound>(1000);
		Hashtable<String, EC> ecs = new Hashtable<String, EC>(1000);

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		String idReaction, idComp, typeComp, idEC;
		String[] words;
		Integer indexReaction;
		Reaction reaction;
		Compound comp;
		EC ec;
		while ((line = br.readLine()) != null)
		{
			words = line.split("\t");
			idReaction = words[0];
			idComp = words[3];
			typeComp = words[4];
			idEC = words[6];
			if (!idReaction.equals("NA"))
			{
				indexReaction = idReactions.get(idReaction);
				if (indexReaction == null)
				{
					reaction = new Reaction(idReaction);
					idReactions.put(idReaction, reactions.size());
					reactions.add(reaction);
				}
				else
				{
					reaction = reactions.get(indexReaction);
				}
				if (!idEC.equals("NA"))
				{
					ec = ecs.get(idEC);
					if (ec == null)
					{
						ec = new EC(idEC);
						ecs.put(idEC, ec);
					}
					reaction.setEc(ec);
				}
				if (!idComp.equals("NA"))
				{
					comp = compounds.get(idComp);
					if (comp == null)
					{
						comp = new Compound(idComp);
						compounds.put(idComp, comp);
					}
					if (typeComp.toLowerCase().equals("substrate"))
					{
						reaction.addSubstrate(comp);
						for (Reaction r : comp.getReactionsAsProduct())
						{
							reaction.addNeighbor(r);
							r.addNeighbor(reaction);
						}
						if (considerSubstSubst)
						{
							for (Reaction r : comp.getReactionsAsSubstrate())
							{
								reaction.addNeighbor(r);
								r.addNeighbor(reaction);
							}
						}
					}
					else if (typeComp.toLowerCase().equals("product"))
					{
						reaction.addProduct(comp);
						for (Reaction r : comp.getReactionsAsSubstrate())
						{
							reaction.addNeighbor(r);
							r.addNeighbor(reaction);
						}
						if (considerProdProd)
						{
							for (Reaction r : comp.getReactionsAsProduct())
							{
								reaction.addNeighbor(r);
								r.addNeighbor(reaction);
							}
						}
					}

				}
			}
		}
		br.close();
		reactions.trimToSize();
		return reactions;
	}

	//modeNeighborhood = L, only substract to products are neighbors
	//modeNeighborhood = V, subst-prod, subst-subst and prod-prod are neighbors
	public static List<Reaction> getReationsFromFileSBML(String fileName, boolean considerSubstSubst,
			boolean considerProdProd) throws ParserConfigurationException, SAXException, IOException
	{
		ArrayList<Reaction> reactions = new ArrayList<Reaction>(1000);
		Hashtable<String, EC> ecs = new Hashtable<String, EC>(1000);
		Hashtable<String, Compound> compounds = new Hashtable<String, Compound>(1000);
		String str, idEc, reactionId, idComp;
		EC ec;
		Compound comp;
		Reaction reaction;

		Element eProduct, eReactant, eReaction, eHTMLP;
		NodeList listHTMLP, listReactants, listProducts;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(fileName)); //your xml file name here
		NodeList nl1 = doc.getElementsByTagName("reaction");

		for (int i = 0; i < nl1.getLength(); i++)
		{
			eReaction = (Element) nl1.item(i);
			reactionId = eReaction.getAttribute("id");
			reaction = new Reaction(reactionId);
			idEc = null;
			listHTMLP = eReaction.getElementsByTagName("html:p");
			for (int j = 0; j < listHTMLP.getLength(); j++)
			{
				eHTMLP = (Element) listHTMLP.item(j);
				str = eHTMLP.getTextContent();
				if (str.startsWith("PROTEIN_CLASS:"))
				{
					idEc = str.substring(15);
				}
			}
			if (idEc != null)
			{
				ec = ecs.get(idEc);
				if (ec == null)
				{
					ec = new EC(idEc);
					ecs.put(idEc, ec);
				}
				reaction.setEc(ec);
				reactions.add(reaction);

				listReactants = ((Element) eReaction.getElementsByTagName("listOfReactants").item(0)).getElementsByTagName("speciesReference");
				for (int j = 0; j < listReactants.getLength(); j++)
				{
					eReactant = (Element) listReactants.item(j);
					idComp = eReactant.getAttribute("species");
					comp = compounds.get(idComp);
					if (comp == null)
					{
						comp = new Compound(idComp);
						compounds.put(idComp, comp);
					}
					reaction.addSubstrate(comp);
					for (Reaction r : comp.getReactionsAsProduct())
					{
						reaction.addNeighbor(r);
						r.addNeighbor(reaction);
					}
					if (considerSubstSubst)
					{
						for (Reaction r : comp.getReactionsAsSubstrate())
						{
							reaction.addNeighbor(r);
							r.addNeighbor(reaction);
						}
					}
				}
				listProducts = ((Element) eReaction.getElementsByTagName("listOfProducts").item(0)).getElementsByTagName("speciesReference");
				for (int j = 0; j < listProducts.getLength(); j++)
				{
					eProduct = (Element) listProducts.item(j);
					idComp = eProduct.getAttribute("species");
					comp = compounds.get(idComp);
					if (comp == null)
					{
						comp = new Compound(idComp);
						compounds.put(idComp, comp);
					}
					reaction.addProduct(comp);
					for (Reaction r : comp.getReactionsAsSubstrate())
					{
						reaction.addNeighbor(r);
						r.addNeighbor(reaction);
					}
					if (considerProdProd)
					{
						for (Reaction r : comp.getReactionsAsProduct())
						{
							reaction.addNeighbor(r);
							r.addNeighbor(reaction);
						}
					}
				}
			}
		}
		return reactions;
	}
}
