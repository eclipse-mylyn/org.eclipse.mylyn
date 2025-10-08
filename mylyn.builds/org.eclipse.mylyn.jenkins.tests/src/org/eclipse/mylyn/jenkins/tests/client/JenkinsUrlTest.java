/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.client;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsUrl;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class JenkinsUrlTest extends TestCase {

	public void testQuotes() throws Exception {
		assertEquals(getExpectedUrl("%27example%27"), createHudsonUrl("example"));
		assertEquals(getExpectedUrl("%22exampleWithSingle%27Quote%22"), createHudsonUrl("exampleWithSingle'Quote"));
		assertEquals(getExpectedUrl("%27exampleWithDouble%22Quote%27"), createHudsonUrl("exampleWithDouble\"Quote"));
		try {
			createHudsonUrl("exampleWithSingle'AndDouble\"Quote");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {// expected
		}

	}

	private String createHudsonUrl(String buildName) throws UnsupportedEncodingException {
		return JenkinsUrl.create("http://hudson.com") //$NON-NLS-1$
				.depth(1)
				.include("/hudson/job")
				.match("name", Collections.singletonList(buildName)) //$NON-NLS-1$
				.exclude("/hudson/job/build") //$NON-NLS-1$
				.toUrl();
	}

	private static String getExpectedUrl(String quotedName) {
		return "http://hudson.com/api/xml?wrapper=hudson&depth=1&xpath=/hudson/job%5Bname%3D" + quotedName
				+ "%5D&exclude=/hudson/job/build";
	}
}
