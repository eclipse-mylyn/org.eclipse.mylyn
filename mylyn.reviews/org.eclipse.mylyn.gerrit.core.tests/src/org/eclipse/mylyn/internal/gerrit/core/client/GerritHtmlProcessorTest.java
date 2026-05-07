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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.mylyn.internal.commons.core.FileUtil;
import org.junit.jupiter.api.Test;

public class GerritHtmlProcessorTest {
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

	private static String read(String path) throws IOException, URISyntaxException {
		URL url = new URI("platform:/plugin/org.eclipse.mylyn.gerrit.core.tests/" + path).toURL(); //$NON-NLS-1$
		try (InputStream input = url.openConnection().getInputStream()) {
			return FileUtil.readFile(input);
		}
	}
}
