/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

/**
 * A means of briding a {@link SourceViewer} with {@link IShowInTarget}.
 * 
 * @author David Green
 * 
 * @since 1.1
 */
public class ShowInTargetBridge implements IShowInTarget {

	private final SourceViewer viewer;

	public ShowInTargetBridge(SourceViewer viewer) {
		this.viewer = viewer;
	}

	public boolean show(ShowInContext context) {
		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toArray()) {
				if (element instanceof OutlineItem) {
					OutlineItem item = (OutlineItem) element;
					viewer.setSelection(new TextSelection(item.getOffset(), item.getLength()), true);
					return true;
				}
			}
		} else if (selection instanceof ITextSelection) {
			viewer.setSelection(selection, true);
			return true;
		}
		return false;
	}

}
