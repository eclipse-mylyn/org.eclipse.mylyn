/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.misc;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.web.ui.WebResource;
import org.eclipse.mylyn.internal.web.ui.WebResourceStructureBridge;
import org.eclipse.mylyn.internal.web.ui.WebSite;
import org.eclipse.mylyn.internal.web.ui.WebUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class UrlExclusionTest extends TestCase {

	public void testUrlExclusion() {
		WebResourceStructureBridge bridge = new WebResourceStructureBridge();
		String url = "http://eclipse.org";
		WebResource resource = new WebSite(url);
		assertEquals(url, bridge.getHandleIdentifier(resource));

		WebUiBridgePlugin.getDefault().addExcludedUrl(url);
		assertNull(bridge.getHandleIdentifier(resource));
	}

	public void testUrlExclusionPattern() {
		WebResourceStructureBridge bridge = new WebResourceStructureBridge();
		String url = "http://eclipse.org";
		WebResource resource = new WebSite(url + "/foo");
		WebUiBridgePlugin.getDefault().addExcludedUrl(url);
		assertNull(bridge.getHandleIdentifier(resource));
	}

}
