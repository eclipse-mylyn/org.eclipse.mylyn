/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 266693
 *     Abner Ballardo - fix for bug 288427
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.workbench.browser.UrlHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskUrlHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 * @author David Green
 */
public class TaskUrlHyperlinkDetectorTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = TaskTestUtil.createMockRepository();
	}

	protected IHyperlink[] detect(final String text, int start, int length) {
		AbstractHyperlinkDetector detector = new TaskUrlHyperlinkDetector();
		detector.setContext(new IAdaptable() {
			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter) {
				return repository;
			}
		});
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
		assertEquals("http://foo", ((UrlHyperlink) links[0]).getURLString());
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
		assertEquals("http://foo", ((UrlHyperlink) links[0]).getURLString());

		links = detect("( http://foo)", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((UrlHyperlink) links[0]).getURLString());

		links = detect("( http://foo).", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo", ((UrlHyperlink) links[0]).getURLString());
	}

	public void testClosingParenthesis() {
		IHyperlink[] links = detect("http://foo?(bar)", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo?(bar)", ((UrlHyperlink) links[0]).getURLString());

		links = detect("(http://foo?(bar))", 0, 18);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo?(bar)", ((UrlHyperlink) links[0]).getURLString());

		links = detect("http://foo?((((bar).", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo?((((bar)", ((UrlHyperlink) links[0]).getURLString());

		links = detect("http://foo?(bar))))))))", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://foo?(bar)", ((UrlHyperlink) links[0]).getURLString());
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

	public void testDetectionMultiplelinesClosingParenthesis() {
		String text = "aa http://www.eclipse.org?foo((bar)\n\n)(http://www.eclipse.org)\nhttp://www.eclipse.org()";
		IHyperlink[] hyperlinks = detect(text, 0, text.length());
		assertNotNull(hyperlinks);
		assertEquals(3, hyperlinks.length);
		assertEquals(new Region(3, 32), hyperlinks[0].getHyperlinkRegion());
		assertEquals(new Region(39, 22), hyperlinks[1].getHyperlinkRegion());
		assertEquals(new Region(63, 24), hyperlinks[2].getHyperlinkRegion());
	}

	public void testDetectionMultiplelines() {
		String text = "aa http://www.eclipse.org\n\nhttp://www.eclipse.org.\nhttp://www.eclipse.org,";
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

	public void testDetectionNoRepositoryRegularUrl() {
		repository = null;
		IHyperlink[] hyperlinks = detect("aa http://www.eclipse.org test", 4, 0);
		assertNull(hyperlinks);
	}

	/**
	 * Tests hyperlink detector in mode outside of task editor.
	 */
	public void testDetectionNoRepositoryRepositoryUrl() {
		repository = null;
		TaskRepository repository1 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://repo1.test/foo");
		try {
			TasksUi.getRepositoryManager().addRepository(repository1);

			String text = "aa http://repo1.test/foo http://www.eclipse.org test";
			IHyperlink[] hyperlinks = detect(text, 0, text.length());
			assertNotNull(hyperlinks);
			assertEquals(1, hyperlinks.length);
			assertEquals(new Region(3, 21), hyperlinks[0].getHyperlinkRegion());
		} finally {
			TasksUiPlugin.getRepositoryManager().removeRepository(repository1);
		}
	}

	/**
	 * Tests hyperlink detector in mode outside of task editor with multiple task URLs.
	 */
	public void testDetectionNoRepositoryMultipleRepositoryUrls() {
		repository = null;
		TaskRepository repository1 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://repo1.test/foo");
		TaskRepository repository2 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://repo2.test");
		try {
			TasksUi.getRepositoryManager().addRepository(repository1);
			TasksUi.getRepositoryManager().addRepository(repository2);

			String text = "aa http://repo2.test http://repo1.test/foo http://repo1.test/bar http://www.eclipse.org test";
			IHyperlink[] hyperlinks = detect(text, 0, text.length());
			assertNotNull(hyperlinks);
			assertEquals(2, hyperlinks.length);
		} finally {
			TasksUiPlugin.getRepositoryManager().removeRepository(repository1);
			TasksUiPlugin.getRepositoryManager().removeRepository(repository2);
		}
	}

}
