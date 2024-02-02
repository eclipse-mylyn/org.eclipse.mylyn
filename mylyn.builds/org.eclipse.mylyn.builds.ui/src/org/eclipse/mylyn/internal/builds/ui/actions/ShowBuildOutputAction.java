/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Techonologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class ShowBuildOutputAction extends BaseSelectionListenerAction {

	public ShowBuildOutputAction() {
		super("Show Output");
		setToolTipText("Show Build Output in Console");
		setImageDescriptor(BuildImages.CONSOLE);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.getFirstElement() instanceof IBuildPlan || selection.getFirstElement() instanceof IBuild;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuildPlan) {
			BuildsUiInternal.getConsoleManager().showConsole((IBuildPlan) selection);
		} else if (selection instanceof IBuild) {
			BuildsUiInternal.getConsoleManager().showConsole((IBuild) selection);
		}
	}

}
