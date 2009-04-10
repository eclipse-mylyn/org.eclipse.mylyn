/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 266693
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskUrlHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskUrlHyperlinkDetector;

/**
 * @author Steffen Pingel
 * @author David Green
 */
public class TaskUrlHyperlinkDetectorTest extends TestCase {

	protected IHyperlink[] detect(final String text, int start, int length) {
		AbstractHyperlinkDetector detector = new TaskUrlHyperlinkDetector();
		return detector.detectHyperlinks(new TextViewer() {
			@Override
			public IDocument getDocument() {
				return new Document(text);
			}
		}, new Region(start, length), true);
	}

	public void testUrl() {
		IHyperlink[] links = detect("http://foo", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((TaskUrlHyperlink) links[0]).getURLString());
	}

	public void testInvalidUrl() {
		IHyperlink[] links = detect("abc", 0, 0);
		assertNull(links);

		links = detect("", 0, 0);
		assertNull(links);

		links = detect(").", 0, 0);
		assertNull(links);
	}

	public void testParenthesis() {
		IHyperlink[] links = detect("(http://foo)", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((TaskUrlHyperlink) links[0]).getURLString());

		links = detect("( http://foo)", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((TaskUrlHyperlink) links[0]).getURLString());

		links = detect("( http://foo).", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((TaskUrlHyperlink) links[0]).getURLString());
	}

	public void testDetectionUsingExtent() {
		IHyperlink[] hyperlinks = detect("aa http://www.eclipse.org test", 0, 30);
		assertNotNull(hyperlinks);
		assertEquals(1, hyperlinks.length);
	}

	public void testDetectionMultipleLinks() {
		String text = "aa http://www.eclipse.org test http://www.foo.bar/baz?one=two&three=four+five#six";
		IHyperlink[] hyperlinks = detect(text, 0, text.length());
		assertNotNull(hyperlinks);
		assertEquals(2, hyperlinks.length);
		assertEquals(new Region(3, 22), hyperlinks[0].getHyperlinkRegion());
		assertEquals(new Region(31, 50), hyperlinks[1].getHyperlinkRegion());
	}

	public void testDetectionNegativeMatchOnTrailingPunctuation() {
		String text = "aa http://www.eclipse.org) http://www.eclipse.org. http://www.eclipse.org,";
		IHyperlink[] hyperlinks = detect(text, 0, text.length());
		assertNotNull(hyperlinks);
		assertEquals(3, hyperlinks.length);
		assertEquals(new Region(3, 22), hyperlinks[0].getHyperlinkRegion());
		assertEquals(new Region(27, 22), hyperlinks[1].getHyperlinkRegion());
		assertEquals(new Region(51, 22), hyperlinks[2].getHyperlinkRegion());
	}

	public void testDetection() {
		IHyperlink[] hyperlinks = detect("aa http://www.eclipse.org test", 20, 0);
		assertNotNull(hyperlinks);
		assertEquals(1, hyperlinks.length);
	}

	public void testDetection2() {
		String text = "http://www.eclipse.org";
		IHyperlink[] hyperlinks = detect(text, 0, text.length());
		assertNotNull(hyperlinks);
		assertEquals(1, hyperlinks.length);
		assertEquals(new Region(0, 22), hyperlinks[0].getHyperlinkRegion());
	}

}
