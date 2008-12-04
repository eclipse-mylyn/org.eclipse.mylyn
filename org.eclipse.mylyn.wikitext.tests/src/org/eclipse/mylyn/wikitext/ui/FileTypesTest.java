/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.wikitext.confluence.core.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.tests.AbstractTestInWorkspace;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * A test that runs in the Eclipse UI that verifies that registered file types make sense.
 * 
 * @author David Green
 */
@HeadRequired
public class FileTypesTest extends AbstractTestInWorkspace {

	private IProject project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createSimpleProject();
	}

	public void testTextileFileType() throws CoreException {
		IFile file = project.getFile("test.textile"); //$NON-NLS-1$
		file.create(createSimpleTextileContent(), false, new NullProgressMonitor());

		editorAsserts(file, TextileLanguage.class);
	}

	public void testTextileFileTypeChangeIsSticky() throws CoreException {
		IFile file = project.getFile("test.textile"); //$NON-NLS-1$
		file.create(createSimpleTextileContent(), false, new NullProgressMonitor());

		// open the editor
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = IDE.openEditor(workbenchPage, file);
		assertInstanceOf(MarkupEditor.class, editor);
		MarkupEditor markupEditor = (MarkupEditor) editor;
		assertInstanceOf(TextileLanguage.class, markupEditor.getMarkupLanguage());

		// set the markup language
		markupEditor.setMarkupLanguage(WikiTextPlugin.getDefault().getMarkupLanguage("MediaWiki"), true); //$NON-NLS-1$
		assertInstanceOf(MediaWikiLanguage.class, markupEditor.getMarkupLanguage());

		// close the editor
		workbenchPage.closeEditor(editor, false);

		// open the editor
		editor = IDE.openEditor(workbenchPage, file);
		markupEditor = (MarkupEditor) editor;

		// verify the language setting is the same
		assertInstanceOf(MediaWikiLanguage.class, markupEditor.getMarkupLanguage());
	}

	public void testMediaWikiFileType() throws CoreException {
		IFile file = project.getFile("test.mediawiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, MediaWikiLanguage.class);
	}

	public void testTracWikiFileType() throws CoreException {
		IFile file = project.getFile("test.tracwiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, TracWikiLanguage.class);
	}

	public void testTWikiFileType() throws CoreException {
		IFile file = project.getFile("test.twiki"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, TWikiLanguage.class);
	}

	public void testConfluenceFileType() throws CoreException {
		IFile file = project.getFile("test.confluence"); //$NON-NLS-1$
		file.create(createSimpleMediaWikiContent(), false, new NullProgressMonitor());

		editorAsserts(file, ConfluenceLanguage.class);
	}

	private void editorAsserts(IFile file, Class<?> markupLanguageClass) throws PartInitException {
		IEditorPart editor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
		assertInstanceOf(MarkupEditor.class, editor);
		MarkupEditor markupEditor = (MarkupEditor) editor;
		assertInstanceOf(markupLanguageClass, markupEditor.getMarkupLanguage());
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
