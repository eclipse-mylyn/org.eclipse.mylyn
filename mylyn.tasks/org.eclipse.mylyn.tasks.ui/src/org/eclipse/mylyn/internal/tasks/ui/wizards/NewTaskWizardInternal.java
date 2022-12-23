/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.ui.INewWizard;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class NewTaskWizardInternal extends MultiRepositoryAwareWizard implements INewWizard {

	public NewTaskWizardInternal(ITaskMapping taskSelection) {
		super(new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, taskSelection),
				Messages.NewTaskWizard_New_Task);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Constructs a new task wizard with an empty selection. This constructor is used by the
	 * <code>org.eclipse.ui.newWizards</code> extension.
	 */
	public NewTaskWizardInternal() {
		this(null);
	}

}
