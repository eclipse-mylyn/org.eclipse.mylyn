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

package org.eclipse.mylyn.trac.tests.support;

/**
 * @author Steffen Pingel
 */
public class TracTestConstants {

	public static final String SERVER = System.getProperty("mylyn.trac.server", "mylyn.eclipse.org");

	public static final String TEST_TRAC_096_URL = "http://" + SERVER + "/trac096";

	public static final String TEST_TRAC_010_URL = "http://" + SERVER + "/trac010";

	public static final String TEST_TRAC_010_SSL_URL = "https://" + SERVER + "/trac010";

	public static final String TEST_TRAC_010_DIGEST_AUTH_URL = "http://" + SERVER + "/trac010digest";

	public static final String TEST_TRAC_010_FORM_AUTH_URL = "http://" + SERVER + "/trac010formauth";

	public static final String TEST_TRAC_011_URL = "http://" + SERVER + "/trac011";

	public static final String TEST_TRAC_INVALID_URL = "http://" + SERVER + "/doesnotexist";

}
