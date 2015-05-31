/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark;

import static java.text.MessageFormat.format;
import static org.eclipse.mylyn.internal.wikitext.commonmark.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.osgi.OsgiServiceLocator;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndDocumentEvent;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Joiner;

public class CommonMarkLanguageTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final CommonMarkLanguage language = new CommonMarkLanguage();

	@Test
	public void name() {
		assertEquals("CommonMark", language.getName());
	}

	@Test
	public void processEmpty() {
		assertEvents("", new BeginDocumentEvent(), new EndDocumentEvent());
	}

	@Test
	public void processBlankLines() {
		assertEvents("\n\n\n\n\n", new BeginDocumentEvent(), new EndDocumentEvent());
	}

	@Test
	public void processDocumentFalse() {
		assertEvents("", false);
	}

	@Test
	public void processSimple() {
		assertEvents("first line\nsecond line\n\nnext para", new BeginDocumentEvent(),
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), new CharactersEvent("first line"),
				new CharactersEvent("\n"), new CharactersEvent("second line"), new EndBlockEvent(),
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), new CharactersEvent("next para"),
				new EndBlockEvent(), new EndDocumentEvent());
	}

	@Test
	public void isDiscoverable() {
		MarkupLanguage markupLanguage = OsgiServiceLocator.getApplicableInstance().getMarkupLanguage("CommonMark");
		assertNotNull(markupLanguage);
		assertEquals(CommonMarkLanguage.class, markupLanguage.getClass());
	}

	@Test
	public void strictlyConforming() {
		assertFalse(language.isStrictlyConforming());
		assertFalse(language.clone().isStrictlyConforming());
		assertNotNull(language.getIdGenerationStrategy());
		assertContent(language, "<p>one (<a href=\"http://example.com/#hey\">http://example.com/#hey</a>) two</p>",
				"one (http://example.com/#hey) two");
		assertContent(language, "<h1 id=\"a-heading\">A Heading</h1>", "# A Heading");
	}

	@Test
	public void strictlyConformingTrue() {
		language.setStrictlyConforming(true);
		assertTrue(language.isStrictlyConforming());
		assertTrue(language.clone().isStrictlyConforming());
		assertNull(language.getIdGenerationStrategy());
		assertContent(language, "<p>one (http://example.com/#hey) two</p>", "one (http://example.com/#hey) two");
		assertContent(language, "<h1>A Heading</h1>", "# A Heading");
	}

	@Test
	public void cloneTest() {
		CommonMarkLanguage language = new CommonMarkLanguage();
		assertNotNull(language.clone());
		assertEquals(language.getName(), language.clone().getName());
		assertEquals(language.isStrictlyConforming(), language.clone().isStrictlyConforming());
	}

	private void assertEvents(String content, DocumentBuilderEvent... events) {
		assertEvents(content, true, events);
	}

	private void assertEvents(String content, boolean asDocument, DocumentBuilderEvent... events) {
		MarkupParser parser = new MarkupParser(language);
		EventDocumentBuilder builder = new EventDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse(content, asDocument);
		List<DocumentBuilderEvent> expectedEvents = Arrays.asList(events);
		List<DocumentBuilderEvent> actualEvents = builder.getDocumentBuilderEvents().getEvents();
		assertEquals(format("Expected {0} but got {1}", toMessage(expectedEvents), toMessage(actualEvents)),
				expectedEvents, actualEvents);
	}

	private String toMessage(List<DocumentBuilderEvent> expectedEvents) {
		return Joiner.on(",\n").join(expectedEvents);
	}
}
