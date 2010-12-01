/*
 * Created on 02/04/2008
 */
package general;

public class EC
{
	String	id;

	public EC(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return getId();
	}

	public String getClasses(int thre) {
		String[] ecs;
		String ec = null;
		if (id != null && (ecs = id.split("\\.")).length >= thre) {
			ec = ecs[0];
			for (int i = 1; i < thre; i++) {
				ec = ec + "." + ecs[i];
			}
		}
		return ec;
	}

}
