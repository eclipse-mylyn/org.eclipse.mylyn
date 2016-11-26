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

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;

import org.eclipse.mylyn.internal.wikitext.core.maven.SourceFileTraversal.Visitor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SourceFileTraversalTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNullRoot() {
		thrown.expect(NullPointerException.class);
		new SourceFileTraversal(null);
	}

	@Test
	public void traverseRootMatch() {
		File file = mockFile("one.Test");
		SourceFileTraversal traversal = new SourceFileTraversal(mockFolder("test", file));
		Visitor visitor = mock(Visitor.class);
		traversal.traverse(visitor);
		verify(visitor).accept(eq(""), same(file));
		verifyNoMoreInteractions(visitor);
	}

	@Test
	public void traverseMatchWithPath() {
		File file = mockFile("one.Test");
		SourceFileTraversal traversal = new SourceFileTraversal(mockFolder("test",
				mockFolder("depth1", mockFolder("depth2", file))));
		Visitor visitor = mock(Visitor.class);
		traversal.traverse(visitor);
		verify(visitor).accept(eq("depth1/depth2"), same(file));
		verifyNoMoreInteractions(visitor);
	}

	private File mockFolder(String name, File... children) {
		File mock = mock(File.class);
		doReturn(name).when(mock).getName();
		doReturn(true).when(mock).isDirectory();
		doReturn(true).when(mock).exists();
		doReturn(children).when(mock).listFiles();
		return mock;
	}

	private File mockFile(String name) {
		File mock = mock(File.class);
		doReturn(name).when(mock).getName();
		doReturn(true).when(mock).isFile();
		doReturn(true).when(mock).exists();
		return mock;
	}

}
