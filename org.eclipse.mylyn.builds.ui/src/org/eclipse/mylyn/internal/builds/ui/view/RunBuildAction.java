/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
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
import org.eclipse.mylyn.internal.builds.core.operations.RunBuildOperation;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Markus Knittig
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
			RunBuildOperation operation = new RunBuildOperation(plan);
			operation.schedule();
		}
	}

}
