/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

public class MarkupToEclipseHelpTaskTest extends MarkupToHtmlTaskTest {

	@Override
	protected MarkupToHtmlTask createTask() {
		return new MarkupToEclipseHelpTask();
	}

	@Override
	public void testSimpleOutput() throws IOException {
		super.testSimpleOutput();

		File tocFile = new File(tempFolder, "markup-toc.xml");
		assertTrue(tocFile.exists());

		String tocContent = getContent(tocFile);
		System.out.println(tocContent);

		assertTrue(tocContent.contains("<toc topic=\"markup.html\" label=\"markup\">"));
		assertTrue(tocContent.contains("<topic href=\"markup.html\" label=\"First Heading\""));
		assertTrue(tocContent.contains("<topic href=\"markup.html#SecondHeading\" label=\"Second Heading\""));
	}

	@Override
	public void testMultipleFiles() throws IOException {
		super.testMultipleFiles();

		File tocFile = new File(tempFolder, "markup-toc.xml");
		assertTrue(tocFile.exists());

		String tocContent = getContent(tocFile);
		System.out.println(tocContent);

		assertTrue(tocContent.contains("<toc topic=\"markup.html\" label=\"markup\">"));
		assertTrue(tocContent.contains("<topic href=\"markup.html\" label=\"First Heading\""));
		assertTrue(tocContent.contains("<topic href=\"Second-Heading.html\" label=\"Second Heading\""));
	}

	@Override
	public void testSimpleOutputAlternateTitle() throws IOException {
		super.testSimpleOutputAlternateTitle();

		File tocFile = new File(tempFolder, "markup-toc.xml");
		assertTrue(tocFile.exists());

		String tocContent = getContent(tocFile);
		System.out.println(tocContent);

		assertTrue(tocContent.contains("<toc topic=\"markup.html\" label=\"Alternate Title\">"));
		assertTrue(tocContent.contains("<topic href=\"markup.html\" label=\"First Heading\""));
		assertTrue(tocContent.contains("<topic href=\"markup.html#SecondHeading\" label=\"Second Heading\""));
	}

	public void testMultipleFilesWithMultiLevelHeadings() throws IOException {
		File markup = createSimpleTextileMarkupWithMultiLevelHeadings();
		task.setFile(markup);
		task.setMultipleOutputFiles(true);
		task.execute();

		listFiles();

		File tocFile = new File(tempFolder, "markup-toc.xml");
		assertTrue(tocFile.exists());

		String tocContent = getContent(tocFile);
		System.out.println(tocContent);

		assertTrue(tocContent.contains("<toc topic=\"markup.html\" label=\"markup\">"));
		assertTrue(tocContent.contains("<topic href=\"markup.html\" label=\"First Heading\""));
		assertTrue(tocContent.contains("<topic href=\"Second-Heading.html\" label=\"Second Heading\""));
		assertTrue(tocContent.contains("<topic href=\"Second-Heading.html#SecondL2\" label=\"Second L2\""));
		assertTrue(tocContent.contains("<topic href=\"Second-Heading.html#SecondL22\" label=\"Second L2 2\""));
	}

	protected File createSimpleTextileMarkupWithMultiLevelHeadings() throws IOException {
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
			writer.println();
			writer.println("h2. Second L2");
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h2. Second L2 2");
			writer.println();
			writer.println("some content");
			writer.println();
		} finally {
			writer.close();
		}
		return markupFile;
	}

}
