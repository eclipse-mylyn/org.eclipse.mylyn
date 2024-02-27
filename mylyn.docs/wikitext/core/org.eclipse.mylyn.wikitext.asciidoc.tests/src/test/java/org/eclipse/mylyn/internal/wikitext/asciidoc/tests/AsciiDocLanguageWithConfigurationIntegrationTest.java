/*******************************************************************************
 * Copyright (c) 2017, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocMarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class AsciiDocLanguageWithConfigurationIntegrationTest extends AsciiDocLanguageTestBase {

	@Test
	public void parseWithEmptyConfiguration() {
		AsciiDocMarkupLanguageConfiguration configuration = new AsciiDocMarkupLanguageConfiguration();

		MarkupParser parser = createParserWithConfiguration(configuration);

		String html = parseAsciiDocToHtml(AsciiDocLanguageAttributeTest.MARKUP_FOR_DEFAULT, parser);
		AsciiDocLanguageAttributeTest.ensureDefaultValues(html);
	}

	@Test
	public void parseWithCommonMarkupLanguageConfiguration() {
		MarkupLanguageConfiguration configuration = new MarkupLanguageConfiguration();

		MarkupParser parser = createParserWithConfiguration(configuration);

		String html = parseAsciiDocToHtml(AsciiDocLanguageAttributeTest.MARKUP_FOR_DEFAULT, parser);
		AsciiDocLanguageAttributeTest.ensureDefaultValues(html);
	}

	@Test
	public void parseWithImagesdirConfiguration() {
		AsciiDocMarkupLanguageConfiguration configuration = new AsciiDocMarkupLanguageConfiguration();
		Map<String, String> initialAttributes = new HashMap<>();
		initialAttributes.put("imagesdir", "IMGS");
		configuration.setInitialAttributes(initialAttributes);

		MarkupParser parser = createParserWithConfiguration(configuration);

		String markup = "See this the {imagesdir} folder";
		String html = parseAsciiDocToHtml(markup, parser);
		assertEquals("<p>See this the IMGS folder</p>\n", html);
	}
}
