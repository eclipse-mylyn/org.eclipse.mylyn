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
package org.eclipse.mylyn.wikitext.core.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class ServiceLocatorTest extends TestCase {

	private ServiceLocator locator;

	@Override
	public void setUp() {
		locator = ServiceLocator.getInstance(ServiceLocatorTest.class.getClassLoader());
	}

	public void testKnownLanguage() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage("Textile");
		assertNotNull(markupLanguage);
	}

	public void testUnknownLanguage() {
		try {
			locator.getMarkupLanguage("No Such Language asdlkfjal;sjdf");
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// expected, ignore
			System.out.println(e.getMessage());
		}
	}

	public void testFQN() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(TextileLanguage.class.getName());
		assertNotNull(markupLanguage);
	}
}
