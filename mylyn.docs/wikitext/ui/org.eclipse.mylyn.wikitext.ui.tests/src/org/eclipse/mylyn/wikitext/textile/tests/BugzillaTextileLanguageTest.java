/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.textile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.mylyn.internal.wikitext.ui.util.Util;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.parser.markup.block.JavaStackTraceBlock;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@EclipseRuntimeRequired
@SuppressWarnings("nls")
public class BugzillaTextileLanguageTest {

	private MarkupParser parser;

	@Before
	public void setUp() throws Exception {
		initParser();
	}

	private void initParser() throws IOException {
		parser = new MarkupParser();
		TextileLanguage markupLanguage = new TextileLanguage();
		MarkupLanguageConfiguration configuration = Util.create("bugzilla");
		markupLanguage.configure(configuration);
		parser.setMarkupLanguage(markupLanguage);
	}

	@Test
	public void testQuotedBlock() {
		String html = parser.parseToHtml("One\n\n> Two\n\nThree");

		assertTrue(html.contains("<body><p>One</p><blockquote><p>&gt; Two</p></blockquote><p>Three</p></body>"));
	}

	@Test
	public void testQuotedBlock2() {
		String html = parser.parseToHtml("One\n\n> Two\nThree");

		assertTrue(html.contains("<body><p>One</p><blockquote><p>&gt; Two</p></blockquote><p>Three</p></body>"));
	}

	@Test
	public void testQuotedBlock3() {
		String html = parser.parseToHtml("One\n> Two\n\nThree");

		assertTrue(html.contains("<body><p>One</p><blockquote><p>&gt; Two</p></blockquote><p>Three</p></body>"));
	}

	@Test
	public void testQuotedBlock4() {
		String html = parser.parseToHtml("One\n(In reply to comment #123)\n> Two\n\nThree");

		assertTrue(html.contains(
				"<body><p>One</p><blockquote><p>(In reply to comment #123)<br/>&gt; Two</p></blockquote><p>Three</p></body>"));
	}

	@Test
	public void testQuotedBlock5() {
		String html = parser.parseToHtml("One\n > Two\n > Three\nFour");

		assertTrue(html.contains(
				"<body><p>One</p><blockquote><p> &gt; Two<br/> &gt; Three</p></blockquote><p>Four</p></body>"));
	}

	@Test
	public void testThisBugHasBeen() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123 ***";

		String html = parser.parseToHtml(baseMarkup);

		assertTrue(html
				.contains("<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(" \t " + baseMarkup);

		assertTrue(html.contains(
				"<p style=\"color: Blue;\"> \t *** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(baseMarkup + "  ");

		assertTrue(html.contains(
				"<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***  </p>"));
	}

	@Test
	public void testThisBugHasBeenNegativeMatch() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123";

		String html = parser.parseToHtml(baseMarkup);

		assertTrue(html.contains("<body><ul><li><ul><li><ul><li>This bug has been marked as a duplicate of bug 123"));
	}

	@Test
	public void testBugFooHasBeenMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug. ***";
		String html = parser.parseToHtml(markup);

		assertTrue(html.contains(
				"<body><p style=\"color: Blue;\">*** Bug 209610 has been marked as a duplicate of this bug. ***</p>"));
	}

	@Test
	public void testBugFooHasBeenNegativeMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug.";
		String html = parser.parseToHtml(markup);

		assertTrue(
				html.contains("<body><ul><li><ul><li><ul><li>Bug 209610 has been marked as a duplicate of this bug."));
	}

	@Test
	public void testXmlEscaping() {
		String html = parser.parseToHtml("some <start>mark</start> up");

		assertTrue(html.contains("<p>some &lt;start&gt;mark&lt;/start&gt; up</p>"));
	}

	@Test
	public void testHtmlEscaping() {
		String html = parser.parseToHtml("some <span class=\"s\">mark</span> up");

		assertTrue(html.contains("<p>some &lt;span class=\"s\"&gt;mark&lt;/span&gt; up</p>"));
	}

	@Test
	public void testJavaStackTraceDetection() {
		String html = parser.parseToHtml("""
				text
				java.lang.Exception: java.lang.IllegalStateException
					at org.eclipse.mylyn.internal.wikitext.tasks.ui.util.Test.main(Test.java:21)
				Caused by: java.lang.IllegalStateException
					... 1 more
				text""");

		assertTrue(html.contains("<p>text</p><pre class=\"javaStackTrace\">java.lang.Exception"));
		assertTrue(html.contains("</pre><p>text</p>"));
	}

	@Test
	public void testJavaStackTraceDetection_bug280805() {
		String html = parser.parseToHtml("""
				text
				java.lang.Exception: java.lang.IllegalStateException
					at org.eclipse.mylyn.internal.wikitext.tasks.ui.util.test.main(Test.java:21)
				Caused by: java.lang.IllegalStateException
					... 1 more
				text""");

		assertTrue(html.contains("<p>text</p><pre class=\"javaStackTrace\">java.lang.Exception"));
		assertTrue(html.contains("</pre><p>text</p>"));
	}

	@Test
	public void testJavaStackTraceDetection_bug273629() {
		boolean canStart = new JavaStackTraceBlock().canStart(
				"org.eclipse.ui.internal.PerspectiveBarContributionItem.select(PerspectiveBarContributionItem.java:124)",
				0);
		assertTrue(canStart);
	}

	@Test
	public void testJavaStackTraceDetection_bug283629() {
		boolean canStart = new JavaStackTraceBlock()
				.canStart(" org.eclipse.ui.texteditor.AbstractDecoratedTextEditor$11.run()V+165", 0);
		assertFalse(canStart);
	}

	@Test
	public void testJavaStackTraceDetection_bug298781() {
		// bug 298781 <clinit> not detected correctly
		String markup = "java.io.EOFException\nat java.io.DataInputStream.readInt(Unknown Source)\nat org.eclipse.jdt.internal.core.JavaModelManager.loadNonChainingJarsCache(JavaModelManager.java:2843)\nat org.eclipse.jdt.internal.core.JavaModelManager.<init>(JavaModelManager.java:1477)\nat org.eclipse.jdt.internal.core.JavaModelManager.<clinit>(JavaModelManager.java:1012)\nat org.eclipse.jdt.core.JavaCore.start(JavaCore.java:4965)\nat org.eclipse.osgi.framework.internal.core.BundleContextImpl$1.run(BundleContextImpl.java:783)\nat java.security.AccessController.doPrivileged(Native Method)";
		String html = parser.parseToHtml(markup);

		assertTrue(html.contains(
				"<pre class=\"javaStackTrace\">java.io.EOFException\nat java.io.DataInputStream.readInt(Unknown Source)\nat org.eclipse.jdt.internal.core.JavaModelManager.loadNonChainingJarsCache(JavaModelManager.java:2843)\nat org.eclipse.jdt.internal.core.JavaModelManager.&lt;init&gt;(JavaModelManager.java:1477)\nat org.eclipse.jdt.internal.core.JavaModelManager.&lt;clinit&gt;(JavaModelManager.java:1012)\nat org.eclipse.jdt.core.JavaCore.start(JavaCore.java:4965)\nat org.eclipse.osgi.framework.internal.core.BundleContextImpl$1.run(BundleContextImpl.java:783)\nat java.security.AccessController.doPrivileged(Native Method)\n</pre>"));
	}

	@Test
	public void testJavaStackTraceDetection_bug391723() {
		String markup = """
				java.lang.IllegalStateException: message
					at com.foo.Bar.baz(Bar.java:199)
					at $Proxy40.findProcessArea(Unknown Source)""";

		String html = parser.parseToHtml(markup);

		assertEquals("""
				<body><pre class="javaStackTrace">java.lang.IllegalStateException: message
					at com.foo.Bar.baz(Bar.java:199)
					at $Proxy40.findProcessArea(Unknown Source)
				</pre></body>""", TestUtil.tagFragment("body", html));
	}

	@Test
	public void testJavaStackTraceDetection_bug432153() {
		String markup = "java.lang.Exception: message\nat com.sun.proxy.$Proxy0.refresh(Unknown Source)";

		String html = parser.parseToHtml(markup);

		assertEquals("""
				<body><pre class="javaStackTrace">java.lang.Exception: message
				at com.sun.proxy.$Proxy0.refresh(Unknown Source)
				</pre></body>""", TestUtil.tagFragment("body", html));
	}

	@Test
	public void testEclipseErrorDetailsBlock() {
		String html = parser.parseToHtml("text\n-- Error Details --\ndetail line 1\n\nno detail");

		assertTrue(html.contains("<p>text</p><pre class=\"eclipseErrorDetails\">-- Error Details --"));
		assertTrue(html.contains("</pre><p>no detail</p>"));
	}
}
