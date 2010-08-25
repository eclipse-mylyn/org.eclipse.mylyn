/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class OpenBuildHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			throw new ExecutionException("No active workbench window"); //$NON-NLS-1$
		}

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object item = ((IStructuredSelection) selection).getFirstElement();
			if (item instanceof IBuildPlan) {
				IBuildPlan plan = (IBuildPlan) item;
				try {
					BuildEditorInput input = new BuildEditorInput(plan);
					page.openEditor(input, BuildsUiConstants.ID_EDITOR_BUILDS);
				} catch (PartInitException e) {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
							"Unexpected error while opening build", e)); //$NON-NLS-1$
				}
			}
		}

		return null;
	}

}
