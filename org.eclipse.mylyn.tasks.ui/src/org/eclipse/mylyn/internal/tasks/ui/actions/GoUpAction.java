/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

public class GoUpAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.view.go.up";

	public GoUpAction(DrillDownAdapter drillDownAdapter) {
		setText("Go Up To Root");
		setToolTipText("Go Up To Root");
		setId(ID);
		setImageDescriptor(CommonImages.GO_UP);
	}

	@Override
	public void run() {
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().goUpToRoot();
		}
	}
}
