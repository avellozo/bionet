/*
 * Created on 06/06/2008
 */
package baobab.sequence.ui;

import java.io.PrintStream;

public class ProgressPrintInterval extends ProgressPrintStream
{
	int	interval;

	public ProgressPrintInterval(PrintStream out, int interval)
	{
		super(out);
		this.interval = interval;
	}

	public ProgressPrintInterval(PrintStream out, int inicialStep, int interval)
	{
		super(out, inicialStep);
		this.interval = interval;
	}

	public ProgressPrintInterval(PrintStream out, int interval, String initialMessage)
	{
		super(out, initialMessage);
		this.interval = interval;
	}

	public ProgressPrintInterval(PrintStream out, int inicialStep, int interval, String initialMessage)
	{
		super(out, inicialStep, initialMessage);
		this.interval = interval;
	}

	@Override
	public void completeStep()
	{
		if (steps % interval == 0)
		{
			steps++;
			printStep();
		}
	}

	@Override
	public void finish()
	{
		printStep();
		super.finish();
	}

}
