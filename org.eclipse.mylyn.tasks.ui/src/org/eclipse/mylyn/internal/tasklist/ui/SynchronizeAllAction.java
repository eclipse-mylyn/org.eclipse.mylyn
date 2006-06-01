/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
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
		MylarTaskListPlugin.getSynchronizationManager().synchNow(0);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
