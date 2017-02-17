/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.parser.builder;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.parser.ImageAttributes;

import junit.framework.TestCase;

/**
 * @author David Green
 */
public class XslfoDocumentBuilderTest extends TestCase {

	private StringWriter out;

	private XslfoDocumentBuilder builder;

	@Override
	public void setUp() {
		out = new StringWriter();
		builder = new XslfoDocumentBuilder(out);
	}

	public void testImageWithWidthInPx() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setWidth(10);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		
		assertTrue(generatedContent.contains("width=\"10px\""));
	}

	public void testImageWithWidthInPct() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setWidth(10);
		attributes.setWidthPercentage(true);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		
		assertTrue(generatedContent.contains("width=\"10%\""));
	}

	public void testImageWithHeightInPx() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setHeight(10);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		
		assertTrue(generatedContent.contains("height=\"10px\""));
	}

	public void testImageWithHeightInPct() {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setHeight(10);
		attributes.setHeightPercentage(true);
		builder.image(attributes, "some/image.png");
		String generatedContent = out.toString();
		
		assertTrue(generatedContent.contains("height=\"10%\""));
	}
}
