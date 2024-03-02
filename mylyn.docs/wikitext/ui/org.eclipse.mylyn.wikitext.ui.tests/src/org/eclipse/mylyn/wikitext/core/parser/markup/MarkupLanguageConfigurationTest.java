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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class MarkupLanguageConfigurationTest {

	private MarkupLanguageConfiguration configuration;

	@Before
	public void setUp() throws Exception {
		configuration = new MarkupLanguageConfiguration();
	}

	@Test
	public void testCloneWithLocale() {
		configuration.setLocale(Locale.ENGLISH);
		MarkupLanguageConfiguration copy = configuration.clone();
		assertEquals(configuration.getLocale(), copy.getLocale());
	}
}
