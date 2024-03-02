/*******************************************************************************
 * Copyright (c) 2016, 2024 Simon Scholz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Simon Scholz - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FileRefHyperlinkDetector;
import org.eclipse.mylyn.wikitext.tests.AbstractTestInWorkspace;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class FileRefHyperlinkDetectorTest extends AbstractTestInWorkspace {

	private IProject project;

	@Override
	@Before
	public void before() {
		super.before();
		project = createSimpleProject();
	}

	@Test
	public void testNoHyperlinkInDocument() throws CoreException {
		List<String> fileRefPatterns = List.of("include::(.+)\\[\\]", "image::(.+)\\[\\]");
		FileRefHyperlinkDetector fileRefHyperlinkDetector = new FileRefHyperlinkDetector(project, fileRefPatterns);

		ITextViewer mockTextViewer = mock(ITextViewer.class);
		when(mockTextViewer.getDocument()).thenReturn(new Document("Some contents without hyperlink in it."));

		IHyperlink[] detectHyperlinks = fileRefHyperlinkDetector.detectHyperlinks(mockTextViewer, new Region(7, 15),
				false);

		assertNull(detectHyperlinks);
	}

	@Test
	public void testFileDoesNotExist() throws CoreException {
		List<String> fileRefPatterns = List.of("include::(.+)\\[\\]", "image::(.+)\\[\\]");
		FileRefHyperlinkDetector fileRefHyperlinkDetector = new FileRefHyperlinkDetector(project, fileRefPatterns);

		ITextViewer mockTextViewer = mock(ITextViewer.class);
		when(mockTextViewer.getDocument()).thenReturn(new Document("image::file-that-does-not-exist.png[]"));

		IHyperlink[] detectHyperlinks = fileRefHyperlinkDetector.detectHyperlinks(mockTextViewer, new Region(7, 15),
				false);

		assertNull(detectHyperlinks);
	}

	@Test
	public void testFindFileRefAndOpenHyperlink() throws CoreException {
		String asciidocFileName = "simon-scholz.adoc";
		IFile file = project.getFile(asciidocFileName);
		file.create(new ByteArrayInputStream("== Writing tests is kinda documentation".getBytes()), true,
				new NullProgressMonitor());

		List<String> fileRefPatterns = List.of("include::(.+)\\[\\]", "image::(.+)\\[\\]");
		FileRefHyperlinkDetector fileRefHyperlinkDetector = new FileRefHyperlinkDetector(file.getParent(),
				fileRefPatterns);

		ITextViewer mockTextViewer = mock(ITextViewer.class);
		when(mockTextViewer.getDocument()).thenReturn(new Document("include::" + asciidocFileName + "[]"));

		IHyperlink[] detectHyperlinks = fileRefHyperlinkDetector.detectHyperlinks(mockTextViewer, new Region(9, 17),
				false);

		assertEquals(1, detectHyperlinks.length);

		IHyperlink hyperlink = detectHyperlinks[0];

		hyperlink.open();

		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActiveEditor();

		IEditorInput editorInput = activeEditor.getEditorInput();
		if (editorInput instanceof IFileEditorInput fei) {
			IFile editorFile = fei.getFile();

			assertEquals(file, editorFile);
		}
	}

	@Test
	public void testDeeperNestedFolderStructure() throws CoreException {
		IFolder folder = project.getFolder("AsciiDoctor Tutorial");
		if (!folder.exists()) {
			folder.create(IResource.NONE, true, new NullProgressMonitor());
		}
		IFolder nestedResourcesFolder = folder.getFolder("resources");
		if (!nestedResourcesFolder.exists()) {
			nestedResourcesFolder.create(IResource.NONE, true, new NullProgressMonitor());
		}
		IFile fileInNestedFolder = nestedResourcesFolder.getFile("nested-document.adoc");
		if (!fileInNestedFolder.exists()) {
			fileInNestedFolder.create(new ByteArrayInputStream("include::../../article.adoc[]".getBytes()), true,
					new NullProgressMonitor());
		}

		IFile articleFile = project.getFile("article.adoc");
		if (!articleFile.exists()) {
			articleFile.create(new ByteArrayInputStream("== Overview".getBytes()), true, new NullProgressMonitor());
		}

		List<String> fileRefPatterns = List.of("include::(.+)\\[\\]", "image::(.+)\\[\\]");
		FileRefHyperlinkDetector fileRefHyperlinkDetector = new FileRefHyperlinkDetector(fileInNestedFolder.getParent(),
				fileRefPatterns);

		ITextViewer mockTextViewer = mock(ITextViewer.class);
		when(mockTextViewer.getDocument()).thenReturn(new Document("include::../../article.adoc[]"));

		IHyperlink[] detectHyperlinks = fileRefHyperlinkDetector.detectHyperlinks(mockTextViewer, new Region(9, 17),
				false);

		assertEquals(1, detectHyperlinks.length);

		IHyperlink hyperlink = detectHyperlinks[0];

		hyperlink.open();

		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActiveEditor();

		IEditorInput editorInput = activeEditor.getEditorInput();
		if (editorInput instanceof IFileEditorInput fei) {
			IFile editorFile = fei.getFile();

			assertEquals(articleFile, editorFile);
		}
	}

}
