/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * @author Mik Kersten
 */
public class GoUpAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.view.go.up"; //$NON-NLS-1$

	public GoUpAction(DrillDownAdapter drillDownAdapter) {
		setText(Messages.GoUpAction_Go_Up_To_Root);
		setToolTipText(Messages.GoUpAction_Go_Up_To_Root);
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
