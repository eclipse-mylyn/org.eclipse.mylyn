/*******************************************************************************
 * Copyright (c) 2009, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.textile;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.mylyn.internal.wikitext.ui.util.Util;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;

import com.google.common.io.Resources;

import junit.framework.TestCase;

/**
 * tests for Textile that involve the tasks plug-in and dependencies on the Eclipse runtime.
 *
 * @author David Green
 */
@EclipseRuntimeRequired
public class TextileLanguageTasksTest extends TestCase {

	private MarkupParser parser;

	private TextileLanguage markupLanguage;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initParser();
	}

	private void initParser() throws IOException {
		parser = new MarkupParser();
		markupLanguage = new TextileLanguage();

		MarkupLanguageConfiguration configuration = Util.create("bugzilla");
		markupLanguage.configure(configuration);

		parser.setMarkupLanguage(markupLanguage);
	}

	public void testSubversiveBugReport() throws IOException {

		StringWriter out = new StringWriter();
		parser.setBuilder(new HtmlDocumentBuilder(out));

		String content = Resources.toString(
				TextileLanguageTasksTest.class.getResource("resources/subversive-bug-report.txt"),
				StandardCharsets.UTF_8);
		parser.parse(content);
	}
}
