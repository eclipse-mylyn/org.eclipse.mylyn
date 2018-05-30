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

import org.eclipse.egit.github.core.Language;
import org.junit.Test;

/**
 * Unit tests of {@link Language}
 */
public class LanguageTest {

	/**
	 * Test languages
	 */
	@Test
	public void languages() {
		assertNotNull(Language.values());
		assertTrue(Language.values().length > 0);
		for (Language lang : Language.values()) {
			assertNotNull(lang);
			assertNotNull(Language.valueOf(lang.name()));
			assertNotNull(lang.getValue());
			assertFalse(lang.getValue().length() == 0);
		}
	}

}
