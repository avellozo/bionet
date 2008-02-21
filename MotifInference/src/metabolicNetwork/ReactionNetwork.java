package metabolicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ReactionNetwork
{

	HashMap<String, Reaction>	reactions	= new HashMap<String, Reaction>();

	public ReactionNetwork()
	{

	}

	public int numberOfColors()
	{
		List<String> colors = new ArrayList<String>();
		for (Iterator iter = reactions.values().iterator(); iter.hasNext();)
		{
			Reaction reaction = (Reaction) iter.next();
			if (!colors.contains(reaction.color))
				colors.add(reaction.color);
		}
		return colors.size();
	}

	public void print()
	{

		for (Iterator iter = reactions.values().iterator(); iter.hasNext();)
		{
			Reaction reaction = (Reaction) iter.next();
			System.out.print(reaction.ID + " [ " + reaction.color + " ]" + ": ");
			String separador = "";
			for (int j = 0; j < reaction.linkedTo.size(); j++)
			{
				System.out.print(separador + reaction.linkedTo.get(j).ID);
				separador = ", ";
			}
			System.out.println();
		}
	}

	public void buildFromSifFile(String fileName)
	{
		FileReader file = null;
		try
		{
			file = new FileReader(new File(fileName));
			BufferedReader buffer = new BufferedReader(file);

			// Each line of the file contains one relation between 2 reactions
			String line = buffer.readLine();
			while (line != null)
			{
				String[] relation = line.split("	linkedWith	");

				if (relation.length != 2)
					throw new RuntimeException(
						"Error during parsing of the SIF file. Relation between 2 reactions was expected at each line, but found "
							+ relation.length + " on line " + line);

				Reaction reaction1 = reactions.get(relation[0]);
				if (reaction1 == null)
				{
					reaction1 = new Reaction(relation[0]);
					reactions.put(reaction1.ID, reaction1);
				}

				Reaction reaction2 = reactions.get(relation[1]);
				if (reaction2 == null)
				{
					reaction2 = new Reaction(relation[1]);
					reactions.put(reaction2.ID, reaction2);
				}

				reaction1.addReactionLink(reaction2);

				line = buffer.readLine();
			}
			file.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void eraseVerticesWithoutColor()
	{
		for (Iterator iter = reactions.values().iterator(); iter.hasNext();)
		{
			Reaction reaction = (Reaction) iter.next();
			if (reaction.color == null || reaction.color.equals("") || reaction.color.equals("NA"))
				reactions.remove(reaction);
		}

	}

	public void loadColorsFrom(String fileName, int threshold)
	{
		FileReader file = null;
		try
		{
			file = new FileReader(new File(fileName));
			BufferedReader buffer = new BufferedReader(file);

			// Each line of the file contains one relation between 2 reactions
			String line = buffer.readLine();
			while (line != null)
			{
				String[] information = line.split("\t");

				Reaction reaction = reactions.get(information[0]);
				if (reaction == null)
				{
					//					 System.out.println("Reaction with ID = "+information[0]+" not found in the network." );
				}
				else
				{
					String color = information[6];
					if (!"NA".equals(color))
					{
						color = color.replace(".", ";");
						String[] colorHierarchy = color.split(";");
						if (threshold <= colorHierarchy.length)
						{
							color = colorHierarchy[0];
							for (int i = 1; i < threshold; i++)
							{
								color = color + "." + colorHierarchy[i];
							}
						}
						else
							color = information[6];

						if (color.contains("-"))
							color = null;

						if (reaction.color != null && !reaction.color.equals(color))
							System.out.println("Duplicated color: Reaction " + reaction.ID + " has color "
								+ reaction.color + " and the file also defines color " + color);
						else
							reaction.color = color; // EC Number of the reaction
					}
				}
				line = buffer.readLine();
			}
			file.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
