/*******************************************************************************
 * Copyright (c) 2007, 2010 Tasktop Technologies Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.TextViewerDeleteLineTarget;

/**
 * Abstract command handler that uses {@link TextViewerDeleteLineTarget}. Subclasses can specify the type of delete line
 * and copyToClipboard.
 * 
 * @author David Green
 * @deprecated use {@link org.eclipse.mylyn.commons.ui.texteditor.AbstractDeleteLineHandler} instead
 */
@Deprecated
public class AbstractDeleteLineHandler extends AbstractTextViewerHandler implements IHandler {
	protected final int type;

	protected final boolean copyToClipboard;

	protected AbstractDeleteLineHandler(int type, boolean copyToClipboard) {
		this.type = type;
		this.copyToClipboard = copyToClipboard;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextViewer viewer = getTextViewer(event);
		if (viewer != null) {
			TextViewerDeleteLineTarget target = new TextViewerDeleteLineTarget(viewer);

			try {
				ITextSelection textSelection = (ITextSelection) viewer.getSelectionProvider().getSelection();

				target.deleteLine(viewer.getDocument(), textSelection, type, copyToClipboard);
			} catch (BadLocationException e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		return null;
	}

}
