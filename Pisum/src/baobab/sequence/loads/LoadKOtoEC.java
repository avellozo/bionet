/*
 * Created on 09/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.biojavax.RichObjectFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import baobab.sequence.dbExternal.EC;
import baobab.sequence.dbExternal.KO;
import baobab.sequence.ui.ProgressPrintInterval;

public class LoadKOtoEC
{
	public static void main(String[] args) {
		// load Hibernate config
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory(); //$NON-NLS-1$
		// open the session
		Session session = sessionFactory.openSession();
		RichObjectFactory.connectToBioSQL(session);
		Transaction tx = session.beginTransaction();
		try {
			//file Fasta
			String fileKo2EcName;
			if (args.length > 0) {
				fileKo2EcName = args[0];
			}
			else {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose KO to EC file");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				fileKo2EcName = fc.getSelectedFile().getPath();
			}

			BufferedReader br = new BufferedReader(new FileReader(fileKo2EcName));

			String line;
			KO ko;
			String[] sep;
			ProgressPrintInterval progress = new ProgressPrintInterval(System.out, 1000, "Initiating import");
			progress.init();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					sep = line.split("\t");
					if (sep.length != 2) {
						System.out.print("Error in line:" + line);
					}
					else {
						ko = new KO(sep[0]);
						sep[1] = sep[1].replaceAll("\\[EC:|\\]", "");
						sep = sep[1].split(" ");
						for (String ecId : sep) {
							ko.link2Ec(new EC(ecId));
						}
						progress.completeStep();
					}
				}
			}
			progress.finish();
			session.flush();
			tx.commit();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
	}

}
