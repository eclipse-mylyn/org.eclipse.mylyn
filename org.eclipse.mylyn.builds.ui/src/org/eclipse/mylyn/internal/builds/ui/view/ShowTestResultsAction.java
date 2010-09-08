/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.util.TestResultManager;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class ShowTestResultsAction extends BaseSelectionListenerAction {

	protected ShowTestResultsAction() {
		super("Show Test Results");
		setToolTipText("Show Test Results in JUnit View");
		setImageDescriptor(BuildImages.CONSOLE);
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
			TestResultManager.showInJUnitView(plan.getLastBuild());
		}
	}

}
