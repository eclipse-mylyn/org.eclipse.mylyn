/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public void partBroughtToTop(final IWorkbenchPart part) {
		// use the display async due to bug 261977: [context] outline view does not filter contents when new editor is opened
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (part instanceof IEditorPart) {
					IEditorPart editorPart = (IEditorPart) part;
					FocusOutlineAction applyAction = FocusOutlineAction.getOutlineActionForEditor(editorPart);
					if (applyAction != null) {
						applyAction.update(editorPart);
					}
				}
			}
		});
	}

	public void partActivated(IWorkbenchPart part) {
		// ignore
	}

	public void partOpened(IWorkbenchPart part) {
		// ignore
	}

	public void partClosed(IWorkbenchPart partRef) {
		// ignore
	}

	public void partDeactivated(IWorkbenchPart partRef) {
		// ignore
	}
}
