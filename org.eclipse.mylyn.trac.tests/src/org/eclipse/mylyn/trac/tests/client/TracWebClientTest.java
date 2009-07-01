/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;

/**
 * @author Steffen Pingel
 */
public class TracWebClientTest extends AbstractTracClientRepositoryTest {

	public TracWebClientTest() {
		super(Version.TRAC_0_9);
	}

	public void testValidate096() throws Exception {
		validate(TracTestConstants.TEST_TRAC_096_URL);
	}

	@Override
	public void testValidate011() throws Exception {
		try {
			validate(TracTestConstants.TEST_TRAC_011_URL);
		} catch (TracException e) {
		}
	}

	public void testValidateAnyPage() throws Exception {
		connect("http://mylyn.eclipse.org/");
		try {
			client.validate(callback);
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testValidateAnonymousLogin() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		client.validate(callback);

		connect(TracTestConstants.TEST_TRAC_096_URL, "", "");
		client.validate(callback);
	}

	public void testUpdateAttributesAnonymous096() throws Exception {
		connect(TracTestConstants.TEST_TRAC_096_URL, "", "");
		updateAttributes();
	}

	public void testUpdateAttributesAnonymous010() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		updateAttributes();
	}

	private void updateAttributes() throws TracException {
		assertNull(client.getMilestones());
		client.updateAttributes(new NullProgressMonitor(), true);
		TracVersion[] versions = client.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("2.0", versions[1].getName());
	}

	// TODO move this test to AbstracTracClientTest when bug 162094 is resolved
	public void testSearchMilestoneAmpersand010() throws Exception {
		connect010();
		searchMilestoneAmpersand();
	}

	public void testSearchMilestoneAmpersand011() throws Exception {
		connect011();
		searchMilestoneAmpersand();
	}

	private void searchMilestoneAmpersand() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "mile&stone");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(7), result.get(0));
	}

	public void testStatusClosed096() throws Exception {
		connect096();
		statusClosed();
	}

}
