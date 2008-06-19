/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import baobab.sequence.general.EST;

public class PFFile
{
	PrintStream	out;

	public PFFile(PrintStream out) {
		this.out = out;
	}

	public PFFile(File fileOut) throws FileNotFoundException {
		this(new PrintStream(fileOut));
	}

	public PFFile(String fileOutName) throws FileNotFoundException {
		this(new PrintStream(new File(fileOutName)));
	}

	public void write(EST est) {
		out.flush();
	}
}
