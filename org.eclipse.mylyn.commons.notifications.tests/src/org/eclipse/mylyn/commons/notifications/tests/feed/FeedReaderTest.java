/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.feed;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedEntry;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;

/**
 * @author Steffen Pingel
 */
public class FeedReaderTest extends TestCase {

	private FeedReader reader;

	private NotificationEnvironment environment;

	@Override
	protected void setUp() throws Exception {
		environment = new NotificationEnvironment() {
			@Override
			public Set<String> getInstalledFeatures(IProgressMonitor monitor) {
				return Collections.singleton("org.eclipse.mylyn");
			}
		};
		reader = new FeedReader("eventId", environment);
	}

	public void testParse() throws Exception {
		assertEquals(Status.OK_STATUS, reader.parse(
				CommonTestUtil.getResource(FeedReaderTest.class, "testdata/FeedReaderTest/update1.xml"), null));
		assertEquals(2, reader.getEntries().size());
		Collections.sort(reader.getEntries());

		FeedEntry entry = reader.getEntries().get(0);
		assertEquals("New Connectors", entry.getTitle());
		assertEquals("[0.0.0,3.7.0)", entry.getFilter("frameworkVersion"));
		assertEquals("New connectors are now available. <a href=\"#discovery\">Show connectors</a>.",
				entry.getDescription());

		entry = reader.getEntries().get(1);
		assertEquals("Mylyn 3.6 is now available", entry.getTitle());
		assertEquals("1.5.0", entry.getFilter("runtimeVersion"));
		assertEquals(
				"Mylyn 3.7 is now available. <a href=\"http://eclipse.org/mylyn/new/\">See New and Noteworthy</a> for details.",
				entry.getDescription());
	}

}
