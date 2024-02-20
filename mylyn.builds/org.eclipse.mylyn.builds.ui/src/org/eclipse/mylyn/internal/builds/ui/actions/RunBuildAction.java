/*******************************************************************************
 * Copyright (c) 2010, 2011 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Eike Stepper - improvements for bug 323781
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.BuildPlan;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.builds.internal.core.operations.RunBuildOperation;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.view.ParametersDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Markus Knittig
 * @author Eike Stepper
 */
public class RunBuildAction extends BaseSelectionListenerAction {

	public RunBuildAction() {
		super(Messages.RunBuildAction_runBuild);
		setToolTipText(Messages.RunBuildAction_runBuildToolTip);
		setImageDescriptor(BuildImages.RUN);
		setDisabledImageDescriptor(BuildImages.RUN_DISABLED);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.getFirstElement() instanceof IBuildPlan;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof final IBuildPlan plan) {
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
		RunBuildOperation operation = BuildsUiInternal.getFactory().getRunBuildOperation(request);
		operation.addOperationChangeListener(new OperationChangeListener() {
			@Override
			public void done(OperationChangeEvent event) {
				if (event.getStatus().isOK()) {
					Display.getDefault().asyncExec(() -> BuildsUiInternal.getFactory().getRefreshOperation(plan).execute());
				}
			}
		});
		operation.execute();
	}

}
