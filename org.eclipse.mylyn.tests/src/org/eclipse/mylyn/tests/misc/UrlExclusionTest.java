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

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.web.MylarWebPlugin;
import org.eclipse.mylyn.internal.web.WebResource;
import org.eclipse.mylyn.internal.web.WebResourceStructureBridge;
import org.eclipse.mylyn.internal.web.WebSite;

/**
 * @author Mik Kersten
 */
public class UrlExclusionTest extends TestCase {

	public void testUrlExclusion() {
		WebResourceStructureBridge bridge = new WebResourceStructureBridge();
		String url = "http://eclipse.org";
		WebResource resource = new WebSite(url);
		assertEquals(url, bridge.getHandleIdentifier(resource));
		
		MylarWebPlugin.getDefault().addExcludedUrl(url);
		assertNull(bridge.getHandleIdentifier(resource));
	}
	
	public void testUrlExclusionPattern() {
		WebResourceStructureBridge bridge = new WebResourceStructureBridge();
		String url = "http://eclipse.org";
		WebResource resource = new WebSite(url + "/foo");
		MylarWebPlugin.getDefault().addExcludedUrl(url);
		assertNull(bridge.getHandleIdentifier(resource));
	}
	
}
