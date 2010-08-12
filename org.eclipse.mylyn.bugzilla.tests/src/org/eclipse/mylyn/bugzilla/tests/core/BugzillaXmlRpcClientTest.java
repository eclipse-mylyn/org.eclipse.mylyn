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
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.bugzilla.core.AbstractBugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.CustomTransitionManager;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;

/**
 * Tests should be run against Bugzilla 3.6 or greater
 * 
 * @author Frank Becker
 */
public class BugzillaXmlRpcClientTest extends TestCase {

	private BugzillaXmlRpcClient bugzillaClient;

	@Override
	public void setUp() throws Exception {
		WebLocation webLocation = new WebLocation(BugzillaFixture.current().getRepositoryUrl() + "/xmlrpc.cgi");
		webLocation.setCredentials(AuthenticationType.REPOSITORY, "tests@mylyn.eclipse.org", "mylyntest");
		bugzillaClient = new BugzillaXmlRpcClient(webLocation);
		bugzillaClient.setContentTypeCheckingEnabled(true);
	}

	@SuppressWarnings("unused")
	public void testXmlRpc() throws Exception {
		if (BugzillaFixture.current() != BugzillaFixture.BUGS_3_6_XML_RPC_DISABLED) {
			IProgressMonitor monitor = new NullProgressMonitor();
			int uID = bugzillaClient.login(monitor);
			String x0 = bugzillaClient.getVersion(monitor);
			HashMap<?, ?> x1 = bugzillaClient.getTime(monitor);
			Date x2 = bugzillaClient.getDBTime(monitor);
			Date x3 = bugzillaClient.getWebTime(monitor);
			Object[] xx0 = bugzillaClient.getUserInfoFromIDs(monitor, new Integer[] { 1, 2 });
			Object[] xx1 = bugzillaClient.getUserInfoFromNames(monitor, new String[] { "tests@mylyn.eclipse.org" });
			Object[] xx2 = bugzillaClient.getUserInfoWithMatch(monitor, new String[] { "est" });
			Object[] xx3 = bugzillaClient.getAllFields(monitor);
			Object[] xx4 = bugzillaClient.getFieldsWithNames(monitor, new String[] { "qa_contact" });
			Object[] xx5 = bugzillaClient.getFieldsWithIDs(monitor, new Integer[] { 12, 18 });
			Object[] xx6 = bugzillaClient.getSelectableProducts(monitor);
			Object[] xx7 = bugzillaClient.getEnterableProducts(monitor);
			Object[] xx8 = bugzillaClient.getAccessibleProducts(monitor);
			Object[] xx9 = bugzillaClient.getProducts(monitor, new Integer[] { 1, 3 });
		}
	}

	@SuppressWarnings("unused")
	public void testXmlRpcInstalled() throws Exception {
		int uID = -1;
		IProgressMonitor monitor = new NullProgressMonitor();
		BugzillaFixture a = BugzillaFixture.current();
		if (BugzillaFixture.current() == BugzillaFixture.BUGS_3_6_XML_RPC_DISABLED) {
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
		if (BugzillaFixture.current() == BugzillaFixture.BUGS_3_6) {
			CustomTransitionManager ctm = new CustomTransitionManager();
			ctm.parse(new NullProgressMonitor(), bugzillaClient);

			String start;
			ArrayList<String> transitions = new ArrayList<String>();

			/*
			 * Copy and paste this block to test valid transitions for different start statuses
			 * 
			 * We check that only valid operations are returned. There is no
			 * way to determine (using the operation 'reopen') whether "REOPEN" or "UNCONFIRMED"
			 * is valid.
			 */
			start = "NEW";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("accept")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " is not expected to transition to " + name.toString());
				}
			}
			assertEquals("Missing transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "UNCONFIRMED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("accept")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else if (name.equals("markNew")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "ASSIGNED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else if (name.equals("markNew")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "REOPENED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("accept")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else if (name.equals("markNew")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "RESOLVED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("verify")) {
					transitions.add(name);
				} else if (name.equals("reopen")) {
					transitions.add(name);
				} else if (name.equals("close")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "VERIFIED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("reopen")) {
					transitions.add(name);
				} else if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else if (name.equals("close")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

			start = "CLOSED";
			transitions.clear();
			for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
				String name = s.toString();
				if (name.equals("reopen")) {
					transitions.add(name);
				} else if (name.equals("resolve")) {
					transitions.add(name);
				} else if (name.equals("duplicate")) {
					transitions.add(name);
				} else {
					fail("The status " + start + " was not expected to transition to " + name.toString());
				}
			}
			assertEquals("Unrecognized transitions for " + start + ", only found " + transitions, transitions.size(),
					ctm.getValidTransitions(start).size());

		}
	}
}
