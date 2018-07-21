/*******************************************************************************
 * Copyright (c) 2007, 2011 Tasktop Technologies Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.texteditor;

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
 * @since 3.7
 */
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
