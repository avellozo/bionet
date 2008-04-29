/*
 * Created on 03/03/2008
 */
package metabolicNetwork;

import java.io.PrintStream;

public class MotifTrie
{

	short[]	a, a1;
	//	int		sizeAlphabet;
	int		nextFree		= 0;
	int		totalLeafs		= 0;
	int		totalInternals	= 0;
	int[]	repeats			= new int[Short.MAX_VALUE];

	//	int		bitsColor;

	// type: Leaf = 0 and Internal = 1
	// Leaf (10 Bytes): 2B = type + color; 4B= brother; 4B = counter
	// Internal (10 Bytes): 2B = type + color; 4B= brother; 4B = child

	public MotifTrie(int sizeTrie) // Bytes of the array
	//	public MotifTrie(int sizeTrie, int qttyColor) // Bytes of the array
	{
		//		System.out.println("Max Memory before array create " + Runtime.getRuntime().maxMemory());
		//		System.out.println("Total Memory before array create " + Runtime.getRuntime().totalMemory());
		//		System.out.println("Free Memory before array create " + Runtime.getRuntime().freeMemory());
		a = new short[sizeTrie / 2];
		//		bitsColor = 0;
		//		qttyColor--;
		//		while (qttyColor != 0)
		//		{
		//			qttyColor = qttyColor >> 1;
		//			bitsColor++;
		//		}
		newInternal((short) 0);
		//		System.out.println("Max Memory after array create " + Runtime.getRuntime().maxMemory());
		//		System.out.println("Total Memory after array create " + Runtime.getRuntime().totalMemory());
		//		System.out.println("Free Memory after array create " + Runtime.getRuntime().freeMemory());
	}

	//	public MotifTrie(int sizeTrie, int sizeAlphabet)
	//	{
	//		m = new short[sizeTrie];
	//		this.sizeAlphabet = sizeAlphabet;
	//	}
	//
	public int addMotif(short[] motif)
	{
		int pos = 0;
		int nextChild, currentChild;
		short colorNext = 0;
		for (int i = 0; i < motif.length; i++)
		{
			nextChild = getChild(pos);
			currentChild = pos;
			while ((nextChild != 0) && ((colorNext = getColor(nextChild)) < motif[i]))
			{
				currentChild = nextChild;
				nextChild = getBrother(nextChild);
			}
			if (nextChild == 0 || colorNext > motif[i])
			{
				int newChild;
				if (i == motif.length - 1)
				{
					newChild = newLeaf(motif[i]);
				}
				else
				{
					newChild = newInternal(motif[i]);
				}
				if (currentChild == pos)
				{
					setChild(pos, newChild);
				}
				else
				{
					setBrother(currentChild, newChild);
				}
				setBrother(newChild, nextChild);
				pos = newChild;
			}
			else
			{
				pos = nextChild;
				if (i == motif.length - 1)
				{
					incCounterLeaf(pos);
				}
			}
		}
		return pos;
	}

	protected short getColor(int pos) //position of the record in the array
	{
		return (short) (a[pos] & 0x7FFF);
	}

	protected boolean isLeaf(int pos)
	{
		return (a[pos] >= 0);
	}

	protected int getBrother(int pos)
	{
		return ((a[pos + 1] << 16) | (a[pos + 2] & 0xFFFF));
	}

	protected int getChild(int pos)
	{
		if (isLeaf(pos))
		{
			throw new RuntimeException();
		}
		return ((a[pos + 3] << 16) | (a[pos + 4] & 0xFFFF));
	}

	protected void setChild(int pos, int child)
	{
		if (isLeaf(pos))
		{
			throw new RuntimeException();
		}
		a[pos + 3] = (short) (child >> 16);
		a[pos + 4] = (short) (child & 0xFFFF);
	}

	protected void setBrother(int pos, int brother)
	{
		a[pos + 1] = (short) (brother >> 16);
		a[pos + 2] = (short) (brother & 0xFFFF);
	}

	public int getCounter(int pos)
	{
		return ((a[pos + 3] << 16) | (a[pos + 4] & 0xFFFF));
	}

	protected int incCounterLeaf(int pos)
	{
		if (!isLeaf(pos))
		{
			throw new RuntimeException();
		}
		int counter = getCounter(pos);
		counter++;
		a[pos + 3] = (short) (counter >> 16);
		a[pos + 4] = (short) (counter & 0xFFFF);
		if (counter < Short.MAX_VALUE - 1)
		{
			repeats[counter - 1]--;
			repeats[counter]++;
		}
		return counter;
	}

	protected int newLeaf(short color)
	{
		int pos = nextFree;
		nextFree += 5;
		a[pos] = color;
		a[pos + 1] = 0; //brother
		a[pos + 2] = 0; //brother
		a[pos + 3] = 0; //counter
		a[pos + 4] = 0; //counter
		totalLeafs++;
		repeats[0]++;
		return pos;
	}

	protected int newInternal(short color)
	{
		int pos = nextFree;
		nextFree += 5;
		a[pos] = (short) (color | 0x8000);
		a[pos + 1] = 0; //brother
		a[pos + 2] = 0; //brother
		a[pos + 3] = 0; //child
		a[pos + 4] = 0; //child
		totalInternals++;
		return pos;
	}

	public void print(PrintStream p, String[] colorStr, short[] colorQtty, int k, int nv, long motcount)
	{
		print(p, getChild(0), new short[k], 0, colorStr, colorQtty, nv, motcount);
	}

	private void print(PrintStream p, int pos, short[] motif, int i, String[] colorStr, short[] colorQtty, int nv,
			long motcount)
	{
		motif[i] = getColor(pos);
		if (isLeaf(pos))
		{
			int counter = getCounter(pos) + 1;
			int k = i + 1;
			short[] colorQttyMotif = new short[colorQtty.length];
			String str = "";
			int j;
			for (j = 0; j < k; j++)
			{
				if (j != 0)
				{
					str += " ";
				}
				str += colorStr[motif[j]];
				colorQttyMotif[motif[j]]++;
			}
			double pAle = 1;
			for (j = 0; j < colorQtty.length; j++)
			{
				pAle = pAle * comb(colorQtty[j], colorQttyMotif[j]);
			}
			pAle = pAle * counter / (comb(nv, k) * motcount);

			p.println(str + " " + counter + " " + pAle);
		}
		else
		{
			print(p, getChild(pos), motif, i + 1, colorStr, colorQtty, nv, motcount);
		}
		if (getBrother(pos) != 0)
		{
			print(p, getBrother(pos), motif, i, colorStr, colorQtty, nv, motcount);
		}

	}

	public double comb(int n, int k)
	{
		double ret = 1;
		for (int i = 0; i < k; i++)
		{
			ret = ret * (n - i) / (k - i);
		}
		return ret;
	}

}