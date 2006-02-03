/*******************************************************************************
 * Copyright (c) 2005 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugs.java;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Mik Kersten
 */
public abstract class AbstractHyperlinkDetector implements IHyperlinkDetector {

	private ITextEditor fEditor;

	public abstract IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
			boolean canShowMultipleHyperlinks);

	public ITextEditor getEditor() {
		return fEditor;
	}

	public void setEditor(ITextEditor editor) {
		this.fEditor = editor;
	}
}