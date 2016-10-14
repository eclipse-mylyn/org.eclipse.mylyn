/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
		super("Abort Build");
		setToolTipText("Abort Build");
		setImageDescriptor(BuildImages.ABORT);
		setDisabledImageDescriptor(BuildImages.ABORT_DISABLED);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.getFirstElement() instanceof IBuildPlan) {
			IBuildPlan buildPlan = (IBuildPlan) selection.getFirstElement();
			return buildPlan.getState() == BuildState.RUNNING;
		}
		return false;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuildPlan) {
			final IBuildPlan plan = (IBuildPlan) selection;
			abortBuild(plan.getLastBuild());
		}
	}

	public static void abortBuild(final IBuild build) {
		AbortBuildOperation operation = BuildsUiInternal.getFactory().getAbortBuildOperation(build);

		operation.addOperationChangeListener(new OperationChangeListener() {
			@Override
			public void done(OperationChangeEvent event) {
				if (event.getStatus().isOK()) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							BuildsUiInternal.getFactory().getRefreshOperation(build.getPlan()).execute();
						}
					});
				}
			}
		});
		operation.execute();
	}
}
