/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.maven;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

public class FileToMarkupLanguageTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNull() {
		thrown.expect(NullPointerException.class);
		new FileToMarkupLanguage(null);
	}

	@Test
	public void getNull() {
		thrown.expect(NullPointerException.class);
		create().get(null);
	}

	@Test
	public void get() {
		assertNotNull(create().get(mockFile("content.Test")));
	}

	@Test
	public void getCaseInsensitive() {
		MarkupLanguage language1 = mockMarkupLanguage("Test");
		MarkupLanguage language2 = mockMarkupLanguage("Test2");
		FileToMarkupLanguage fileToMarkupLanguage = create(language1, language2);
		assertSame(language1, fileToMarkupLanguage.get(mockFile("test.Test")));
		assertSame(language1, fileToMarkupLanguage.get(mockFile("test.test")));
		assertSame(language2, fileToMarkupLanguage.get(mockFile("test.test2")));
		assertSame(language2, fileToMarkupLanguage.get(mockFile("test.tEst2")));
	}

	@Test
	public void getNotFound() {
		assertNull(create().get(mockFile("test.test3")));
	}

	@Test
	public void getEmptyExtension() {
		assertNull(create().get(mockFile("content.")));
	}

	@Test
	public void getNoExtension() {
		assertNull(create().get(mockFile("content")));
	}

	private File mockFile(String name) {
		File mock = mock(File.class);
		doReturn(name).when(mock).getName();
		return mock;
	}

	private FileToMarkupLanguage create() {
		return create(mockMarkupLanguage("Test"));
	}

	private FileToMarkupLanguage create(MarkupLanguage... languages) {
		return new FileToMarkupLanguage(Sets.newHashSet(languages));
	}

	private MarkupLanguage mockMarkupLanguage(String name) {
		MarkupLanguage mock = mock(MarkupLanguage.class);
		doReturn(name).when(mock).getName();
		doReturn(Sets.newHashSet(name)).when(mock).getFileExtensions();
		return mock;
	}
}
