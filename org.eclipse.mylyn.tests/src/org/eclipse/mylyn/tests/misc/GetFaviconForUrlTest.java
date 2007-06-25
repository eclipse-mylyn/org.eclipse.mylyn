/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.misc;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.web.ui.WebUiUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Leo Dos Santos
 */
public class GetFaviconForUrlTest extends TestCase {

	public void testMalformedUrl() {
		boolean exceptionThrown = false;
		try {
			WebUiUtil.getFaviconForUrl("www.eclipse.org");
		} catch (MalformedURLException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public void testEclipseDotOrg() {
		Image img = null;
		try {
			img = WebUiUtil.getFaviconForUrl("http://www.eclipse.org").createImage(false);
		} catch (MalformedURLException e) {
			fail();
		}
		assertNotNull(img);
		if (img != null) {
			ImageData data = img.getImageData();
			assertEquals(data.height, 16);
			assertEquals(data.width, 16);
			img.dispose();
		}
	}

	public void testNoFavicon() throws MalformedURLException {
		assertNull(WebUiUtil.getFaviconForUrl("http://help.eclipse.org/help32/index.jsp"));
	}
}
