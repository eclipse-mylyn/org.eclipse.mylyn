/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.operations.AbortBuildOperation;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public class AbortBuildAction extends BaseSelectionListenerAction {

	public AbortBuildAction() {
		super(Messages.AbortBuildAction_abortBuild);
		setToolTipText(Messages.AbortBuildAction_abortBuild);
		setImageDescriptor(BuildImages.ABORT);
		setDisabledImageDescriptor(BuildImages.ABORT_DISABLED);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IBuild build) {
			return build.getState() == BuildState.RUNNING;
		} else if (element instanceof IBuildPlan buildPlan) {
			return buildPlan.getState() == BuildState.RUNNING;
		}
		return false;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuild build) {
			abortBuild(build);
		} else if (selection instanceof IBuildPlan buildPlan) {
			abortBuild(buildPlan.getLastBuild());
		}
	}

	public static void abortBuild(final IBuild build) {
		AbortBuildOperation operation = BuildsUiInternal.getFactory().getAbortBuildOperation(build);

		operation.addOperationChangeListener(new OperationChangeListener() {
			@Override
			public void done(OperationChangeEvent event) {
				if (event.getStatus().isOK()) {
					Display.getDefault().asyncExec(() -> BuildsUiInternal.getFactory().getRefreshOperation(build.getPlan()).execute());
				}
			}
		});
		operation.execute();
	}
}
