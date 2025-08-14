/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Izzet Safer - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Izzet Safer
 */
public class FocusViewHandler extends AbstractHandler {

	private static final String PARAMETER_VIEW_ID = "viewId"; //$NON-NLS-1$

	private AbstractFocusViewAction applyAction = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object returnValue = Boolean.FALSE;
		String viewId = event.getParameter(PARAMETER_VIEW_ID);

		if (viewId == null) {
			return returnValue;
		}

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = activePage.findView(viewId);
		if (viewPart == null) {
			return returnValue;
		}

		applyAction = AbstractFocusViewAction.getActionForPart(viewPart);

		if (applyAction != null) {
			applyAction.update(true);
			returnValue = Boolean.TRUE;
		}

		return returnValue;
	}
}
