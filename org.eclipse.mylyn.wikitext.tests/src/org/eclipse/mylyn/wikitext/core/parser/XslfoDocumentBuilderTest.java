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
package org.eclipse.mylyn.wikitext.core.parser;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.builder.XslfoDocumentBuilder;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * @author David Green
 */
public class XslfoDocumentBuilderTest extends TestCase {

	private MarkupParser parser;

	private StringWriter out;

	private XslfoDocumentBuilder builder;

	@Override
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new XslfoDocumentBuilder(out);
		parser.setBuilder(builder);
	}

	public void testImageWithWidthInPx() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setWidth(10);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		TestUtil.println("OUT: \n" + generatedContent);
		assertTrue(generatedContent.contains("width=\"10px\""));
	}

	public void testImageWithWidthInPct() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setWidth(10);
		attributes.setWidthPercentage(true);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		TestUtil.println("OUT: \n" + generatedContent);
		assertTrue(generatedContent.contains("width=\"10%\""));
	}

	public void testImageWithHeightInPx() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setHeight(10);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		TestUtil.println("OUT: \n" + generatedContent);
		assertTrue(generatedContent.contains("height=\"10px\""));
	}

	public void testImageWithHeightInPct() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setHeight(10);
		attributes.setHeightPercentage(true);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		TestUtil.println("OUT: \n" + generatedContent);
		assertTrue(generatedContent.contains("height=\"10%\""));
	}
}
