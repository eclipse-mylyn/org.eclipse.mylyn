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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;

public class BugzillaXMLRPCTest {
	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	private static String TEST_REPO = "http://macmainz.dyndns.org/Internet/BugzillaDevelop";


	public static BugzillaFixture BUGS_3_7 = new BugzillaFixture(TEST_REPO, "3.7", "");

	@Before
	public void setUp() throws Exception {
		repository = BugzillaFixture.current(BUGS_3_7).repository();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"),
				false);
		repository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"), false);
		connector = BugzillaFixture.current(BUGS_3_7).connector();
	}

	@Test
	@SuppressWarnings("unused")
	public void testxmlrpc() throws Exception {
		BugzillaXmlRpcClient ws = new BugzillaXmlRpcClient(repository);
		int user = ws.getUserID();
		assertEquals(-1, user);

		// Services from Bugzilla::WebService::Bugzilla
		String version = ws.getVersion();
		assertEquals("3.7", version);
		user = ws.getUserID();
		assertTrue(user != -1);
		Date dbtime = ws.getDBTime();
		Date webtime = ws.getWebTime();
		HashMap<String, Date> time = ws.getTime();
		// native Response
		Object[] xx0 = ws.getUserInfoFromIDs(new Integer[] { 1, 2 });
		Object[] xx1 = ws.getUserInfoFromNames(new String[] { "test@Frank-Becker.de" });
		Object[] xx2 = ws.getUserInfoWithMatch(new String[] { "tes" });
		Object[] xx3 = ws.getAllFields();
		Object[] xx4 = ws.getFieldsWithNames(new String[] { "qa_contact" });
		Object[] xx5 = ws.getFieldsWithIDs(new Integer[] { 12, 18 });
		Object[] xx6 = ws.getSelectableProducts();
		Object[] xx7 = ws.getEnterableProducts();
		Object[] xx8 = ws.getAccessibleProducts();
		Object[] xx9 = ws.getProducts(new Integer[] { 1, 3 });

		user++;
		user--;
	}
}
