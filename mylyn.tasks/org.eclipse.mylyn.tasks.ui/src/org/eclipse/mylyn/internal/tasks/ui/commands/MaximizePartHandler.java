/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author
 */
public class MaximizePartHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site instanceof IEditorSite) {
			IWorkbenchPart part = ((IEditorSite) site).getPart();
			if (part instanceof FormEditor) {
				IFormPage page = ((FormEditor) part).getActivePageInstance();
				Control focusedControl = EditorUtil.getFocusControl(page);
				if (focusedControl != null) {
					Object data = focusedControl.getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION);
					if (data instanceof IAction action) {
						action.setChecked(!action.isChecked());
						action.run();
					}
				}
			}
		}

		return null;
	}
}
