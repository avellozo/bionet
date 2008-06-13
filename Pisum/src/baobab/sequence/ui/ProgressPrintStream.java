/*
 * Created on 06/06/2008
 */
package baobab.sequence.ui;

import java.io.PrintStream;

public class ProgressPrintStream implements Progress
{
	PrintStream	out;
	int			steps			= 0;
	String		initialMessage	= null;

	public ProgressPrintStream(PrintStream out) {
		this.out = out;
	}

	public ProgressPrintStream(PrintStream out, int inicialStep) {
		this.out = out;
		this.steps = inicialStep;
	}

	public ProgressPrintStream(PrintStream out, String initialMessage) {
		this(out);
		this.initialMessage = initialMessage;
	}

	public ProgressPrintStream(PrintStream out, int inicialStep, String initialMessage) {
		this(out, inicialStep);
		this.initialMessage = initialMessage;
	}

	public void completeStep() {
		steps++;
		printStep();
	}

	protected void printStep() {
		out.println(steps);
	}

	public void setTotalSteps(int totalSteps) {
		out.println("Total steps:" + totalSteps);
	}

	public void setInitialStep(int initialStep) {
		steps = initialStep;
		printStep();
	}

	public void init() {
		out.println(initialMessage);
	}

	public void finish() {
		finish("Finished.");
	}

	public void finish(String msg) {
		out.println(msg);
	}

}
