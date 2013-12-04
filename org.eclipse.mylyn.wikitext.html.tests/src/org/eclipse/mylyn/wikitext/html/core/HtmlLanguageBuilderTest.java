/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlLanguageBuilderTest {

	private HtmlLanguageBuilder builder;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		builder = new HtmlLanguageBuilder();
	}

	@Test
	public void nameNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a name");
		builder.name(null);
	}

	@Test
	public void nameEmpty() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not be empty");
		builder.name("");
	}

	@Test
	public void nameLeadingWhitespace() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not have leading or trailing whitespace");
		builder.name(" Name");
	}

	@Test
	public void nameBlacklisted() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML);
	}

	@Test
	public void nameBlacklisted2() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML.toLowerCase());
	}

	@Test
	public void nameBlacklisted3() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML.toUpperCase());
	}

	@Test
	public void name() {
		assertNotNull(builder.name("Test"));
		assertSame(builder, builder.name("Test"));
	}

	@Test
	public void create() {
		HtmlLanguage language = builder.name("Test").create();
		assertNotNull(language);
		assertEquals("Test", language.getName());
	}

	protected void expectBlacklisted() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not be equal to " + HtmlLanguage.NAME_HTML);
	}

	@Test
	public void createWithoutName() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Name must be provided to create an HtmlLanguage");
		builder.create();
	}
}
