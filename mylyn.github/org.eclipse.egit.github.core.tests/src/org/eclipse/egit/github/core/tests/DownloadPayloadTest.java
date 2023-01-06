/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.event.DownloadPayload;
import org.junit.Test;

/**
 * Unit tests of {@link DownloadPayload}
 */
public class DownloadPayloadTest {

	/**
	 * Test default state of DownloadPayload
	 */
	@Test
	public void defaultState() {
		DownloadPayload payload = new DownloadPayload();
		assertNull(payload.getDownload());
	}

	/**
	 * Test updating DownloadPayload fields
	 */
	@Test
	public void updateFields() {
		DownloadPayload payload = new DownloadPayload();
		Download download = new Download().setName("download");
		assertEquals(download, payload.setDownload(download).getDownload());
	}
}
