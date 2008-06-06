/*
 * Created on 05/06/2008
 */
package baobab.sequence.general;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
	private static final String			BUNDLE_NAME		= "baobab.sequence.general.messages";		//$NON-NLS-1$

	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
