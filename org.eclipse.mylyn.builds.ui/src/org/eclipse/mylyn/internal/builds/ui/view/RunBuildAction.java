/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Eike Stepper - improvements for bug 323781
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.operations.RunBuildOperation;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Markus Knittig
 * @author Eike Stepper
 */
public class RunBuildAction extends BaseSelectionListenerAction {

	protected RunBuildAction() {
		super("Run Build");
		setToolTipText("Run Build");
		setImageDescriptor(BuildImages.RUN);
		setDisabledImageDescriptor(BuildImages.RUN_DISABLED);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return (selection.getFirstElement() instanceof IBuildPlan);
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuildPlan) {
			final IBuildPlan plan = (IBuildPlan) selection;
			askParametersAndRunBuild(plan);
		}
	}

	public static void askParametersAndRunBuild(final IBuildPlan plan) {
		BuildPlan copy = ((BuildPlan) plan).createWorkingCopy();

		// query for parameters if necessary
		Map<String, String> parameters = null;
		if (!copy.getParameterDefinitions().isEmpty()) {
			ParametersDialog dialog = new ParametersDialog(WorkbenchUtil.getShell(), copy);
			if (dialog.open() != Window.OK) {
				return;
			}
			parameters = dialog.getParameters();
		}

		RunBuildRequest request = new RunBuildRequest(copy);
		request.setParameters(parameters);
		RunBuildOperation operation = new RunBuildOperation(request);
		BuildsUiInternal.getModel().getScheduler().schedule(operation);
	}
}
