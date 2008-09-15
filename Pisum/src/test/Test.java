/*
 * Created on 19/06/2008
 */
package test;

import java.util.ArrayList;

import org.biojavax.bio.seq.CompoundRichLocation;
import org.biojavax.bio.seq.RichLocation;
import org.biojavax.bio.seq.SimplePosition;
import org.biojavax.bio.seq.SimpleRichLocation;

public class Test
{
	public static void main(String[] args) {
		RichLocation loc1a = new SimpleRichLocation(new SimplePosition(2), new SimplePosition(4), 0);
		RichLocation loc1b = new SimpleRichLocation(new SimplePosition(5), new SimplePosition(8), 0);
		RichLocation loc2a = new SimpleRichLocation(new SimplePosition(3), new SimplePosition(5), 0);
		RichLocation loc2b = new SimpleRichLocation(new SimplePosition(7), new SimplePosition(8), 0);
		ArrayList a = new ArrayList();
		a.add(loc2a);
		a.add(loc2b);
		CompoundRichLocation loc2 = new CompoundRichLocation(a);
		CompoundRichLocation loc1 = new CompoundRichLocation(a);
		System.out.println(loc1.contains(loc2));
		a.clear();
		a.add(loc1a);
		a.add(loc1b);
		loc1 = new CompoundRichLocation(a);
		System.out.println(loc1.contains(loc2));
	}
}
