/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Utility that cleans up artifacts created by the Trac test suite. This class should be run periodically to speed up
 * execution of (attachment) tests.
 * 
 * @author Steffen Pingel
 */
public class TracTestCleanupUtil extends TestCase {

	@RunWith(Parameterized.class)
	public static class TracTestCleanupUtil4 extends TracTestCleanupUtil {

		public TracTestCleanupUtil4(TracFixture fixture, String name) {
			super(name);
			setFixture(fixture);
		}

		// requires JUnit 4.11 @Parameters(name = "{1}")
		@Parameters
		public static Iterable<Object[]> data() {
			List<TracFixture> fixtures = TestConfiguration.getDefault().discover(TracFixture.class, "trac");
			List<Object[]> data = new ArrayList<Object[]>(fixtures.size());
			for (TracFixture fixture : fixtures) {
				data.add(new Object[] { fixture, fixture.getInfo() });
			}
			return data;
		}

	}

	private TracFixture fixture;

	public TracTestCleanupUtil(String name) {
		super(name);
		this.fixture = TracFixture.current();
	}

	protected void setFixture(TracFixture fixture) {
		this.fixture = fixture;
	}

	@Test
	public void testCleanUpTasks() throws Exception {
		System.err.println("Connected to " + fixture.getRepositoryUrl());
		ITracClient client = fixture.connectXmlRpc(PrivilegeLevel.ADMIN);
		deleteOldTickets(client);
	}

	public void deleteOldTickets(ITracClient client) throws TracException {
		TracSearch query = new TracSearch();
		query.setMax(10000);
		List<Integer> result = new ArrayList<Integer>();
		client.searchForTicketIds(query, result, null);
		System.err.println("Found " + result.size() + " tickets");
		System.err.print("Deleting ticket: ");
		for (Integer i : result) {
			if (i > 10) {
				System.err.print(i + ", ");
				client.deleteTicket(i, null);
				if (i % 20 == 0) {
					System.err.println();
					System.err.print(" ");
				}
			}
		}
		System.err.println();
	}

}
