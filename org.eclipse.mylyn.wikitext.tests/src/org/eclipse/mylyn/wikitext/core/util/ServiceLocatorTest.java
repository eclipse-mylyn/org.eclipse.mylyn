/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
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
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * Tests for {@link ServiceLocator}
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
		MarkupLanguage markupLanguage;
		try {
			markupLanguage = locator.getMarkupLanguage("Textile");
		} catch (IllegalArgumentException e) {
			// if this test fails it may be because it's run stand-alone and the Textile
			// language is loaded from a folder instead of a jar file.
			fail(e.getMessage() + " NOTE: THIS TEST WILL FAIL IF THE CLASSES UNDER TEST ARE NOT IN A JAR");
			return;
		}
		assertNotNull(markupLanguage);
	}

	public void testUnknownLanguage() {
		try {
			locator.getMarkupLanguage("No Such Language asdlkfjal;sjdf");
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// expected, ignore
			TestUtil.println(e.getMessage());
		}
	}

	public void testFQN() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(TextileLanguage.class.getName());
		assertNotNull(markupLanguage);
	}

}
