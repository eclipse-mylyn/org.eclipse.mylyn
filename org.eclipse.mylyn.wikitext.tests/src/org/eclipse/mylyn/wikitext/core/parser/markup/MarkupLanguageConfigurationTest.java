/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.util.Locale;

import junit.framework.TestCase;

public class MarkupLanguageConfigurationTest extends TestCase {

	private MarkupLanguageConfiguration configuration;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configuration = new MarkupLanguageConfiguration();
	}

	public void testCloneWithLocale() {
		configuration.setLocale(Locale.ENGLISH);
		MarkupLanguageConfiguration copy = configuration.clone();
		assertEquals(configuration.getLocale(), copy.getLocale());
	}
}
