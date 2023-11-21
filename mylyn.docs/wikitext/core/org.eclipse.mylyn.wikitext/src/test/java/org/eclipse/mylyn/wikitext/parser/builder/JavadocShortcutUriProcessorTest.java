/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavadocShortcutUriProcessorTest {

	private final JavadocShortcutUriProcessor processor = new JavadocShortcutUriProcessor("../",
			"org.eclipse.mylyn.wikitext");

	@Test
	public void processAnyUri() {
		assertEquals("", processor.process(""));
		assertEquals("foo", processor.process("foo"));
		assertEquals("some/path/foo.html", processor.process("some/path/foo.html"));
	}

	@Test
	public void processPackageName() {
		assertEquals("../index.html?foo/package-summary.html", processor.process("@foo"));
		assertEquals("../index.html?foo/bar/package-summary.html", processor.process("@foo.bar"));
	}

	@Test
	public void processAbsoluteUriPackageName() {
		assertEquals("../index.html?foo/package-summary.html", processor.process("javadoc://foo"));
	}

	@Test
	public void processPackageNameWithBasePackage() {
		assertEquals("../index.html?org/eclipse/mylyn/wikitext/foo/package-summary.html", processor.process("@.foo"));
		assertEquals("../index.html?org/eclipse/mylyn/wikitext/foo/bar/package-summary.html",
				processor.process("@.foo.bar"));
	}

	@Test
	public void processTypeName() {
		assertEquals("../index.html?Foo.html", processor.process("@Foo"));
		assertEquals("../index.html?foo/Bar.html", processor.process("@foo.Bar"));
	}

	@Test
	public void processAbsoluteUriTypeName() {
		assertEquals("../index.html?Foo.html", processor.process("javadoc://Foo"));
	}

	@Test
	public void processTypeNameWithBasePackage() {
		assertEquals("../index.html?org/eclipse/mylyn/wikitext/Foo.html", processor.process("@.Foo"));
		assertEquals("../index.html?org/eclipse/mylyn/wikitext/foo/Bar.html", processor.process("@.foo.Bar"));
	}

	@Test
	public void processMalformedName() {
		assertEquals("@foo-bar", processor.process("@foo-bar"));
	}

	@Test
	public void target() {
		assertEquals("_javadoc", processor.target("@.Foo"));
		assertEquals(null, processor.target("Foo"));

	}
}
