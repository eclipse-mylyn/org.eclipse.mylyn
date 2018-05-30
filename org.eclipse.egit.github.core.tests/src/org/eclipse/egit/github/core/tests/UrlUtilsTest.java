/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.util.UrlUtils;
import org.junit.Test;

/**
 * Unit tests of {@link UrlUtils}
 */
public class UrlUtilsTest {

	/**
	 * Test default constructor through anonymous sub-class
	 */
	@Test
	public void constructor() {
		assertNotNull(new UrlUtils() {
		});
	}

	/**
	 * Encode url
	 */
	@Test
	public void encode() {
		assertEquals("url", UrlUtils.encode("url"));
		String encoded = UrlUtils.encode("http://test.com/with space");
		assertNotNull(encoded);
		assertEquals(-1, encoded.indexOf(' '));
	}

	/**
	 * Verify generation of HTTPS URL
	 */
	@Test
	public void gererateHttpsUrl() {
		RepositoryId repo = new RepositoryId("person", "project");
		assertEquals("https://me@github.com/person/project.git",
				UrlUtils.createRemoteHttpsUrl(repo, "me"));
	}

	/**
	 * Verify generation of SSH URL
	 */
	@Test
	public void gererateSshUrl() {
		RepositoryId repo = new RepositoryId("person", "project");
		assertEquals("git@github.com:person/project.git",
				UrlUtils.createRemoteSshUrl(repo));
	}

	/**
	 * Verify generation of read only URL
	 */
	@Test
	public void gererateReadOnlyUrl() {
		RepositoryId repo = new RepositoryId("person", "project");
		assertEquals("git://github.com/person/project.git",
				UrlUtils.createRemoteReadOnlyUrl(repo));
	}
}
