// works with hash table with mapping of colors to reduce key size
// Design and implementation Cinzia Pizzi, 2007

package metabolicNetwork;

import general.MotifTrie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;


public class motif_inference_v7
{

	static int[][]	matrix;

	static int		nV, count, k;

	public static void apsp()
	{
		/*
		 * Function to compute all pairs shortest path
		 */

		for (int i = 0; i < nV; i++)
			for (int j = 0; j < nV; j++)
				if (matrix[i][j] == 0)
					matrix[i][j] = 1000;

		for (int k = 0; k < nV; k++)
			for (int i = 0; i < nV; i++)
				for (int j = 0; j < nV; j++)
					if (matrix[i][k] + matrix[k][j] < matrix[i][j])
						matrix[i][j] = matrix[i][k] + matrix[k][j];

	}

	public static void main(String args[])
	{

		String filename;
		String filename2;
		String line;
		int[] ok = new int[nV]; // this vector holds value 1 if the row i has at least k-1 neighbors at distance <k
		int sum;
		BufferedReader br; // used to read files
		BufferedWriter fw; // used to write files
		int min, help;
		int step;
		int sort;
		int thre;
		//		System.out.println("Max Memory " + Runtime.getRuntime().maxMemory());
		//		System.out.println("Total Memory " + Runtime.getRuntime().totalMemory());
		//		System.out.println("Free Memory " + Runtime.getRuntime().freeMemory());
		int maxSizeTrie = (int) (Runtime.getRuntime().maxMemory() * 2 / 3);
		if (maxSizeTrie < 0)
			maxSizeTrie = Integer.MAX_VALUE - 1;
		//		maxSizeTrie = 1400 * 1000000;
		System.out.println("Array size " + maxSizeTrie);

		// Read the parameter (to be done properly!!!)
		filename = args[0];
		k = (Integer.valueOf(args[1])).intValue();
		step = (Integer.valueOf(args[2])).intValue();
		sort = (Integer.valueOf(args[3])).intValue();
		thre = (Integer.valueOf(args[4])).intValue();
		filename2 = args[5];

		System.out.println("input " + args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " "
			+ args[5]);

		// Set up time variables
		Date d1 = new Date();
		long dstart = d1.getTime();
		long ptime, dend;

		try
		{

			/***********************************************************************************************************
			 * read the graph topology from the file args[0].sif new version
			 */
			String[] sword;
			Hashtable ht = new Hashtable(600);
			Hashtable htcolors = new Hashtable(100);
			//			Hashtable htmotifs = new Hashtable(1000);
			//			TrieNodeMotifShort motifRoot = new TrieInternalNodeMotifShort((short) -1);
			Hashtable colormap = new Hashtable(500);

			br = new BufferedReader(new FileReader(args[0] + ".col"));
			fw = new BufferedWriter(new FileWriter(args[0] + ".incol"));
			String filtered = new String();
			int index = 0;
			short colnum = 0;
			while ((line = br.readLine()) != null)
			{

				sword = line.split("\t");
				index = 0;

				if (!sword[6].equals("NA"))
				{

					for (int i = 0; i < thre; i++)
					{
						index = sword[6].indexOf(".", index + 1);
					}
					if (index != -1)
					{
						filtered = sword[6].substring(0, index);
					}
					else
					{
						filtered = sword[6];
					}
					if (filtered.indexOf("-") == -1)
					{
						htcolors.put(sword[0], filtered);
						//System.out.println(sword[0]+" "+filtered+" "+filtered.indexOf("-"));
						//System.in.read();
						fw.write(sword[0] + " " + filtered + "\n");

						if (!colormap.containsKey(filtered))
						{
							colormap.put(filtered, new Short(colnum));
							colnum++;
						}

					}
				}

			}
			br.close();
			fw.flush();
			fw.close();
			System.out.println("color num " + colnum);

			String[] inverseColorMap = new String[colnum];
			Enumeration e = colormap.keys();
			String s;
			Short code;
			while (e.hasMoreElements())
			{
				s = (String) e.nextElement();
				code = (Short) colormap.get(s);
				inverseColorMap[code.intValue()] = s;
			}

			br = new BufferedReader(new FileReader(filename2));

			short[] colorQtty = new short[colnum];
			nV = 0;
			while ((line = br.readLine()) != null)
			{
				sword = line.split("\t");
				if (line.indexOf("linked") != -1)
				{
					if (htcolors.containsKey(sword[0]) && htcolors.containsKey(sword[2]))
					{
						if (ht.containsKey(sword[0]) == false)
						{
							ht.put(sword[0], String.valueOf(nV));
							nV++;
							colorQtty[(Short) colormap.get((String) htcolors.get(sword[0]))]++;
						}
						if (ht.containsKey(sword[2]) == false)
						{
							ht.put(sword[2], String.valueOf(nV));
							nV++;
							colorQtty[(Short) colormap.get((String) htcolors.get(sword[2]))]++;
						}

					}
				}

			}
			br.close();

			System.out.println(nV);

			// set up global matrices
			matrix = new int[nV][nV];

			// initialize the matrix
			for (int i = 0; i < nV; i++)
			{
				for (int j = 0; j < nV; j++)
					matrix[i][j] = 0;
			}

			br = new BufferedReader(new FileReader(filename2));
			fw = new BufferedWriter(new FileWriter(filename + ".in"));
			int n1, n2;
			Short[] colors = new Short[nV];

			while ((line = br.readLine()) != null)
			{
				if (line.indexOf("linked") != -1)
				{
					sword = line.split("\t");
					if (ht.containsKey(sword[0]) && ht.containsKey(sword[2]))
					{
						n1 = (Integer.valueOf((String) ht.get(sword[0]))).intValue();
						n2 = (Integer.valueOf((String) ht.get(sword[2]))).intValue();

						matrix[n1][n2] = 1;
						matrix[n2][n1] = 1;
						colors[n1] = (Short) colormap.get((String) htcolors.get(sword[0]));
						colors[n2] = (Short) colormap.get((String) htcolors.get(sword[2]));
						fw.write(sword[0] + " " + sword[2] + "\n");
					}
				}

			}
			br.close();
			fw.flush();
			fw.close();

			/** ****************************************************************************************************************************** */

			Date d2 = new Date();
			ptime = d2.getTime();
			System.out.println("time to read data " + (ptime - dstart));

			// compute all pairs shortest path
			apsp();
			Date d3 = new Date();
			dend = d3.getTime();
			System.out.println("time to all pairs " + (dend - ptime));

			// write computed apsp
			/*fw = new BufferedWriter(new FileWriter("apsp"+filename+".dat"));

			for(int i=0;i<nV;i++){
				for(int j=0;j<nV;j++)
					fw.write(matrix[i][j]+"\t");
					fw.write("\n");
			}
			fw.flush();
			fw.close();
			*/

			d2 = new Date();
			ptime = d2.getTime();

			// check which vertexes are connected to at least k-1 other vertexes at distance <k
			ok = new int[nV];

			/***********************************************************************************************************
			 * sort the matrix
			 */

			int[] c1 = new int[nV];
			int[] c2 = new int[nV];
			int[] c3 = new int[nV];
			int[] c4 = new int[nV];
			int[] c5 = new int[nV];
			int[] c6 = new int[nV];
			int somma = 0;

			for (int i = 0; i < nV; i++)
			{
				c1[i] = 0;
				c2[i] = 0;
				c3[i] = 0;
				c4[i] = 0;
				c5[i] = 0;
				c6[i] = 0;
			}
			if (sort != 0)
			{
				// check which vertexes are connected to at least k-1 other vertexes at distance <k
				// and also which vertexes have one color in the motif to search
				for (int i = 0; i < nV; i++)
				{
					sum = 0;

					for (int j = i + 1; j < nV; j++)
					{
						if (matrix[i][j] < k)
							sum++;
					}
					c2[i] = sum; // c2 holds the number of elements at distance less than k above the diagonal

					somma = 0;
					for (int j = 0; j < nV; j++)
					{
						if (matrix[i][j] < k)
							somma++;
					}
					c1[i] = somma; //  c1 holds the number of elements at distance less than k

					c3[i] = c1[i];
					c4[i] = i;
					//System.out.println("node "+i+" = "+sum);
					if (sum >= k - 1)
						ok[i] = 1;
					else
						ok[i] = 0;
				}

				// c3 contains the number of neighbors for each row i
				// c4 contains the indexes corresponding to c3
				int[] bighelp = new int[nV];
				Short colhelp;
				for (int j = 0; j < nV - 1; j++)
				{
					min = j;
					// find the row in the not yet sorted matrix such that the neighbors are the least
					for (int t = j + 1; t < nV; t++)
					{
						if (c3[t] < c3[min])
						{
							min = t;
						}
					}
					// help contains the number of neighbors of row j to be swapped
					help = c3[j];
					colhelp = colors[j];
					// bighelp contains row j
					for (int t = 0; t < nV; t++)
					{
						bighelp[t] = matrix[j][t];
					}

					// assign to c3[j] the value of the neighbors of row min
					c3[j] = c3[min];
					colors[j] = colors[min];
					// copy row min in row j
					for (int t = 0; t < nV; t++)
					{
						matrix[j][t] = matrix[min][t];
					}
					// c3[min] now hold the number of neighbors of row j
					c3[min] = help;
					colors[min] = colhelp;
					//help has now the index of the row at position j
					help = c4[j];
					//c4[j] hold the index of the row that is set there
					c4[j] = min;
					//c4[min] has now the index of the row at position j
					c4[min] = help;

					// copy row j in row min
					for (int t = 0; t < nV; t++)
					{
						matrix[min][t] = bighelp[t];
					}
					//copy column j in bighelp
					for (int t = 0; t < nV; t++)
					{
						bighelp[t] = matrix[t][j];
					}
					//copy column  min in column j
					for (int t = 0; t < nV; t++)
					{
						matrix[t][j] = matrix[t][min];
					}
					// copy column j in column min
					for (int t = 0; t < nV; t++)
					{
						matrix[t][min] = bighelp[t];
					}

					if (sort == 2)
					{
						for (int t = j + 1; t < nV; t++)
						{
							if (matrix[t][j] < k)
							{
								c3[t]--;
							}
						}
					}
				}

				// write computed apsp
				fw = new BufferedWriter(new FileWriter(filename + ".dat.apsp2"));

				for (int i = 0; i < nV; i++)
				{
					//System.out.println("color["+i+"]="+colors[i]);
					for (int j = 0; j < nV; j++)
						fw.write(matrix[i][j] + "\t");
					fw.newLine();
				}
				fw.flush();
				fw.close();
			}

			/***********************************************************************************************************
			 * end sort the matrix
			 */

			for (int i = 0; i < nV; i++)
			{
				sum = 0;
				for (int j = i + 1; j < nV; j++)
				{
					if (matrix[i][j] < k)
						sum++;
				}
				c5[i] = sum;

				somma = 0;
				for (int j = 0; j < nV; j++)
				{
					if (matrix[i][j] < k)
					{
						somma++;
					}
					else
					{
						matrix[i][j] = 0;
					}
				}
				c6[i] = somma;
				if (sum >= k - 1)
				{
					ok[i] = 1;
				}
				else
				{
					ok[i] = 0;
				}
			}

			fw = new BufferedWriter(new FileWriter("summary"));
			fw.write("tot \t diag \t sort\t sort2 \t sdiag \n");
			for (int i = 0; i < nV; i++)
			{
				fw.write(c1[i] + "\t" + c2[i] + "\t" + c3[i] + "\t" + c5[i] + "\t" + c6[i] + "\n");
			}

			fw.flush();
			fw.close();

			int[] candidates = new int[nV];
			line = new String();

			int q_index;

			//Hashtable ht = new Hashtable();
			short[] list = new short[k];

			//int[] lookahed = new int[nV];

			fw = new BufferedWriter(new FileWriter("v7output_" + k + ".out"));

			long motcount = 0;
			//			int gc = 0;

			int start, stop;
			start = (step - 1) * 100;
			stop = step * 100;
			if (stop > nV - k + 1)
			{
				stop = nV - k + 1;
			}

			if (step == -1)
			{
				start = 0;
				stop = nV;
			}

			int pcount = 0;

			int[] queue = new int[nV];
			int[] incandidate = new int[nV];
			int[] addwith = new int[nV];

			int firstfree = 0;

			for (int j = 0; j < nV; j++)
			{
				incandidate[j] = 0;
				addwith[j] = -1;
			}

			int[] pointers = new int[k];
			for (int j = 0; j < k; j++)
			{
				pointers[j] = -1;
			}

			MotifTrie trie = new MotifTrie(maxSizeTrie);

			for (int i = start; i < stop; i++)
			{

				//				System.gc();
				if (ok[i] == 1)
				{

					//					System.out.println("node " + i + " id ok");
					pcount = 0;
					count = 0;

					// set up list of candidates at distance k from node i
					for (int j = i + 1; j < nV; j++)
					{

						if (matrix[i][j] != 0)
						{
							candidates[count] = j;
							count++;
						}

					}

					for (int j = 0; j < nV; j++)
					{
						addwith[j] = -1;
					}
					for (int j = 0; j < k; j++)
					{
						pointers[j] = -1;
					}

					// sort the candidates according to their distance to the node
					// maybe no more necessary
					/*					min = 0;
					for(int j=0;j<count-1;j++){
						min=j;
						for(int t=j+1;t<count;t++){
							if(matrix[i][candidates[t]]<matrix[i][candidates[min]]){min=t;}
						}
						help = candidates[j];
						candidates[j] = candidates[min];
						candidates[min]=help;
					}


					min = matrix[i][candidates[0]];
					int conta = 1;
					while(conta<count){
						if(matrix[i][candidates[conta]]==min){
							conta++;
						}
						else{
							if(matrix[i][candidates[conta]]==min+1){
								min++;
								conta++;
							}
							else{
								count=conta-1;
							}
						}
					}
					// end maybe no more necessary (if only conneceted set are examined)

					*/
					//					System.out.println("candidates:" + count);
					for (int j = 0; j < count; j++)
					{
						incandidate[candidates[j]] = 1;
					}

					pointers[0] = 0;
					queue[0] = i;
					firstfree = 0; // point to the last inserted node in the queue. need to be incremented to put a new node
					index = 0; // keep track of the last pointer set up, when equal to k-1 I have a motif to report
					q_index = 0; // index of last marked node

					for (int j = i + 1; j < nV; j++)
					{
						if (matrix[i][j] == 1 && incandidate[j] == 1 && addwith[j] == -1)
						{ // && ok[j]??
							firstfree++;
							queue[firstfree] = j;
							addwith[j] = queue[pointers[0]];
							//System.out.println("add children "+j+" addwith "+queue[pointers[index]]);
						}
					}

					while (index != -1)
					{

						// at this point k-1 pointers has been set
						// the queue contains all marked nodes and their childrens, but only if they occur in the candidate set
						if (index == k - 1)
						{
							for (int j = 0; j < k; j++)
							{
								list[j] = colors[queue[pointers[j]]];
							}
							//System.out.println("list size "+list.size());
							Arrays.sort(list);

							motcount++;
							pcount++;

							trie.addMotif(list);
							//							TrieNodeMotifShort node = motifRoot;
							//							for (int j = 0; j < k; j++)
							//							{
							//								if (j == k - 1)
							//								{
							//									node = node.addChild(list[j], true);
							//								}
							//								else
							//								{
							//									node = node.addChild(list[j], false);
							//								}
							//							}

							//							if (gc == 10000000)
							//							{
							//								gc = 0;
							//								System.gc();
							//							}
							//							gc++;
							//							if (!htmotifs.containsKey(list))
							//							{
							//								htmotifs.put(list.clone(), new Integer(1));
							//							}
							//							else
							//							{
							//								htmotifs.put(list.clone(), new Integer(((Integer) htmotifs.get(list)).intValue() + 1));
							//							}
							//							list.clear();

							// backtrack
							//System.out.println("backtrack "+queue[pointers[index]]);

							while (addwith[queue[firstfree]] == queue[pointers[index]])
							{

								addwith[queue[firstfree]] = -1;
								firstfree--;
								if (q_index > firstfree)
								{
									q_index = firstfree;
								}
							}
							index--;

						}
						else
						{
							// if last marked node is not the last node in the queue
							if (q_index < firstfree)
							{
								q_index++;
								index++;
								pointers[index] = q_index;

								for (int j = i + 1; j < nV; j++)
								{
									if (matrix[queue[pointers[index]]][j] == 1 && incandidate[j] == 1
										&& addwith[j] == -1)
									{ // j>i?
										firstfree++;
										queue[firstfree] = j;
										addwith[j] = queue[pointers[index]];
									}
								}
							}
							else
							{
								//System.out.println("last considered node is the very last ");
								// if the last marked node is the last in the queue, unmark the node,
								// backtrack
								//System.out.println("backtrack "+queue[pointers[index]]);
								while (addwith[queue[firstfree]] == queue[pointers[index]])
								{
									//System.out.println("delete "+queue[firstfree]);
									addwith[queue[firstfree]] = -1;
									firstfree--;

								}
								q_index = pointers[index];
								index--;
							}
						}
					}//pointers!=-1

				}//ok=1

				for (int j = 0; j < k - 1; j++)
				{
					incandidate[candidates[j]] = 0;
				}

			}//for...

			//			Enumeration myenum = htmotifs.keys();
			//			ArrayList colmot = new ArrayList(4);
			//
			//			while (myenum.hasMoreElements())
			//			{
			//				colmot = (ArrayList) myenum.nextElement();
			//				//					System.out.println(colmot.toString() + " " + htmotifs.get(colmot));
			//				keysb.delete(0, keysb.length());
			//				for (int j = 0; j < k; j++)
			//				{
			//					//						System.out.println(colmot.get(j));
			//					keysb.append(inverseColorMap[((Short) colmot.get(j)).intValue()]);
			//					keysb.append(" ");
			//				}
			//				keysb.substring(0, keysb.length() - 1);
			//				key = keysb.toString();
			//				fw.write(((Integer) htmotifs.get(colmot)).toString() + "\t" + key + "\n");
			//			}
			//
			//			fw.flush();
			//			fw.close();

			d3 = new Date();
			dend = d3.getTime();
			//			System.out.println("Time for searching " + (dend - ptime));
			System.out.println("Total time " + (dend - dstart));
			System.out.println("motifs " + motcount);
			//			System.out.println("motifs leaves " + trie.totalLeafs);
			//			System.out.println("motifs internal nodes " + trie.totalInternals);
			int repeats[] = trie.repeats;
			//			System.out.println("motifs leaves " + TrieLeafMotifShort.counterLeafs);
			//			System.out.println("motifs internal nodes " + TrieInternalNodeMotifShort.counterInternalNodes);
			//			int repeats[] = TrieLeafMotifShort.repeats;
			System.out.println("Repeats ");
			for (int j = 0; j < repeats.length; j++)
			{
				if (repeats[j] != 0)
					System.out.println(j + " " + repeats[j]);
			}
			//			trie.print(System.out, inverseColorMap, k, colorQtty, nV, motcount);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}

	}
}
