/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Gregory Amerson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class ToggleFocusActiveViewHandler extends AbstractHandler {

	private AbstractFocusViewAction focusAction;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object returnValue = false;

		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IWorkbenchPart activePart = activePage.getActivePart();

		if (activePart instanceof final IViewPart viewPart) {
			focusAction = AbstractFocusViewAction.getActionForPart(viewPart);

			if (focusAction != null) {
				focusAction.update(!focusAction.isChecked());
				returnValue = true;
			}
		}

		return returnValue;
	}
}
