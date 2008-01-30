package br.ufms.biocomp.metabolicNetwork;

public class MotifSearcher {

	public static void main(String[] args) 
	{
		ReactionNetwork network = new ReactionNetwork();
		//network.buildFromSifFile("T:\\Trabalho em Lyon\\MotifInference\\Examples\\reaction_graph_motus_coli.sif");
		//network.loadColorsFrom("T:\\Trabalho em Lyon\\MotifInference\\Examples\\primCpdsSmmReactionsCompounds.col", 3);

		network.buildFromSifFile("T:\\Trabalho em Lyon\\MotifInference\\Examples\\exemplo1.sif");
		network.loadColorsFrom("T:\\Trabalho em Lyon\\MotifInference\\Examples\\exemplo1.col", 3);		
		network.eraseVerticesWithoutColor();
		// network.print();
		
		System.out.println("Número de cores: " + network.numberOfColors());
		// Defines the size of the motif to search.
		int k = 3;
		
		
		
		
	}	
}
