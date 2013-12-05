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

package org.eclipse.mylyn.wikitext.core.parser.markup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.eclipse.mylyn.internal.wikitext.MockMarkupLanguage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableSet;

public class MarkupLanguageProviderTest {

	static class TestLanguageProvider extends MarkupLanguageProvider {
		@Override
		protected Set<MarkupLanguage> loadMarkupLanguages() {
			return ImmutableSet.<MarkupLanguage> of(new MockMarkupLanguage("Test"));
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getMarkupLanguages() {
		MarkupLanguageProvider provider = new TestLanguageProvider();
		assertNotNull(provider.getMarkupLanguages());
		assertEquals(1, provider.getMarkupLanguages().size());
		assertEquals("Test", provider.getMarkupLanguages().iterator().next().getName());
	}

	@Test
	public void getMarkupLanguagesNull() {
		MarkupLanguageProvider provider = new MarkupLanguageProvider() {

			@Override
			protected Set<MarkupLanguage> loadMarkupLanguages() {
				return null;
			}
		};
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("loadMarkupLanguages() must not return null");
		provider.getMarkupLanguages();
	}

	@Test
	public void getMarkupLanguagesDuplicatedNames() {
		MarkupLanguageProvider provider = new MarkupLanguageProvider() {

			@Override
			protected Set<MarkupLanguage> loadMarkupLanguages() {
				return ImmutableSet.<MarkupLanguage> of(new MockMarkupLanguage("Test"), new MockMarkupLanguage("Test"));
			}
		};
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Language name 'Test' must not be provided more than once");
		provider.getMarkupLanguages();
	}

	@Test
	public void getMarkupLanguagesNullName() {
		MarkupLanguageProvider provider = new MarkupLanguageProvider() {

			@Override
			protected Set<MarkupLanguage> loadMarkupLanguages() {
				MockMarkupLanguage language = new MockMarkupLanguage();
				language.setName(null);
				return ImmutableSet.<MarkupLanguage> of(language);
			}
		};
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Provided languages must have a name");
		provider.getMarkupLanguages();
	}
}
