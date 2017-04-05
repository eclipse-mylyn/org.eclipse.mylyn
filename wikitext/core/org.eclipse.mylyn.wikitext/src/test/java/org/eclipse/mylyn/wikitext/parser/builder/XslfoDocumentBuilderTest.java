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

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;

import junit.framework.TestCase;

/**
 * @author David Green
 */
public class XslfoDocumentBuilderTest extends TestCase {
	private static final String XSLFO_BEGIN = "<root xmlns=\"http://www.w3.org/1999/XSL/Format\"><layout-master-set><simple-page-master master-name=\"page-layout\" page-height=\"29.7cm\" page-width=\"21.0cm\" margin=\"1.5cm\"><region-body margin-bottom=\"3cm\"/><region-after extent=\"2.0cm\" precedence=\"false\" region-name=\"footer\"/></simple-page-master></layout-master-set><page-sequence master-reference=\"page-layout\"><static-content flow-name=\"footer\"><block font-size=\"10.0pt\" text-align=\"outside\"><page-number/></block></static-content><flow flow-name=\"xsl-region-body\">";

	private static final String XSLFO_END = "</flow></page-sequence></root>";

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

	public void testMark() {
		builder.beginDocument();
		builder.characters("normal text ");
		builder.beginSpan(SpanType.MARK, new Attributes());
		builder.characters("marked text");
		builder.endSpan();
		builder.endDocument();

		String generatedContent = out.toString();
		String expectedContent = "normal text <inline font-style=\"italic\">marked text</inline>";

		assertEquals(XSLFO_BEGIN + expectedContent + XSLFO_END, generatedContent);
	}
}
