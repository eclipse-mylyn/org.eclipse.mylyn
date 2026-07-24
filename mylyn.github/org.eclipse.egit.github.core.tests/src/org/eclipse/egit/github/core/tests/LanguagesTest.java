/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.Languages;
import org.junit.Test;

/**
 * Unit tests of {@link Languages}
 */
public class LanguagesTest {

	/**
	 * Test languages
	 */
	@Test
	public void languages() {
		String[] languages = Languages.getLanguages();
		assertNotNull(languages);
		assertTrue(languages.length > 0);
		for (String lang : languages) {
			assertNotNull(lang);
			assertFalse(lang.length() == 0);
			assertFalse("HTML encoding found in language name", lang.contains("&"));
		}
	}

}
