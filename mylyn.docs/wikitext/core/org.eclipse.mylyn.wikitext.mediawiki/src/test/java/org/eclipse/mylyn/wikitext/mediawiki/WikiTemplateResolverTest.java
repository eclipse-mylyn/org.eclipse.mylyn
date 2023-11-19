/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.internal.TemplateProcessor;
import org.junit.Before;
import org.junit.Test;

public class WikiTemplateResolverTest {

	private static final String BUG_TEMPLATE_CONTENT = "[https://bugs.eclipse.org/{{{1}}} Bug {{{1}}}]";

	private static final String TEST_TEMPLATE_CONTENT = "XXX";

	private static final String OTHER_TEMPLATE_CONTENT = "alt";

	private WikiTemplateResolver resolver;

	private TemplateProcessor templateProcessor;

	@Before
	public void setUp() throws Exception {

		TestWikiTemplateResolver resolverUnderTest = new TestWikiTemplateResolver();
		resolverUnderTest.setWikiBaseUrl("http://wiki.eclipse.org");

		Map<String, String> serverContent = new HashMap<>();
		serverContent.put("http://wiki.eclipse.org/index.php?title=Template%3Abug&action=raw", BUG_TEMPLATE_CONTENT);
		serverContent.put("http://wiki.eclipse.org/index.php?title=Test&action=raw", TEST_TEMPLATE_CONTENT);
		serverContent.put("http://wiki.eclipse.org/index.php?title=Other%3ATest&action=raw", OTHER_TEMPLATE_CONTENT);
		resolverUnderTest.setServerContent(serverContent);

		MediaWikiLanguage markupLanguage = new MediaWikiLanguage();
		markupLanguage.getTemplateProviders().add(resolverUnderTest);

		this.templateProcessor = new TemplateProcessor(markupLanguage);
		this.resolver = resolverUnderTest;
	}

	@Test
	public void testResolveTemplateDefault() {
		Template template = resolver.resolveTemplate("bug");
		assertNotNull(template);
		assertEquals("bug", template.getName());
		assertEquals(BUG_TEMPLATE_CONTENT, template.getTemplateMarkup().trim());
	}

	@Test
	public void testResolveTemplateNoNamespace() {
		Template template = resolver.resolveTemplate(":Test");
		assertNotNull(template);
		assertEquals(TEST_TEMPLATE_CONTENT, template.getTemplateMarkup().trim());
	}

	@Test
	public void testResolveTemplateOtherNamespace() {
		Template template = resolver.resolveTemplate("Other:Test");
		assertNotNull(template);
		assertEquals("Other:Test", template.getName());
		assertEquals(OTHER_TEMPLATE_CONTENT, template.getTemplateMarkup().trim());
	}

	@Test
	public void testProcessTemplatesDefault() {
		String markup = templateProcessor.processTemplates("See {{bug|468237}}!");
		assertEquals("See [https://bugs.eclipse.org/468237 Bug 468237]!", markup);
	}

	@Test
	public void testProcessTemplatesNoNamespace() {
		String markup = templateProcessor.processTemplates("Include {{:Test}} content!");
		assertEquals("Include XXX content!", markup);
	}

	@Test
	public void testProcessTemplatesOtherNamespace() {
		String markup = templateProcessor.processTemplates("Include {{Other:Test}} content!");
		assertEquals("Include alt content!", markup);
	}

}
