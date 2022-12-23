/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsModel;
import org.eclipse.swt.widgets.Event;

/**
 * @author Kilian Matt
 */
public class IncludeSubTasksAction extends Action {
	private ITaskVersionsModel model;

	public IncludeSubTasksAction(ITaskVersionsModel model) {
		super("Include subtasks",AS_CHECK_BOX);
		setImageDescriptor(TasksUiImages.TASK_NEW_SUB);
		this.model = model;
	}

	public void run() {
		model.setIncludeSubTasks(isChecked());
	}

	public void runWithEvent(Event event) {
		model.setIncludeSubTasks(isChecked());
	}

}
