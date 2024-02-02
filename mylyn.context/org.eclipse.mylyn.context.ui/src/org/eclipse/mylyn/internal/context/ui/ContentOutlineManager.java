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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.mylyn.internal.context.ui.actions.FocusOutlineAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class ContentOutlineManager implements IPartListener {

	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {
		// use the display async due to bug 261977: [context] outline view does not filter contents when new editor is opened
		Display.getDefault().asyncExec(() -> {
			if (part instanceof IEditorPart editorPart) {
				FocusOutlineAction applyAction = FocusOutlineAction.getOutlineActionForEditor(editorPart);
				if (applyAction != null) {
					applyAction.update(editorPart);
				}
			}
		});
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// ignore
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// ignore
	}

	@Override
	public void partClosed(IWorkbenchPart partRef) {
		// ignore
	}

	@Override
	public void partDeactivated(IWorkbenchPart partRef) {
		// ignore
	}
}
