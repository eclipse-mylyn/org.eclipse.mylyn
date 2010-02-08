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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;

public class TemplateProcessorTest extends TestCase {

	private MediaWikiLanguage markupLanguage;

	@Override
	protected void setUp() throws Exception {
		markupLanguage = new MediaWikiLanguage();
	}

	public void testBasicTemplateNoParameters() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	public void testBasicTemplateNamedParameter() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|message=foo bar}} two");
		assertEquals("one _expandedfoo bar_ two", markup);
	}

	public void testBasicTemplatePositionalParameter() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{1}}}and{{{2}}}and{{{1}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|one|two}} two");
		assertEquals("one _expandedoneandtwoandone_ two", markup);
	}

	public void testBasicTemplateNamedParameterMissingValue() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	public void testBasicTemplateNamedParameterMultiple() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}and{{{message2}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|message=foo bar|message2=baz}} two");
		assertEquals("one _expandedfoo barandbaz_ two", markup);
	}

	public void testBasicTemplateQualifiedName() {
		Template template = new Template();
		template.setName("Test:test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Test:test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	public void testBasicTemplateIncludeonly() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("foo bar baz\n<includeonly>_expanded_</includeonly>");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	public void testBasicTemplateNoInclude() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("<noinclude>foo bar baz\n</noinclude>_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	public void testBasicTemplateExcluded() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);
		markupLanguage.setTemplateExcludes("boo, baz, test");

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one  two", markup);
	}

	public void testBasicTemplateExcluded2() {
		Template template = new Template();
		template.setName("testBar");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);
		markupLanguage.setTemplateExcludes("boo, baz, test*");

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{testBar}} two");
		assertEquals("one  two", markup);
	}
}
