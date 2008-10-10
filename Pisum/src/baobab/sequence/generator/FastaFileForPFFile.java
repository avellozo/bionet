/*
 * Created on 19/06/2008
 */
package baobab.sequence.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import baobab.sequence.general.BioSql;

public class FastaFileForPFFile
{
	String		fileName;
	int			lineWidth;
	//	ByteArrayOutputStream	baos	= new ByteArrayOutputStream(500000000);
	//	PrintStream				out = new PrintStream(baos);
	PrintStream	out;

	public FastaFileForPFFile(String fileOutName, String header, int lineWidth) throws FileNotFoundException {
		this.out = new PrintStream(new File(fileOutName));
		out.print(header);
		this.fileName = fileOutName;
		this.lineWidth = lineWidth;
	}

	public int write(Integer seqId) {
		String stringSeq = BioSql.getStringSeq(seqId);
		int length = stringSeq.length();

		for (int pos = 0; pos < length; pos++) {
			if (pos % lineWidth == 0) {
				out.println();
			}
			out.print(stringSeq.charAt(pos));
		}
		out.flush();
		return length;
	}

	public void restart() throws FileNotFoundException {
		out.close();
		out = new PrintStream(new FileOutputStream(fileName, true));
	}

	public void flush() {

	}

}
