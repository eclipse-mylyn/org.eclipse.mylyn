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

package org.eclipse.mylyn.internal.wikitext.ui.editor.operations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;

/**
 * 
 * @author David Green
 */
public abstract class AbstractDocumentCommand {

	protected String problemText;

	public void execute(IUndoManager undoManager, IDocument document) throws CoreException {
		if (!isEnabled()) {
			throw new IllegalStateException();
		}
		try {
			undoManager.beginCompoundChange();
			try {
				doCommand(document);
			} finally {
				undoManager.endCompoundChange();
			}
		} catch (BadLocationException e) {
			throw new CoreException(WikiTextUiPlugin.getDefault().createStatus(IStatus.ERROR, e));
		}
	}

	/**
	 * indicate if the command can be exectued
	 * 
	 * @see #getProblemText()
	 */
	public boolean isEnabled() {
		return getProblemText() == null;
	}

	/**
	 * indicate the cause of any problems (why the command cannot be executed).
	 * 
	 * @see #isEnabled()
	 */
	public String getProblemText() {
		return problemText;
	}

	/**
	 * @see #getProblemText()
	 */
	protected void setProblemText(String problemText) {
		this.problemText = problemText;
	}

	protected abstract void doCommand(IDocument document) throws BadLocationException;
}
