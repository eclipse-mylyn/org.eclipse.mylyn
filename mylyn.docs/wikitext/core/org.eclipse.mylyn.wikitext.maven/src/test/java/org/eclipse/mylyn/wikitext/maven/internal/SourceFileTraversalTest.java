/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.maven.internal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;

import org.eclipse.mylyn.wikitext.maven.internal.SourceFileTraversal.Visitor;
import org.junit.Test;

@SuppressWarnings("nls")
public class SourceFileTraversalTest {

	@Test(expected = NullPointerException.class)
	public void createNullRoot() {
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
		SourceFileTraversal traversal = new SourceFileTraversal(
				mockFolder("test", mockFolder("depth1", mockFolder("depth2", file))));
		Visitor visitor = mock(Visitor.class);
		traversal.traverse(visitor);
		verify(visitor).accept(eq("depth1" + File.separator + "depth2"), same(file));
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
