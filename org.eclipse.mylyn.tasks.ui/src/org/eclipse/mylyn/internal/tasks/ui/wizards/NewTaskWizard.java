/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.tasks.core.TaskSelection;

/**
 * API-3.0: rename this class, the name conflicts with org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class NewTaskWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Task";

	private static NewTaskPage page;

	public NewTaskWizard(TaskSelection taskSelection) {
		super(page = new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, taskSelection), TITLE);
		setNeedsProgressMonitor(true);
	}

	// API-3.0: consider removing this method
	public NewTaskWizard() {
		this(null);
	}

	// API-3.0: remove legacy support
	public boolean supportsTaskSelection() {
		return page.supportsTaskSelection();
	}

}
