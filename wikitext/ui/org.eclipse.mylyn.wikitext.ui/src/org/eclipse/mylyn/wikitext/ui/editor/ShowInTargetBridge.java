/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others.
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

package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

/**
 * A means of briding a {@link SourceViewer} with {@link IShowInTarget}.
 *
 * @author David Green
 * @since 1.1
 */
public class ShowInTargetBridge implements IShowInTarget {

	private final SourceViewer viewer;

	public ShowInTargetBridge(SourceViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public boolean show(ShowInContext context) {
		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection sse) {
			for (Object element : sse.toArray()) {
				if (element instanceof OutlineItem item) {
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
