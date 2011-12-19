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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.bugzilla.core.AbstractBugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.CustomTransitionManager;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;

/**
 * Tests should be run against Bugzilla 3.6 or greater
 * 
 * @author Frank Becker
 */
public class BugzillaXmlRpcClientTest extends TestCase {

	private static final String BUGZILLA_LE_4_0 = "<4.0";

	private static final String BUGZILLA_GE_4_0 = ">=4.0";

	private BugzillaXmlRpcClient bugzillaClient;

	@SuppressWarnings("serial")
	private final Map<String, Map<String, ArrayList<String>>> fixtureTransitionsMap = new HashMap<String, Map<String, ArrayList<String>>>() {
		{
			put(BUGZILLA_LE_4_0, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
						}
					});
				}
			});
			put(BUGZILLA_GE_4_0, new HashMap<String, ArrayList<String>>() {
				{
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("CONFIRMED");
							add("duplicate");
							add("IN_PROGRESS");
						}
					});
					put("IN_PROGRESS", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("CONFIRMED");
						}
					});
					put("CONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("IN_PROGRESS");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("CONFIRMED");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("CONFIRMED");
						}
					});
				}
			});
			put(BugzillaFixture.CUSTOM_WF, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("close");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("reopen");
							add("close");
							add("verify");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
						}
					});
				}
			});
			put(BugzillaFixture.CUSTOM_WF_AND_STATUS, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("close");
							add("reopen");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("close");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
							add("close");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("reopen");
							add("close");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
							add("MODIFIED");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("verify");
						}
					});
					put("ON_DEV", new ArrayList<String>() {
						{
							add("close");
							add("POST");
						}
					});
					put("POST", new ArrayList<String>() {
						{
							add("close");
						}
					});
					put("MODIFIED", new ArrayList<String>() {
						{
							add("close");
							add("ON_DEV");
						}
					});
				}
			});
		}
	};

	@Override
	public void setUp() throws Exception {
		WebLocation webLocation = new WebLocation(BugzillaFixture.current().getRepositoryUrl() + "/xmlrpc.cgi");
		webLocation.setCredentials(AuthenticationType.REPOSITORY, "tests@mylyn.eclipse.org", "mylyntest");
		bugzillaClient = new BugzillaXmlRpcClient(webLocation);
		bugzillaClient.setContentTypeCheckingEnabled(true);
	}

//	@SuppressWarnings("unused")
// only for local development work
//	public void testXmlRpc() throws Exception {
//		if (!BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
//			IProgressMonitor monitor = new NullProgressMonitor();
//			HashMap<?, ?> x1 = bugzillaClient.getTime(monitor);
//			Date x2 = bugzillaClient.getDBTime(monitor);
//			Date x3 = bugzillaClient.getWebTime(monitor);
//			if (BugzillaFixture.current() != BugzillaFixture.BUGS_3_4) {
//				Object[] xx3 = bugzillaClient.getAllFields(monitor);
//				Object[] xx4 = bugzillaClient.getFieldsWithNames(monitor, new String[] { "qa_contact" });
//				Object[] xx5 = bugzillaClient.getFieldsWithIDs(monitor, new Integer[] { 12, 18 });
//			}
//		}
//	}

	public void testGetVersion() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			String version = bugzillaClient.getVersion(monitor);
			assertEquals(BugzillaFixture.current().getVersion(), version);
		}
	}

	@SuppressWarnings("unchecked")
	public void testUserInfo() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			int uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
			Object[] userList0 = bugzillaClient.getUserInfoFromIDs(monitor, new Integer[] { 1, 2 });
			assertNotNull(userList0);
			assertEquals(2, userList0.length);
			assertEquals(((Integer) 1), ((HashMap<String, Integer>) userList0[0]).get("id"));
			assertEquals("admin@mylyn.eclipse.org", ((HashMap<String, String>) userList0[0]).get("email"));
			assertEquals("admin@mylyn.eclipse.org", ((HashMap<String, String>) userList0[0]).get("name"));
			assertEquals("Mylyn Admin", ((HashMap<String, String>) userList0[0]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList0[0]).get("can_login"));

			assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList0[1]).get("id"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList0[1]).get("email"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList0[1]).get("name"));
			assertEquals("Mylyn Test", ((HashMap<String, String>) userList0[1]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList0[1]).get("can_login"));

			Object[] userList1 = bugzillaClient.getUserInfoFromNames(monitor,
					new String[] { "tests@mylyn.eclipse.org" });
			assertNotNull(userList1);
			assertEquals(1, userList1.length);
			assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList1[0]).get("id"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList1[0]).get("email"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList1[0]).get("name"));
			assertEquals("Mylyn Test", ((HashMap<String, String>) userList1[0]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList1[0]).get("can_login"));

			Object[] userList2 = bugzillaClient.getUserInfoWithMatch(monitor, new String[] { "est" });
			assertEquals(2, userList2.length);
			assertEquals(((Integer) 3), ((HashMap<String, Integer>) userList2[0]).get("id"));
			assertEquals("guest@mylyn.eclipse.org", ((HashMap<String, String>) userList2[0]).get("email"));
			assertEquals("guest@mylyn.eclipse.org", ((HashMap<String, String>) userList2[0]).get("name"));
			assertEquals("Mylyn guest", ((HashMap<String, String>) userList2[0]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList2[0]).get("can_login"));

			assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList2[1]).get("id"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList2[1]).get("email"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList2[1]).get("name"));
			assertEquals("Mylyn Test", ((HashMap<String, String>) userList2[1]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList2[1]).get("can_login"));
		}
	}

	@SuppressWarnings("unchecked")
	public void testProductInfo() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			int uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
			Object[] selProductIDs = bugzillaClient.getSelectableProducts(monitor);
			assertNotNull(selProductIDs);
			assertEquals(3, selProductIDs.length);
			Object[] enterProductIDs = bugzillaClient.getEnterableProducts(monitor);
			assertNotNull(enterProductIDs);
			assertEquals(3, enterProductIDs.length);
			Object[] accessibleProductIDs = bugzillaClient.getAccessibleProducts(monitor);
			assertNotNull(accessibleProductIDs);
			assertEquals(3, accessibleProductIDs.length);
			Object[] productDetails = bugzillaClient.getProducts(monitor, new Integer[] { 1, 3 });
			assertNotNull(productDetails);
			assertEquals(2, productDetails.length);
			assertEquals(((Integer) 1), ((HashMap<String, Integer>) productDetails[0]).get("id"));
			assertEquals(
					"This is a test product. This ought to be blown away and replaced with real stuff in a finished installation of bugzilla.",
					((HashMap<String, String>) productDetails[0]).get("description"));
			assertEquals(((Integer) 3), ((HashMap<String, Integer>) productDetails[1]).get("id"));
			assertEquals("Product for manual testing", ((HashMap<String, String>) productDetails[1]).get("description"));

		}
	}

	@SuppressWarnings("unused")
	public void testXmlRpcInstalled() throws Exception {
		int uID = -1;
		IProgressMonitor monitor = new NullProgressMonitor();
		BugzillaFixture a = BugzillaFixture.current();
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			try {
				uID = bugzillaClient.login(monitor);
				fail("Never reach this! We should get an XmlRpcException");
			} catch (XmlRpcException e) {
				assertEquals("The server returned an unexpected content type: 'text/html; charset=UTF-8'",
						e.getMessage());
			}
		} else {
			uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
		}
	}

	public void testTransitionManagerWithXml() throws Exception {

		if (BugzillaFixture.current().getBugzillaVersion().isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			return;
		} else if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			CustomTransitionManager ctm = new CustomTransitionManager();
			ctm.parse(new NullProgressMonitor(), bugzillaClient);

			ArrayList<String> transitions = new ArrayList<String>();
			Map<String, ArrayList<String>> expectTransitions;

			/*
			 * Copy and paste this block to test valid transitions for different start statuses
			 * 
			 * We check that only valid operations are returned. There is no
			 * way to determine (using the operation 'reopen') whether "REOPEN" or "UNCONFIRMED"
			 * is valid.
			 */
			if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.CUSTOM_WF)) {
				expectTransitions = fixtureTransitionsMap.get(BugzillaFixture.CUSTOM_WF);
			} else if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.CUSTOM_WF_AND_STATUS)) {
				expectTransitions = fixtureTransitionsMap.get(BugzillaFixture.CUSTOM_WF_AND_STATUS);
			} else if (BugzillaFixture.current().getBugzillaVersion().isSmaller(BugzillaVersion.BUGZILLA_4_0)) {
				expectTransitions = fixtureTransitionsMap.get(BUGZILLA_LE_4_0);
			} else {
				expectTransitions = fixtureTransitionsMap.get(BUGZILLA_GE_4_0);
			}
			for (String start : expectTransitions.keySet()) {
				transitions.clear();
				ArrayList<String> expectedStateTransition = expectTransitions.get(start);
				for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
					String end = s.toString();
					if (expectedStateTransition.contains(end)) {
						transitions.add(end);
					} else {
						fail("The status " + start + " is not expected to transition to " + end.toString());
					}
				}
				assertEquals("Missing transitions for " + start + ", only found " + transitions, transitions.size(),
						ctm.getValidTransitions(start).size());
			}
		}
	}
}
