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
package org.eclipse.mylyn.wikitext.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.ui.PlatformUI;

/**
 * An abstract base class for handlers that use the workbench selection to operate on resources
 * 
 * @author David Green
 * 
 */
public abstract class AbstractMarkupResourceHandler extends AbstractHandler {

	protected MarkupLanguage markupLanguage;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection currentSelection = null;

		try {
			currentSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		} catch (Exception e) {
			// ignore
		}

		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) currentSelection;

			Object o = structuredSelection.getFirstElement();
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

				if (markupLanguage == null) {
					markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguageForFilename(file.getName());
					if (markupLanguage == null) {
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Unexpected Error", String.format("Cannot guess markup language for file '%s'",
										file.getName()));
						return null;
					}
				}

				handleFile(file, name);
			}
		}

		return null;
	}

	protected abstract void handleFile(IFile file, String name);

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

}
