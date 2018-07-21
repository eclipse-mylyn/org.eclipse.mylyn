/*******************************************************************************
 * Copyright (c) 2013 David E. Narvaez and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     David E. Narvaez - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class GerritHtmlProcessorTest extends TestCase {
	@Test
	public void testHTMLProcessorGerrit2_6() throws Exception {
		GerritHtmlProcessor processor = new GerritHtmlProcessor();
		ByteArrayInputStream input = new ByteArrayInputStream(read("testdata/wikimedia-gerrit-2.7.html").getBytes()); //$NON-NLS-1$

		processor.parse(input, "utf-8"); //$NON-NLS-1$

		assertNotNull(processor.getConfig());
	}

	@Test
	public void testHTMLProcessorGerrit2_7() throws Exception {
		GerritHtmlProcessor processor = new GerritHtmlProcessor();
		ByteArrayInputStream input = new ByteArrayInputStream(read("testdata/eclipse-gerrit-2.6.html").getBytes()); //$NON-NLS-1$

		processor.parse(input, "utf-8"); //$NON-NLS-1$

		assertNotNull(processor.getConfig());
	}

	private static String read(String path) throws IOException {
		URL url = new URL("platform:/plugin/org.eclipse.mylyn.gerrit.core.tests/" + path); //$NON-NLS-1$
		InputStream input = url.openConnection().getInputStream();
		return IOUtils.toString(input, "utf-8"); //$NON-NLS-1$
	}
}
