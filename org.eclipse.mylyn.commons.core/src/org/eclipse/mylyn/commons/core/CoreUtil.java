/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

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
		TEST_MODE = System.getProperty("eclipse.application", "").endsWith("testapplication");
	}

}
