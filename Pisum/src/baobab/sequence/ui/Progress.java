/*
 * Created on 06/06/2008
 */
package baobab.sequence.ui;

public interface Progress
{
	public void setTotalSteps(int totalSteps);

	public void completeStep();

	public void setInitialStep(int initialStep);

	public void init();

	public void finish();

}
