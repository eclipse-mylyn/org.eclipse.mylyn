/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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

}
