/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.util.TestResultManager;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class ShowTestResultsAction extends BaseSelectionListenerAction {

	public ShowTestResultsAction() {
		super(Messages.ShowTestResultsAction_showTestResults);
		setToolTipText(Messages.ShowTestResultsAction_showTestResultsInJUnitView);
		setImageDescriptor(BuildImages.JUNIT);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!TestResultManager.isJUnitAvailable()) {
			return false;
		}
		if (selection.getFirstElement() instanceof IBuildPlan) {
			return isEnabled(((IBuildPlan) selection.getFirstElement()).getLastBuild());
		} else if (selection.getFirstElement() instanceof IBuild) {
			return isEnabled((IBuild) selection.getFirstElement());
		}
		return false;
	}

	private boolean isEnabled(IBuild build) {
		return build != null && build.getTestResult() != null;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuildPlan plan) {
			TestResultManager.showInJUnitView(plan.getLastBuild());
		} else if (selection instanceof IBuild build) {
			TestResultManager.showInJUnitView(build);
		}
	}

}
