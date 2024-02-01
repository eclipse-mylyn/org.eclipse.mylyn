/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Mik Kersten
 */
@Deprecated
public abstract class AbstractEditorHyperlinkDetector implements IHyperlinkDetector {

	private ITextEditor editor;

	@Override
	public abstract IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
			boolean canShowMultipleHyperlinks);

	public ITextEditor getEditor() {
		return editor;
	}

	public void setEditor(ITextEditor editor) {
		this.editor = editor;
	}
}
