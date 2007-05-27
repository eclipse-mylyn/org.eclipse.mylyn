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

package org.eclipse.mylar.internal.context.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IInteractionContext;
import org.eclipse.mylar.context.core.IInteractionContextListener;
import org.eclipse.mylar.context.core.IInteractionElement;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * This action is not persistent, in order to avoid Mylar not working on
 * startup.
 * 
 * @author Mik Kersten
 */
public class ContextCapturePauseAction extends Action implements IViewActionDelegate, IInteractionContextListener {
	
	protected IAction initAction = null;
	
	public void init(IViewPart view) {
		// NOTE: not disposed until shutdown
		ContextCorePlugin.getContextManager().addListener(this);
	}

	public void run(IAction action) {
		initAction = action;
		setChecked(!action.isChecked());
		if (isChecked()) {
			resume();
		} else {
			pause();
		}
	}

	public void pause() {
		ContextCorePlugin.getContextManager().setContextCapturePaused(true);
		TaskListView.getFromActivePerspective().indicatePaused(true);
	}

	public void resume() {
		ContextCorePlugin.getContextManager().setContextCapturePaused(false);
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().indicatePaused(false);
		}
	}

	public void contextActivated(IInteractionContext context) {
		resume();
		setChecked(false);
		if (initAction != null) {
			initAction.setChecked(false);
		}
	}

	public void contextCleared(IInteractionContext context) {
		// ignore
	}
	
	public void contextDeactivated(IInteractionContext context) {
		// ignore
	}

	public void relationsChanged(IInteractionElement element) {
		// ignore
	}

	public void interestChanged(List<IInteractionElement> elements) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void elementDeleted(IInteractionElement element) {
		// ignore
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
