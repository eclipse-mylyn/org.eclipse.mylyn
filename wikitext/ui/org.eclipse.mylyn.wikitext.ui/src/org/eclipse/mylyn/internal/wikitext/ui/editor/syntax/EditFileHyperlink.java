/*******************************************************************************
 * Copyright (c) 2010, 2021 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class EditFileHyperlink implements IHyperlink {

	private final IFile file;

	private final IRegion region;

	protected EditFileHyperlink(IFile file, IRegion region) {
		this.file = file;
		this.region = region;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return NLS.bind(Messages.MarkupHyperlinkDetector_openFileInEditor, file.getName());
	}

	@Override
	public void open() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		try {
			IDE.openEditor(activePage, file);
		} catch (PartInitException e) {
			WikiTextUiPlugin.getDefault().log(e);
			MessageDialog.openError(window.getShell(), Messages.MarkupHyperlinkDetector_unexpectedError,
					NLS.bind(Messages.MarkupHyperlinkDetector_openException, file.getName(), e.getMessage()));
		}
	}
}
