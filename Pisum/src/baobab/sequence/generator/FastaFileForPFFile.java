/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.biojava.bio.seq.Sequence;

import baobab.sequence.general.Messages;

public class FastaFileForPFFile
{
	PrintStream	out;
	String		fileName;

	public FastaFileForPFFile(String fileOutName) throws FileNotFoundException {
		this.out = new PrintStream(new File(fileOutName));
		out.println(Messages.getString("FastaFileForPFFile.header"));
		fileName = fileOutName;
	}

	public void write(Sequence seq) {
		int length = seq.length();
		int lineWidth = Integer.parseInt(Messages.getString("FastaFileForPFFile.lineWidth"));

		for (int pos = 1; pos <= length; pos += lineWidth) {
			int end = Math.min(pos + lineWidth - 1, length);
			out.println(seq.subStr(pos, end));
		}
		out.flush();
	}

	public void restart() throws FileNotFoundException {
		out.close();
		out = new PrintStream(new FileOutputStream(fileName, true));
	}

}
