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

package org.eclipse.mylyn.bugzilla.tests;

/**
 * @author Frank Becker
 */
public class BugzillaTestConstants {

	/* For now test on 3.0 as earliest bugzilla. Also bugs30 should always be the latest 3.0.x release */
	public static final String TEST_BUGZILLA_30_URL = getServerUrl("bugs30"); //$NON-NLS-1$

	public static final String TEST_BUGZILLA_218_URL = getServerUrl("bugs218");

	public static final String TEST_BUGZILLA_220_URL = getServerUrl("bugs220");

	public static final String TEST_BUGZILLA_2201_URL = getServerUrl("bugs220");

	public static final String TEST_BUGZILLA_222_URL = getServerUrl("bugs222");

	public static final String TEST_BUGZILLA_303_URL = getServerUrl("bugs30");

	public static final String TEST_BUGZILLA_32_URL = getServerUrl("bugs32");

	public static final String TEST_BUGZILLA_322_URL = getServerUrl("bugs322");

	public static final String TEST_BUGZILLA_323_URL = getServerUrl("bugs323");

	public static final String TEST_BUGZILLA_34_URL = getServerUrl("bugs34"); //$NON-NLS-1$

	public static final String TEST_BUGZILLA_LATEST_URL = TEST_BUGZILLA_34_URL;

	private static final String getServerUrl(String version) {
		String url = System.getProperty("bugzilla.server.url" + version, null); //$NON-NLS-1$
		return (url != null) ? url : System.getProperty("bugzilla.server.url", "http://mylyn.eclipse.org/" + version); //$NON-NLS-1$
	}
}
