package br.ufms.biocomp.metabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class Reaction {

	String ID;
	String color;
	List<Reaction> linkedTo = new ArrayList<Reaction>();
	
	public Reaction(String ID){
		this.ID = ID;
	}
	
	public void addReactionLink(Reaction r){
		if( !linkedTo.contains(r) ){
			linkedTo.add(r);
			r.addReactionLink(this);			
		}
	}	
}
