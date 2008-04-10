/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class SynchronizeAllAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		TasksUi.synchronizeAlllRepositories(false);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
