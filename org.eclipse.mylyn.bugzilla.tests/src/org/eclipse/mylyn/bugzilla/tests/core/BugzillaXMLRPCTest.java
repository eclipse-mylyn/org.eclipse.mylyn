/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;

/**
 * Tests should be run against Bugzilla 3.6 or greater
 * 
 * @author Frank Becker
 */

public class BugzillaXMLRPCTest extends TestCase {
	private BugzillaXmlRpcClient bugzillaClient;

//	private static String TEST_REPO = "http://.../Bugzilla36noRPC";
//	private static String TEST_REPO = "http://mylyn.eclipse.org/bugs36";

	@Override
	public void setUp() throws Exception {
//		webLocation.setCredentials(AuthenticationType.REPOSITORY, "user", "password");
//		webLocation.setCredentials(AuthenticationType.HTTP, "user", "password");
//		WebLocation webLocation = new WebLocation(BugzillaTestConstants.TEST_BUGZILLA_HEAD_URL + "/xmlrpc.cgi");

		WebLocation webLocation = new WebLocation(BugzillaFixture.current().getRepositoryUrl() + "/xmlrpc.cgi");
		webLocation.setCredentials(AuthenticationType.REPOSITORY, "tests@mylyn.eclipse.org", "mylyntest");
		bugzillaClient = new BugzillaXmlRpcClient(webLocation);
		bugzillaClient.setContentTypeCheckingEnabled(true);
	}

	@SuppressWarnings("unused")
	public void testxmlrpc() throws Exception {
		int uID = bugzillaClient.login();
		String x0 = bugzillaClient.getVersion();
		HashMap<?, ?> x1 = bugzillaClient.getTime();
		Date x2 = bugzillaClient.getDBTime();
		Date x3 = bugzillaClient.getWebTime();
		Object[] xx0 = bugzillaClient.getUserInfoFromIDs(new Integer[] { 1, 2 });
		Object[] xx1 = bugzillaClient.getUserInfoFromNames(new String[] { "tests@mylyn.eclipse.org" });
		Object[] xx2 = bugzillaClient.getUserInfoWithMatch(new String[] { "est" });
		Object[] xx3 = bugzillaClient.getAllFields();
		Object[] xx4 = bugzillaClient.getFieldsWithNames(new String[] { "qa_contact" });
		Object[] xx5 = bugzillaClient.getFieldsWithIDs(new Integer[] { 12, 18 });
		Object[] xx6 = bugzillaClient.getSelectableProducts();
		Object[] xx7 = bugzillaClient.getEnterableProducts();
		Object[] xx8 = bugzillaClient.getAccessibleProducts();
		Object[] xx9 = bugzillaClient.getProducts(new Integer[] { 1, 3 });
		int i = 9;
		i++;
	}
}
