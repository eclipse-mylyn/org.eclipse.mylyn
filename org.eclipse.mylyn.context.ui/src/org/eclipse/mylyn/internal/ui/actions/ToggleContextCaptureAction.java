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

package org.eclipse.mylar.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * This action is not persistent, in order to avoid Mylar not working on
 * startup.
 * 
 * @author Mik Kersten
 */
public class ToggleContextCaptureAction extends Action implements IViewActionDelegate {
	
	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		setChecked(!action.isChecked());
		if (isChecked()) {
			resume();
		} else {
			pause();
		}
		// super.setChecked(!super.isChecked());
	}

	public void pause() {
		MylarPlugin.getContextManager().setContextCapturePaused(true);
		TaskListView.getFromActivePerspective().indicatePaused(true);
	}

	public void resume() {
		MylarPlugin.getContextManager().setContextCapturePaused(false);
		TaskListView.getFromActivePerspective().indicatePaused(false);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
