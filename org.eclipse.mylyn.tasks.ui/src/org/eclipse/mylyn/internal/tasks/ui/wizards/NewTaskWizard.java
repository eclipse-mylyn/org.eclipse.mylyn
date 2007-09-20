/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class NewTaskWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Task";

	public NewTaskWizard() {
		super(new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK), TITLE);
		setNeedsProgressMonitor(true);
	}

}
