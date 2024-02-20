/*******************************************************************************
 * Copyright (c) 2010,2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Maarten Meijer       - added confirmation dialog bug#340986
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class DeleteBuildElementHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object item = ((IStructuredSelection) selection).getFirstElement();
			if (item instanceof IBuildServer server) {
				boolean deleteConfirmed = MessageDialog.openQuestion(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DeleteBuildElementHandler_deleteBuildServer,
						NLS.bind(
								Messages.DeleteBuildElementHandler_areYouSure,
								server.getLabel()));

				if (deleteConfirmed) {
					List<IBuildPlan> plans = BuildsUiInternal.getModel().getPlans((IBuildServer) item);
					BuildsUiInternal.getModel().getPlans().removeAll(plans);
					BuildsUiInternal.getModel().getServers().remove(item);
				}
			}
		}
		return null;
	}

}
