/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.util.Map;

/**
 * @since 3.0
 * @author Steffen Pingel
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CoreUtil {

	/**
	 * @since 3.0
	 */
	public static final boolean TEST_MODE;

	static {
		String application = System.getProperty("eclipse.application", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (application.length() > 0) {
			TEST_MODE = application.endsWith("testapplication"); //$NON-NLS-1$
		} else {
			// eclipse 3.3 does not the eclipse.application property
			String commands = System.getProperty("eclipse.commands", ""); //$NON-NLS-1$ //$NON-NLS-2$
			TEST_MODE = commands.contains("testapplication\n"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns a string representation of <code>object</code>. If object is a map or array the returned string will
	 * contains a comma separated list of contained elements.
	 * 
	 * @since 3.4
	 */
	public static String toString(Object object) {
		StringBuilder sb = new StringBuilder();
		toString(sb, object);
		return sb.toString();
	}

	private static void toString(StringBuilder sb, Object object) {
		if (object instanceof Object[]) {
			sb.append("["); //$NON-NLS-1$
			Object[] entries = (Object[]) object;
			boolean prependSeparator = false;
			for (Object entry : entries) {
				if (prependSeparator) {
					sb.append(", "); //$NON-NLS-1$
				}
				toString(sb, entry);
				prependSeparator = true;
			}
			sb.append("]"); //$NON-NLS-1$
		} else if (object instanceof Map<?, ?>) {
			sb.append("{"); //$NON-NLS-1$
			boolean prependSeparator = false;
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				if (prependSeparator) {
					sb.append(", "); //$NON-NLS-1$
				}
				toString(sb, entry.getKey());
				sb.append("="); //$NON-NLS-1$
				toString(sb, entry.getValue());
				prependSeparator = true;
			}
			sb.append("}"); //$NON-NLS-1$
		} else {
			sb.append(object);
		}
	}

}
