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

package org.eclipse.mylyn.internal.wikitext.ui;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.internal.wikitext.ui.registry.WikiTextExtensionPointReader;
import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.tests.AbstractTestInWorkspace;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.Test;

/**
 * A test that runs in the Eclipse UI that verifies that registered file types make sense.
 *
 * @author David Green
 */
@HeadRequired
@SuppressWarnings({ "nls", "restriction" })
public class FileTypesTest extends AbstractTestInWorkspace {

	private IProject project;

	@Override
	@Before
	public void before() {
		super.before();
		project = createSimpleProject();
	}

	@Test
	public void testTextileFileType() throws CoreException {
		IFile file = project.getFile("test.textile"); //$NON-NLS-1$
		file.create(createSimpleTextileContent(), false, new NullProgressMonitor());

		editorAsserts(file, TextileLanguage.class);
	}

	@Test
	public void testTextileFileTypeChangeIsSticky() throws CoreException {
		IFile file = project.getFile("test.textile"); //$NON-NLS-1$
		file.create(createSimpleTextileContent(), false, new NullProgressMonitor());

		// open the editor
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = openEditor(workbenchPage, file);
		assertInstanceOf(MarkupEditor.class, editor);
		MarkupEditor markupEditor = (MarkupEditor) editor;
		assertInstanceOf(TextileLanguage.class, markupEditor.getMarkupLanguage());

		// set the markup language
		markupEditor.setMarkupLanguage(WikiTextExtensionPointReader.instance().getMarkupLanguage("MediaWiki"), true); //$NON-NLS-1$
		assertInstanceOf(MediaWikiLanguage.class, markupEditor.getMarkupLanguage());

		// close the editor
		workbenchPage.closeEditor(editor, false);

		// open the editor
		editor = openEditor(workbenchPage, file);
		markupEditor = (MarkupEditor) editor;

		// verify the language setting is the same
		assertInstanceOf(MediaWikiLanguage.class, markupEditor.getMarkupLanguage());
	}

	@Test
	public void testMediaWikiFileType() throws CoreException {
		IFile file = project.getFile("test.mediawiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, MediaWikiLanguage.class);
	}

	@Test
	public void testTracWikiFileType() throws CoreException {
		IFile file = project.getFile("test.tracwiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, TracWikiLanguage.class);
	}

	@Test
	public void testTWikiFileType() throws CoreException {
		IFile file = project.getFile("test.twiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, TWikiLanguage.class);
	}

	@Test
	public void testConfluenceFileType() throws CoreException {
		IFile file = project.getFile("test.confluence"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, ConfluenceLanguage.class);
	}

	private void editorAsserts(IFile file, Class<?> markupLanguageClass) throws PartInitException {
		IEditorPart editor = openEditor(file);
		MarkupEditor markupEditor = (MarkupEditor) editor;
		assertInstanceOf(markupLanguageClass, markupEditor.getMarkupLanguage());
	}

	private IEditorPart openEditor(IFile file) throws PartInitException {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		return openEditor(workbenchPage, file);
	}

	private IEditorPart openEditor(IWorkbenchPage workbenchPage, IFile file) throws PartInitException {
		IContentType contentType = IDE.getContentType(file);
		IEditorDescriptor[] editorDescriptors = PlatformUI.getWorkbench()
				.getEditorRegistry()
				.getEditors(file.getName(), contentType);
		Optional<IEditorDescriptor> editorDescriptor = Arrays.asList(editorDescriptors)
				.stream()
				.filter(descriptor -> descriptor.getId().equals("org.eclipse.mylyn.wikitext.ui.editor.markupEditor"))
				.findFirst();
		assertTrue(format("Expected wikitext editor to be registered for file {0} of content type {1}", file.getName(),
				contentType), editorDescriptor.isPresent());

		IEditorPart editor = workbenchPage.openEditor(new FileEditorInput(file), editorDescriptor.get().getId());
		assertInstanceOf(MarkupEditor.class, editor);
		return editor;
	}

	private void assertInstanceOf(Class<?> clazz, Object o) {
		if (o != null && clazz.isAssignableFrom(o.getClass())) {
			return;
		}
		fail("Expected instanceof " + clazz.getName() + " but found " + (o == null ? null : o.getClass().getName()));
	}

	private InputStream createSimpleTextileContent() {
		return new ByteArrayInputStream("h1. Heading\n\ncontent\n".getBytes());
	}

	private InputStream createSimpleMediaWikiContent() {
		return new ByteArrayInputStream("= Heading =\n\ncontent\n".getBytes());
	}
}
