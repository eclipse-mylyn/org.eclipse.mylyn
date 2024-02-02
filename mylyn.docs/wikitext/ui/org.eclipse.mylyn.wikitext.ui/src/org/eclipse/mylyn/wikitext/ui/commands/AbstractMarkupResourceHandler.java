/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.commands;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * An abstract base class for handlers that use the workbench selection to operate on resources
 *
 * @author David Green
 */
public abstract class AbstractMarkupResourceHandler extends AbstractHandler {

	protected MarkupLanguage markupLanguage;

	private IStructuredSelection selection;

	@Override
	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection currentSelection = selection;
		if (currentSelection == null) {
			try {
				currentSelection = computeSelection(event);
			} catch (Exception e) {
				// ignore
			}
		}

		if (currentSelection instanceof IStructuredSelection structuredSelection) {
			Iterator<Object> it = structuredSelection.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				IFile file = null;
				if (o instanceof IAdaptable adapt) {
					file = adapt.getAdapter(IFile.class);
				}
				if (file != null) {
					String name = file.getName();
					int idxOfDot = name.lastIndexOf('.');
					if (idxOfDot != -1) {
						name = name.substring(0, idxOfDot);
					}
					// use a temporary so that the setting does not stick even if the handler is reused.
					MarkupLanguage prev = markupLanguage;
					try {
						if (markupLanguage == null) {
							markupLanguage = MarkupEditor.loadMarkupLanguagePreference(file);
							if (markupLanguage == null) {
								markupLanguage = WikiText.getMarkupLanguageForFilename(file.getName());
							}
							if (markupLanguage == null) {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										Messages.AbstractMarkupResourceHandler_unexpectedError,
										NLS.bind(Messages.AbstractMarkupResourceHandler_markupLanguageMappingFailed,
												new Object[] { file.getName() }));
								return null;
							}
						}

						handleFile(event, file, name);
					} finally {
						markupLanguage = prev;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Classes that need access to the {@code event} should override this method. The default implementation simply calls
	 * {@link #handleFile(IFile, String)}
	 *
	 * @since 1.9
	 * @see #handleFile(IFile, String)
	 */
	protected void handleFile(ExecutionEvent event, IFile file, String name) throws ExecutionException {
		handleFile(file, name);
	}

	/**
	 * @param event
	 * @since 1.1
	 */
	protected ISelection computeSelection(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			selection = HandlerUtil.getActiveMenuEditorInput(event);
		}
		if (!(selection instanceof IStructuredSelection)) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		if (!(selection instanceof IStructuredSelection)) {
			selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		}
		return selection;
	}

	/**
	 * Perform the command's function on the given file.
	 *
	 * @param file
	 *            the input file to process
	 * @param name
	 *            the name of the output file without file extension
	 * @see #handleFile(ExecutionEvent, IFile, String)
	 */
	protected abstract void handleFile(IFile file, String name) throws ExecutionException;

	/**
	 * @since 3.0
	 */
	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * @since 3.0
	 */
	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	/**
	 * @since 1.1
	 */
	public IStructuredSelection getSelection() {
		return selection;
	}

	/**
	 * @since 1.1
	 */
	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

}
