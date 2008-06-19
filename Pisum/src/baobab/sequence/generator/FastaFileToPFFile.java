/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import baobab.sequence.general.EST;
import baobab.sequence.general.Messages;

public class FastaFileToPFFile
{
	PrintStream	out;

	public FastaFileToPFFile(PrintStream out) {
		this.out = out;
		out.println(Messages.getString("FastaFileToPFFile.header"));
	}

	public FastaFileToPFFile(File fileOut) throws FileNotFoundException {
		this(new PrintStream(fileOut));
	}

	public FastaFileToPFFile(String fileOutName) throws FileNotFoundException {
		this(new PrintStream(new File(fileOutName)));
	}

	public void write(EST est) {
		out.println(est.getSequence().getInternalSymbolList().seqString());
		out.flush();
	}
}
