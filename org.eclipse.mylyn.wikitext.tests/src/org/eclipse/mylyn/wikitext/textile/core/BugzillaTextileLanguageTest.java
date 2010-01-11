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
package org.eclipse.mylyn.wikitext.textile.core;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.tasks.ui.util.Util;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.core.parser.markup.block.JavaStackTraceBlock;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * @author David Green
 */
@EclipseRuntimeRequired
@HeadRequired
public class BugzillaTextileLanguageTest extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initParser();
	}

	private void initParser() throws IOException {
		parser = new MarkupParser();
		TextileLanguage markupLanaguage = new TextileLanguage();
		MarkupLanguageConfiguration configuration = Util.create("bugzilla");
		markupLanaguage.configure(configuration);
		parser.setMarkupLanguage(markupLanaguage);
	}

	public void testQuotedBlock() {
		String html = parser.parseToHtml("One\n\n> Two\n\nThree");
		TestUtil.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock2() {
		String html = parser.parseToHtml("One\n\n> Two\nThree");
		TestUtil.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock3() {
		String html = parser.parseToHtml("One\n> Two\n\nThree");
		TestUtil.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock4() {
		String html = parser.parseToHtml("One\n(In reply to comment #123)\n> Two\n\nThree");
		TestUtil.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>(In reply to comment #123)<br/>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock5() {
		String html = parser.parseToHtml("One\n > Two\n > Three\nFour");
		TestUtil.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p> > Two<br/> > Three</p></blockquote><p>Four</p></body>"));
	}

	public void testThisBugHasBeen() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123 ***";

		String html = parser.parseToHtml(baseMarkup);
		TestUtil.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(" \t " + baseMarkup);
		TestUtil.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\"> \t *** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(baseMarkup + "  ");
		TestUtil.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***  </p>"));
	}

	public void testThisBugHasBeenNegativeMatch() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123";

		String html = parser.parseToHtml(baseMarkup);
		TestUtil.println(html);
		assertTrue(html.contains("<body><ul><li><ul><li><ul><li>This bug has been marked as a duplicate of bug 123"));
	}

	public void testBugFooHasBeenMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug. ***";
		String html = parser.parseToHtml(markup);
		TestUtil.println(html);
		assertTrue(html.contains("<body><p style=\"color: Blue;\">*** Bug 209610 has been marked as a duplicate of this bug. ***</p>"));
	}

	public void testBugFooHasBeenNegativeMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug.";
		String html = parser.parseToHtml(markup);
		TestUtil.println(html);
		assertTrue(html.contains("<body><ul><li><ul><li><ul><li>Bug 209610 has been marked as a duplicate of this bug."));
	}

	public void testXmlEscaping() {
		String html = parser.parseToHtml("some <start>mark</start> up");
		TestUtil.println(html);
		assertTrue(html.contains("<p>some &lt;start>mark&lt;/start> up</p>"));
	}

	public void testHtmlEscaping() {
		String html = parser.parseToHtml("some <span class=\"s\">mark</span> up");
		TestUtil.println(html);
		assertTrue(html.contains("<p>some &lt;span class=\"s\">mark&lt;/span> up</p>"));
	}

	public void testJavaStackTraceDetection() {
		String html = parser.parseToHtml("text\n" + "java.lang.Exception: java.lang.IllegalStateException\n"
				+ "	at org.eclipse.mylyn.internal.wikitext.tasks.ui.util.Test.main(Test.java:21)\n"
				+ "Caused by: java.lang.IllegalStateException\n" + "	... 1 more\n" + "text");
		TestUtil.println(html);
		assertTrue(html.contains("<p>text</p><pre class=\"javaStackTrace\">java.lang.Exception"));
		assertTrue(html.contains("</pre><p>text</p>"));
	}

	public void testJavaStackTraceDetection_bug280805() {
		String html = parser.parseToHtml("text\n" + "java.lang.Exception: java.lang.IllegalStateException\n"
				+ "	at org.eclipse.mylyn.internal.wikitext.tasks.ui.util.test.main(Test.java:21)\n"
				+ "Caused by: java.lang.IllegalStateException\n" + "	... 1 more\n" + "text");
		TestUtil.println(html);
		assertTrue(html.contains("<p>text</p><pre class=\"javaStackTrace\">java.lang.Exception"));
		assertTrue(html.contains("</pre><p>text</p>"));
	}

	public void testJavaStackTraceDetection_bug273629() {
		boolean canStart = new JavaStackTraceBlock().canStart(
				"org.eclipse.ui.internal.PerspectiveBarContributionItem.select(PerspectiveBarContributionItem.java:124)",
				0);
		assertTrue(canStart);
	}

	public void testJavaStackTraceDetection_bug283629() {
		boolean canStart = new JavaStackTraceBlock().canStart(
				" org.eclipse.ui.texteditor.AbstractDecoratedTextEditor$11.run()V+165", 0);
		assertFalse(canStart);
	}

	public void testJavaStackTraceDetection_bug298781() {
		// bug 298781 <clinit> not detected correctly
		String markup = "java.io.EOFException\nat java.io.DataInputStream.readInt(Unknown Source)\nat org.eclipse.jdt.internal.core.JavaModelManager.loadNonChainingJarsCache(JavaModelManager.java:2843)\nat org.eclipse.jdt.internal.core.JavaModelManager.<init>(JavaModelManager.java:1477)\nat org.eclipse.jdt.internal.core.JavaModelManager.<clinit>(JavaModelManager.java:1012)\nat org.eclipse.jdt.core.JavaCore.start(JavaCore.java:4965)\nat org.eclipse.osgi.framework.internal.core.BundleContextImpl$1.run(BundleContextImpl.java:783)\nat java.security.AccessController.doPrivileged(Native Method)";
		String html = parser.parseToHtml(markup);

		TestUtil.println(html);

		assertTrue(html.contains("<pre class=\"javaStackTrace\">java.io.EOFException\nat java.io.DataInputStream.readInt(Unknown Source)\nat org.eclipse.jdt.internal.core.JavaModelManager.loadNonChainingJarsCache(JavaModelManager.java:2843)\nat org.eclipse.jdt.internal.core.JavaModelManager.&lt;init>(JavaModelManager.java:1477)\nat org.eclipse.jdt.internal.core.JavaModelManager.&lt;clinit>(JavaModelManager.java:1012)\nat org.eclipse.jdt.core.JavaCore.start(JavaCore.java:4965)\nat org.eclipse.osgi.framework.internal.core.BundleContextImpl$1.run(BundleContextImpl.java:783)\nat java.security.AccessController.doPrivileged(Native Method)\n</pre>"));
	}

	public void testEclipseErrorDetailsBlock() {
		String html = parser.parseToHtml("text\n-- Error Details --\ndetail line 1\n\nno detail");
		TestUtil.println(html);
		assertTrue(html.contains("<p>text</p><pre class=\"eclipseErrorDetails\">-- Error Details --"));
		assertTrue(html.contains("</pre><p>no detail</p>"));
	}
}
