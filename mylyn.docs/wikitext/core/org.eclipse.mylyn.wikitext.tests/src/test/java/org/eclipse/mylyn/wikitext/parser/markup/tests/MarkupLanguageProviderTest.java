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
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.markup.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.parser.tests.MockMarkupLanguage;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class MarkupLanguageProviderTest {

	static class TestLanguageProvider extends MarkupLanguageProvider {
		@Override
		protected Set<MarkupLanguage> loadMarkupLanguages() {
			return Set.of(new MockMarkupLanguage("Test"));
		}
	}

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
		NullPointerException npe = assertThrows(NullPointerException.class, () -> provider.getMarkupLanguages());
		assertTrue(npe.getMessage().contains("loadMarkupLanguages() must not return null"));
	}

	@Test
	public void getMarkupLanguagesDuplicatedNames() {
		MarkupLanguageProvider provider = new MarkupLanguageProvider() {

			@Override
			protected Set<MarkupLanguage> loadMarkupLanguages() {
				return Set.of(new MockMarkupLanguage("Test"), new MockMarkupLanguage.MockMarkupLanguage2("Test"));
			}
		};
		IllegalStateException ise = assertThrows(IllegalStateException.class, () -> provider.getMarkupLanguages());
		assertTrue(ise.getMessage().contains("Language name 'Test' must not be provided more than once"));
	}

	@Test
	public void getMarkupLanguagesNullName() {
		MarkupLanguageProvider provider = new MarkupLanguageProvider() {

			@Override
			protected Set<MarkupLanguage> loadMarkupLanguages() {
				MockMarkupLanguage language = new MockMarkupLanguage();
				language.setName(null);
				return Set.of(language);
			}
		};
		NullPointerException npe = assertThrows(NullPointerException.class, () -> provider.getMarkupLanguages());
		assertTrue(npe.getMessage().contains("Provided languages must have a name"));
	}
}
