/**
 * 
 */
package org.eclipse.mylyn.htmltext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author tom
 *
 */
public class Configuration {
	
	private String enterMode = "P";

	public String getEnterMode() {
		return enterMode;
	}

	/**
	 * Sets the enter mode: Possible values: 'P','BR','DIV'
	 * Default is 'P'
	 * @param enterMode
	 */
	public void setEnterMode(String enterMode) {
		this.enterMode = enterMode;
	}
	
	public String toQuery() {
		try {
			return "enterMode=" + URLEncoder.encode(enterMode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			 throw new IllegalStateException();
		}
	}

}
