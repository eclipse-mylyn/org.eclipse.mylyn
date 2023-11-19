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

package org.eclipse.mylyn.internal.wikitext.ui.editor.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author David Green
 */
public class ShowQuickOutlineCommand extends AbstractHandler {

	/**
	 * Operation code for quick outline
	 */
	public static final int QUICK_OUTLINE = 513; // magic number from PDE quick outline see PDEProjectionViewer

	@Override
	public Object execute(ExecutionEvent event) {
		Object activeFocusControl = HandlerUtil.getVariable(event, "activeFocusControl"); //$NON-NLS-1$
		if (activeFocusControl instanceof Control control) {
			if (!control.isDisposed()) {
				ISourceViewer viewer = (ISourceViewer) control.getData(ISourceViewer.class.getName());
				if (viewer != null) {
					ITextOperationTarget operationTarget = viewer.getTextOperationTarget();
					if (operationTarget.canDoOperation(QUICK_OUTLINE)) {
						operationTarget.doOperation(QUICK_OUTLINE);
					}
				}
			}
		}
		return null;
	}
}
