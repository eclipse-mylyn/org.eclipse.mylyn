/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.util.anttask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MarkupToXslfoTaskTest extends AbstractTestAntTask {

	private MarkupToXslfoTask task;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		task = new MarkupToXslfoTask();
		task.setMarkupLanguage(languageName);
	}

	public void testSimpleOutput() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.execute();

		listFiles();

		File targetFile = new File(markup.getParentFile(), "markup.fo");
		assertTrue(targetFile.exists() && targetFile.isFile());

		String content = getContent(targetFile);
//		System.out.println(content);

		assertTrue(content.contains("<root xmlns=\"http://www.w3.org/1999/XSL/Format\""));
		assertTrue(content.contains("</root>"));
		assertTrue(content.contains(">markup</block>"));
		assertTrue(content.contains(" id=\"FirstHeading\">First Heading</block>"));
		assertTrue(content.contains(" id=\"SecondHeading\">Second Heading</block>"));
	}

	protected File createSimpleTextileMarkup() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		PrintWriter writer = new PrintWriter(new FileWriter(markupFile));
		try {
			writer.println("h1. First Heading");
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h1. Second Heading");
			writer.println();
			writer.println("some more content");
		} finally {
			writer.close();
		}
		return markupFile;
	}

}
