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

package org.eclipse.mylyn.internal.wikitext.ui.editor.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 * @author David Green
 */
public class ShowQuickOutlineCommand extends AbstractHandler {

	/**
	 * Operation code for quick outline
	 */
	public static final int QUICK_OUTLINE = 513; // magic number from PDE quick outline see PDEProjectionViewer

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object activeFocusControl = HandlerUtil.getVariable(event, "activeFocusControl"); //$NON-NLS-1$
		if (activeFocusControl instanceof Control) {
			Control control = (Control) activeFocusControl;
			ISourceViewer viewer = (ISourceViewer) control.getData(ISourceViewer.class.getName());
			if (viewer != null) {
				ITextOperationTarget operationTarget = viewer.getTextOperationTarget();
				if (operationTarget.canDoOperation(QUICK_OUTLINE)) {
					operationTarget.doOperation(QUICK_OUTLINE);
				}
			}
		}
		return null;
	}
}
