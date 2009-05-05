/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.icu.text.MessageFormat;

/**
 * An abstract base class for handlers that use the workbench selection to operate on resources
 * 
 * @author David Green
 * 
 */
public abstract class AbstractMarkupResourceHandler extends AbstractHandler {

	protected MarkupLanguage markupLanguage;

	private IStructuredSelection selection;

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

		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) currentSelection;
			Iterator<Object> it = structuredSelection.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				IFile file = null;
				if (o instanceof IAdaptable) {
					file = (IFile) ((IAdaptable) o).getAdapter(IFile.class);
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
								MessageDialog.openError(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										Messages.AbstractMarkupResourceHandler_unexpectedError, MessageFormat.format(
												Messages.AbstractMarkupResourceHandler_markupLanguageMappingFailed,
												new Object[] { file.getName() }));
								return null;
							}
						}

						handleFile(file, name);
					} finally {
						markupLanguage = prev;
					}
				}
			}
		}

		return null;
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

	protected abstract void handleFile(IFile file, String name) throws ExecutionException;

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

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
