/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskSelection;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
// API-3.0: rename this class, the name conflicts with org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard
public class NewTaskWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Task";

	public NewTaskWizard(TaskSelection taskSelection) {
		super(new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, taskSelection), TITLE);
		setNeedsProgressMonitor(true);
	}

}
