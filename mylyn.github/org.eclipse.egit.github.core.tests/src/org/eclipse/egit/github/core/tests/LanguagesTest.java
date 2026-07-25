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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.egit.github.core.Languages;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link Languages}
 */
@SuppressWarnings("nls")
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
			assertFalse(lang.contains("&"), "HTML encoding found in language name");
		}
	}

}
