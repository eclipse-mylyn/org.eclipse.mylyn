/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class NewTaskAction extends Action implements IViewActionDelegate, IExecutableExtension {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create"; //$NON-NLS-1$

	private boolean skipRepositoryPage = false;

	private boolean localTask = false;

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			if (localTask) {
				TasksUiUtil.openNewLocalTaskEditor(shell, null);
			} else {
				if (skipRepositoryPage) {
					TasksUiUtil.openNewTaskEditor(shell, null, TasksUiUtil.getSelectedRepository());
				} else {
					TasksUiUtil.openNewTaskEditor(shell, null, null);
				}
			}
		}
	}

	public void run(IAction action) {
		run();
	}

	public void init(IViewPart view) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if ("skipFirstPage".equals(data)) { //$NON-NLS-1$
			this.skipRepositoryPage = true;
		}
		if ("local".equals(data)) { //$NON-NLS-1$
			this.localTask = true;
		}
	}

}
