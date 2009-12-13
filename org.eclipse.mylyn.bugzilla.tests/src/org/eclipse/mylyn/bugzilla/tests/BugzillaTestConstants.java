/*******************************************************************************
 * Copyright (c) 2004, 2009 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

/**
 * @author Frank Becker
 */
public class BugzillaTestConstants {

	public static final String SERVER = System.getProperty("mylyn.bugzilla.server", "mylyn.eclipse.org");

	public static final String TEST_BUGZILLA_30_URL = getServerUrl("bugs30");

	public static final String TEST_BUGZILLA_218_URL = getServerUrl("bugs218");

	public static final String TEST_BUGZILLA_220_URL = getServerUrl("bugs220");

	public static final String TEST_BUGZILLA_2201_URL = getServerUrl("bugs220");

	public static final String TEST_BUGZILLA_222_URL = getServerUrl("bugs222");

	public static final String TEST_BUGZILLA_303_URL = getServerUrl("bugs30");

	public static final String TEST_BUGZILLA_32_URL = getServerUrl("bugs32");

	public static final String TEST_BUGZILLA_322_URL = getServerUrl("bugs322");

	public static final String TEST_BUGZILLA_323_URL = getServerUrl("bugs323");

	public static final String TEST_BUGZILLA_34_URL = getServerUrl("bugs34");

	public static final String TEST_BUGZILLA_HEAD_URL = getServerUrl("bugshead");

	public static final String TEST_BUGZILLA_LATEST_URL = TEST_BUGZILLA_34_URL;

	private static final String getServerUrl(String version) {
		return "http://" + SERVER + "/" + version;
	}

}
