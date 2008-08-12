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
package org.eclipse.mylyn.wikitext.core.parser.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.util.MarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class TextileToEclipseTocTest extends TestCase {

	private MarkupToEclipseToc textileToEclipseToc;

	@Override
	public void setUp() {
		textileToEclipseToc = new MarkupToEclipseToc();
		textileToEclipseToc.setMarkupLanguage(new TextileLanguage());
	}

	public void testHeader() throws Exception {
		textileToEclipseToc.setBookTitle("Test");
		textileToEclipseToc.setHtmlFile("Test.html");
		String toc = textileToEclipseToc.parse("h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n\nh2. Nested title\n\nnested content");

		System.out.println("Eclipse TOC: " + toc);
	}

}
