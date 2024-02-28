/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Jeremie Bresson - Bug 379783
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.Template;
import org.eclipse.mylyn.wikitext.mediawiki.TemplateResolver;
import org.eclipse.mylyn.wikitext.mediawiki.internal.TemplateProcessor;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class TemplateProcessorTest {

	private MediaWikiLanguage markupLanguage;

	@Before
	public void setUp() throws Exception {
		markupLanguage = new MediaWikiLanguage();
	}

	@Test
	public void testBasicTemplateNoParameters() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateNoNamespace() {
		//Bug 468237
		Template template = new Template();
		template.setName(":Test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{:Test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateOtherNamespace() {
		//Bug 468237
		Template template = new Template();
		template.setName("Other:Test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Other:Test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateNamedParameter() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|message=foo bar}} two");
		assertEquals("one _expandedfoo bar_ two", markup);
	}

	@Test
	public void testBasicTemplatePositionalParameter() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{1}}}and{{{2}}}and{{{1}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|one|two}} two");
		assertEquals("one _expandedoneandtwoandone_ two", markup);
	}

	@Test
	public void testBasicTemplatePositionalParameterWithSpaces() {
		Template template = new Template();
		template.setName("Note");
		template.setTemplateMarkup("<p class='note'>{{{1}}}</p>");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Note|comment with spaces.}} two");
		assertEquals("one <p class='note'>comment with spaces.</p> two", markup);
	}

	@Test
	public void testBasicTemplateNamedParameterMissingValue() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateNamedParameterMultiple() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message}}}and{{{message2}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test|message=foo bar|message2=baz}} two");
		assertEquals("one _expandedfoo barandbaz_ two", markup);
	}

	@Test
	public void testBasicTemplateQualifiedName() {
		Template template = new Template();
		template.setName("Test:test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Test:test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateWithSlashes() {
		//Bug 468237
		Template template = new Template();
		template.setName("Temp/Main");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Temp/Main}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateWithBrackets() {
		//Bug 468237
		Template template = new Template();
		template.setName("Templ(a)te");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Templ(a)te}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateIncludeonly() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("foo bar baz\n<includeonly>_expanded_</includeonly>");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplateNoInclude() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("<noinclude>foo bar baz\n</noinclude>_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
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

	@Test
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

	@Test
	public void testBasicTemplateExcludedWithWildcards() {
		Template template = new Template();
		template.setName("Foo:test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);
		markupLanguage.setTemplateExcludes("boo, baz, *test*");

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{Foo:test}} two");
		assertEquals("one  two", markup);
	}

	@Test
	public void testCaseSensitivity() {
		// bug 323224
		final Set<String> templateNames = new HashSet<>();
		markupLanguage.getTemplateProviders().add(new TemplateResolver() {

			@Override
			public Template resolveTemplate(String templateName) {
				templateNames.add(templateName);
				Template template = new Template();
				template.setName(templateName);
				template.setTemplateMarkup("test");
				return template;
			}
		});
		String[] names = { "One", "one", "OneTwo", "onetwo", "oneTwo" };
		for (String name : names) {
			templateNames.clear();

			TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);
			templateProcessor.processTemplates("content {{" + name + "}} more");

			assertContains(templateNames, name);
			assertEquals(1, templateNames.size());
		}
	}

	@Test
	public void testBasicTemplatesNoParametersRec() {
		//Bug 379783
		Template templateFoo = new Template();
		templateFoo.setName("foo");
		templateFoo.setTemplateMarkup("_{{bar}}expanded_");
		markupLanguage.getTemplates().add(templateFoo);

		Template templateBar = new Template();
		templateBar.setName("bar");
		templateBar.setTemplateMarkup("+exp+");
		markupLanguage.getTemplates().add(templateBar);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{foo}} two");
		assertEquals("one _+exp+expanded_ two", markup);
	}

	@Test
	public void testTemplateRepeated() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two {{test}}");
		assertEquals("one _expanded_ two _expanded_", markup);
	}

	@Test
	public void testBasicTemplateNoParametersRecLoopDetection() {
		//Bug 379783
		Template templateMer = new Template();
		templateMer.setName("mer");
		templateMer.setTemplateMarkup("�test{{mer}}test�");
		markupLanguage.getTemplates().add(templateMer);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("{{mer}}");
		assertEquals("�test<span class=\"error\">Template loop detected:mer</span>test�", markup);
	}

	@Test
	public void testBasicTemplatesNoParametersRecLoopDetection() {
		//Bug 379783
		Template template1 = new Template();
		template1.setName("rec_a");
		template1.setTemplateMarkup("+ rec_a {{rec_b}} rec_a +");
		markupLanguage.getTemplates().add(template1);

		Template template2 = new Template();
		template2.setName("rec_b");
		template2.setTemplateMarkup("+ rec_b {{rec_c}} rec_b +");
		markupLanguage.getTemplates().add(template2);

		Template template3 = new Template();
		template3.setName("rec_c");
		template3.setTemplateMarkup("+ rec_c {{rec_a}} rec_c +");
		markupLanguage.getTemplates().add(template3);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("{{rec_a}}");
		assertEquals(
				"+ rec_a + rec_b + rec_c <span class=\"error\">Template loop detected:rec_a</span> rec_c + rec_b + rec_a +",
				markup);
	}

	@Test
	public void testBasicTemplateDidgitInTheName() {
		//Bug 380052
		Template template = new Template();
		template.setName("1stTest");
		template.setTemplateMarkup("first test");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("This is a {{1stTest}}.");
		assertEquals("This is a first test.", markup);
	}

	@Test
	public void testBasicTemplateDidgitInTheName2() {
		//Bug 380052
		Template template = new Template();
		template.setName("Item2");
		template.setTemplateMarkup("second item");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("This is the {{Item2}}.");
		assertEquals("This is the second item.", markup);
	}

	@Test
	public void testBasicTemplateDidgitInTheName3() {
		//Bug 380052
		Template template = new Template();
		template.setName("foo999bar");
		template.setTemplateMarkup("foo-bar");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("foo{{foo999bar}}bar");
		assertEquals("foofoo-barbar", markup);
	}

	@Test
	public void testBasicTemplateNamedParameter_DefaultValue() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message|default value}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expandeddefault value_ two", markup);
	}

	@Test
	public void testBasicTemplateNamedParameter_EmptyDefaultValue() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{message|}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expanded_ two", markup);
	}

	@Test
	public void testBasicTemplatePositionalParameter_DefaultValue() {
		Template template = new Template();
		template.setName("test");
		template.setTemplateMarkup("_expanded{{{1|first}}}and{{{2|second}}}_");
		markupLanguage.getTemplates().add(template);

		TemplateProcessor templateProcessor = new TemplateProcessor(markupLanguage);

		String markup = templateProcessor.processTemplates("one {{test}} two");
		assertEquals("one _expandedfirstandsecond_ two", markup);
	}

	private void assertContains(Set<String> strings, String string) {
		assertTrue(String.format("Expected %s but got %s", string, strings), strings.contains(string));
	}
}
