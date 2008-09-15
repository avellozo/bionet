/*
 * Created on 27/08/2008
 */
package baobab.sequence.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.biojavax.ontology.ComparableOntology;
import org.biojavax.ontology.ComparableTerm;

import baobab.sequence.exception.DBObjectNotFound;
import baobab.sequence.general.Messages;
import baobab.sequence.general.Organism;
import baobab.sequence.general.TermsAndOntologies;

public class PFFileGenerator extends JPanel
{
	static Organism		organism;
	JTextArea			output;
	JList				listCompilations;
	JTable				table;
	String				newline	= "\n";
	ListSelectionModel	listSelectionModel;

	public PFFileGenerator() {
		super(new BorderLayout());

		ComparableOntology ont = TermsAndOntologies.getCompilationOnt(organism);
		Set<ComparableTerm> comps = ont.getTerms();
		String[] listData = new String[comps.size()];
		int i = 0;
		for (ComparableTerm comp : comps) {
			listData[i++] = comp.getName();
		}

		listCompilations = new JList(listData);

		listSelectionModel = listCompilations.getSelectionModel();
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel.addListSelectionListener(new SelectionCompilationHandler());
		JScrollPane listPane = new JScrollPane(listCompilations);

		//		JPanel controlPane = new JPanel();
		//		controlPane.add(new JLabel("Selection mode:"));
		//		controlPane.add(comboBox);
		//
		//		//Build output area.
		//		output = new JTextArea(1, 10);
		//		output.setEditable(false);
		//		JScrollPane outputPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		//			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//
		//Do the layout.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);

		JPanel topHalf = new JPanel();
		topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
		JPanel listContainer = new JPanel(new GridLayout(1, 1));
		listContainer.setBorder(BorderFactory.createTitledBorder("List"));
		listContainer.add(listPane);

		topHalf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		topHalf.add(listContainer);
		//topHalf.add(tableContainer);

		topHalf.setMinimumSize(new Dimension(100, 50));
		topHalf.setPreferredSize(new Dimension(100, 110));
		splitPane.add(topHalf);

		//		JPanel bottomHalf = new JPanel(new BorderLayout());
		//		bottomHalf.add(controlPane, BorderLayout.PAGE_START);
		//		bottomHalf.add(outputPane, BorderLayout.CENTER);
		//		//XXX: next line needed if bottomHalf is a scroll pane:
		//		//bottomHalf.setMinimumSize(new Dimension(400, 50));
		//		bottomHalf.setPreferredSize(new Dimension(450, 135));
		//		splitPane.add(bottomHalf);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("PF File Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		PFFileGenerator pfFileGenerator = new PFFileGenerator();
		pfFileGenerator.setOpaque(true);
		frame.setContentPane(pfFileGenerator);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// the ncbiTaxon of organism
		String respDialog;
		if (args.length < 2) {
			respDialog = JOptionPane.showInputDialog("Please input the NCBI_Taxon_ID:",
				Messages.getString("ncbiTaxonNumberDefault"));
			if (respDialog == null) {
				return;
			}
			int ncbiTaxonNumber = Integer.parseInt(respDialog);
			try {
				organism = new Organism(ncbiTaxonNumber);
			}
			catch (DBObjectNotFound ex) {
				organism = null;
			}
			while (organism == null) {
				respDialog = JOptionPane.showInputDialog("NCBITaxonID not found. Please input the NCBI_Taxon_ID:",
					ncbiTaxonNumber);
				if (respDialog == null) {
					return;
				}
				ncbiTaxonNumber = Integer.parseInt(respDialog);
				try {
					organism = new Organism(ncbiTaxonNumber);
				}
				catch (DBObjectNotFound ex) {
				}
			}
		}
		else {
			try {
				organism = new Organism(Integer.parseInt(args[1]));
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	class SelectionCompilationHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) {
			//			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			//
			//			int firstIndex = e.getFirstIndex();
			//			int lastIndex = e.getLastIndex();
			//			boolean isAdjusting = e.getValueIsAdjusting();
			//			output.append("Event for indexes " + firstIndex + " - " + lastIndex + "; isAdjusting is " + isAdjusting
			//				+ "; selected indexes:");
			//
			//			if (lsm.isSelectionEmpty()) {
			//				output.append(" <none>");
			//			}
			//			else {
			//				// Find out which indexes are selected.
			//				int minIndex = lsm.getMinSelectionIndex();
			//				int maxIndex = lsm.getMaxSelectionIndex();
			//				for (int i = minIndex; i <= maxIndex; i++) {
			//					if (lsm.isSelectedIndex(i)) {
			//						output.append(" " + i);
			//					}
			//				}
			//			}
			//			output.append(newline);
			//			output.setCaretPosition(output.getDocument().getLength());
		}
	}
}