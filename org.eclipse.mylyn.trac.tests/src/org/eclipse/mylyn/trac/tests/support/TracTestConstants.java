/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

/**
 * @author Steffen Pingel
 */
public class TracTestConstants {

	public static final String TEST_TRAC_010_URL = TestConfiguration.getRepositoryUrl("trac010");

	public static final String TEST_TRAC_010_SSL_URL = TestConfiguration.getRepositoryUrl("trac010", true);

	public static final String TEST_TRAC_010_DIGEST_AUTH_URL = TestConfiguration.getRepositoryUrl("trac010digest");

	public static final String TEST_TRAC_010_FORM_AUTH_URL = TestConfiguration.getRepositoryUrl("trac010formauth");

	public static final String TEST_TRAC_011_URL = TestConfiguration.getRepositoryUrl("trac011");

	public static final String TEST_TRAC_012_URL = TestConfiguration.getRepositoryUrl("trac012");

	public static final String TEST_TRAC_10_URL = TestConfiguration.getRepositoryUrl("trac10");

	public static final String TEST_TRAC_TRUNK_URL = TestConfiguration.getRepositoryUrl("tractrunk");

	public static final String TEST_TRAC_INVALID_URL = TestConfiguration.getRepositoryUrl("doesnotexist");

}
