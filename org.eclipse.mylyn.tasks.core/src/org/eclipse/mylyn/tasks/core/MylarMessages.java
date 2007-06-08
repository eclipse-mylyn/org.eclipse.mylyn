/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 */
public class MylarMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.tasks.core.MylarMessages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, MylarMessages.class);
	}

	private MylarMessages() {
		// Do not instantiate
	}

	public static String repository_login_failure;

	public static String repository_not_found;
	
	public static String repository_comment_reqd;
	
	public static String repository_collision;

	public static String operation_cancelled;

	public static String network_error;

	public static String io_error;

	public static String internal_error;


	public static String repository_error;

}
