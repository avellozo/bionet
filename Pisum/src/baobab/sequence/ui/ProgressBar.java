/*
 * Created on 06/06/2008
 */
package baobab.sequence.ui;

import javax.swing.JProgressBar;

public class ProgressBar implements Progress
{
	JProgressBar	progressBar;
	int				steps	= 0;

	public void completeStep()
	{
		progressBar.setValue(steps++);
	}

	public void setTotalSteps(int totalSteps)
	{
		progressBar.setMaximum(totalSteps);
	}

	public void setInitialStep(int initialStep)
	{
		progressBar.setMinimum(initialStep);
	}

	public void init()
	{
		progressBar.setVisible(true);
	}

	public void finish()
	{
		progressBar.setValue(progressBar.getMaximum());
	}

}
