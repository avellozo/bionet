/*
 * Created on 19/06/2008
 */
package baobab.sequence.loads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.dbExternal.KO;
import baobab.sequence.general.BioSql;
import baobab.sequence.general.Messages;
import baobab.sequence.general.TermsAndOntologies;
import baobab.sequence.ui.Progress;
import baobab.sequence.ui.ProgressPrintInterval;

public class loadKoDefinition
{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i = 0;
		String query = "";
		try {
			BioSql.beginTransaction();
			Set<ComparableTerm> kos = TermsAndOntologies.getAllKosTerms();
			Progress progress = new ProgressPrintInterval(System.out, 100,
				Messages.getString("LoadKoDefinition.initialMessage"));
			progress.init();
			for (ComparableTerm ko : kos) {
				if (ko.getDescription() == null || ko.getDescription().length() == 0
					|| ko.getDescription().trim().equals(Messages.getString("KO.defaultDefinition").trim())) {
					i++;
					query += "ko:" + ko.getName() + " ";
					if (i == 100) {
						updateFromKegg(query);
						query = "";
						i = 0;
					}
					progress.completeStep();
				}
			}
			if (i > 0) {
				updateFromKegg(query);
			}
			BioSql.endTransactionOK();
			progress.finish();
		}
		catch (ServiceException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			BioSql.finish();
		}

	}

	private static void updateFromKegg(String kosId) throws ServiceException, IOException {
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		String tits = serv.btit(kosId);
		String tit, koDef, koId;
		String[] strs, strs0;
		KO ko;
		BufferedReader readAns = new BufferedReader(new StringReader(tits));
		//update definition of ko
		while ((tit = readAns.readLine()) != null) {
			strs = tit.split("; ");
			strs0 = strs[0].split("[:\\s]");
			if (strs0.length > 1) {
				koId = strs0[1];
			}
			else {
				throw new RuntimeException("Without [:\\s] to find ko number: " + strs[0]);
			}
			if (strs.length > 1) {
				koDef = strs[1];
				ko = new KO(koId);
				ko.setDefinition(koDef);
			}
		}
	}

}
