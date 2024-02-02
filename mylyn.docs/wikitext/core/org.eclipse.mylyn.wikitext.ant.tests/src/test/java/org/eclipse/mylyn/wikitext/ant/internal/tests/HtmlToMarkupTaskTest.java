/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ant.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.mylyn.wikitext.ant.internal.HtmlToMarkupTask;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
public class HtmlToMarkupTaskTest extends AbstractTestAntTask {

	private HtmlToMarkupTask task;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		task = createTask();
		task.setMarkupLanguage(languageName);
	}

	private HtmlToMarkupTask createTask() {
		return new HtmlToMarkupTask();
	}

	@Test
	public void testSimpleOutput() throws IOException {
		File markup = createSimpleHtmlMarkup();
		task.setFile(markup);
		task.execute();

		listFiles();

		File markupFile = new File(markup.getParentFile(), "markup.textile");
		assertTrue("Expecting file: " + markupFile, markupFile.exists() && markupFile.isFile());

		String content = getContent(markupFile);

		assertEquals("h1. First Heading\n\nsome content\n\nh1. Second Heading\n\nsome more content\n\n", content);
	}

	private File createSimpleHtmlMarkup() throws IOException {
		File htmlFile = new File(tempFolder, "markup.html");
		PrintWriter writer = new PrintWriter(new FileWriter(htmlFile));
		try (writer) {
			writer.println(
					"<html><body>\n<h1>First Heading</h1>\n\n<p>some content</p>\n<h1>Second Heading</h1>\n<p>some more content</p></body></html>");
		}
		return htmlFile;
	}

	@Test
	public void testTaskdef() {
		assertEquals(HtmlToMarkupTask.class.getName(), loadTaskdefBundle().getString("html-to-wikitext"));
	}
}
