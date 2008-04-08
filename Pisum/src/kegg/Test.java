package kegg;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

class Test
{
	public static void main(String[] args) throws Exception
	{
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();

		String query = args[0];
		//		String[] results = serv.get_genes_by_pathway(query);
		//		String str = serv.btit("ko:K04663");
		//		LinkDBRelation[] links = serv.get_linkdb_by_entry("KO:K00294 KO:K04663", "GO", 1, 10);
		//		links = serv.get_linkdb_by_entry("KO:K00294 KO:K04663", "GB", 1, 10);
		//		System.out.println(str);
		//		Definition[] results = serv.get_genes_by_ko("ko:K04663", "all");
		Definition[] results = serv.get_genes_by_ko("ko:K04663", "hsa");

		for (int i = 0; i < results.length; i++)
		{
			System.out.println(results[i].getEntry_id() + " - " + results[i].getDefinition());
		}
		//		for (int i = 0; i < links.length; i++)
		//		{
		//			System.out.println(links[i].getEntry_id1() + " -> " + links[i].getEntry_id2() + " -> " + links[i].getType()
		//				+ " -> " + links[i].getPath());
		//		}
	}
}
