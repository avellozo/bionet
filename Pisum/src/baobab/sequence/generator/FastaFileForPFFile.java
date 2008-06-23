/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import baobab.sequence.general.EST;
import baobab.sequence.general.Messages;

public class FastaFileForPFFile
{
	PrintStream	out;

	public FastaFileForPFFile(PrintStream out) {
		this.out = out;
		out.println(Messages.getString("FastaFileForPFFile.header"));
	}

	public FastaFileForPFFile(File fileOut) throws FileNotFoundException {
		this(new PrintStream(fileOut));
	}

	public FastaFileForPFFile(String fileOutName) throws FileNotFoundException {
		this(new PrintStream(new File(fileOutName)));
	}

	public void write(EST est) {
		out.println(est.getSequence().getInternalSymbolList().seqString());
		out.flush();
	}
}
