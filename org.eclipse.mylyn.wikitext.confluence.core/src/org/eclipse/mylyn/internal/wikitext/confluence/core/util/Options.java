/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.confluence.core.util;

/**
 * A utility for parsing Confluence-style options
 * 
 * @author David Green
 */
public class Options {
	/**
	 * a handler for accepting options
	 */
	public interface Handler {

		/**
		 * Set an option with key and value pair
		 * 
		 * @param key
		 *            the key of the option
		 * @param value
		 *            the value of the option
		 * 
		 * @see #setOption(String)
		 */
		public void setOption(String key, String value);

		/**
		 * Set an option that is specified without a value. The default implementation does nothing.
		 * 
		 * @param option
		 *            the option to set
		 * 
		 * @see #setOption(String, String)
		 */
		public void setOption(String option);
	}

	public static void parseOptions(String options, Handler handler) {
		if (options == null) {
			return;
		}
		String[] opts = options.split("\\s*\\|\\s*"); //$NON-NLS-1$
		for (String optionPair : opts) {
			String[] keyValue = optionPair.split("\\s*=\\s*"); //$NON-NLS-1$
			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				handler.setOption(key, value);
			} else if (keyValue.length == 1) {
				handler.setOption(optionPair);
			}
		}
	}
}
