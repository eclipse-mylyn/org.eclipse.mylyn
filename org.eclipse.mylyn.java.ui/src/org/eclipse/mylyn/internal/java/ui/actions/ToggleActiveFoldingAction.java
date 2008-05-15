/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Jul 29, 2004
 */
package org.eclipse.mylyn.internal.java.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 * 
 * 	TODO: move to UI
 */
public class ToggleActiveFoldingAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	private static ToggleActiveFoldingAction INSTANCE;

	private IAction parentAction = null;

	public ToggleActiveFoldingAction() {
		super();
		INSTANCE = this;
		setText("Active folding");
		setImageDescriptor(TasksUiImages.CONTEXT_FOCUS);
	}

	public static void toggleFolding(boolean on) {
		if (INSTANCE.parentAction != null) {
			INSTANCE.valueChanged(INSTANCE.parentAction, on);
		}
	}

	public void run(IAction action) {
		valueChanged(action, action.isChecked());
	}

	private void valueChanged(IAction action, final boolean on) {
		try {
			if (on) {
				JavaPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
			}
			action.setChecked(on);
			JavaUiBridgePlugin.getDefault().getPreferenceStore().setValue(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED, on);
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not enable editor management", t));
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// don't care when the active editor changes
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

	public void init(IAction action) {
		this.parentAction = action;
		valueChanged(action, JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				JavaUiBridgePlugin.AUTO_FOLDING_ENABLED));
	}

	public void dispose() {
		// don't need to do anything

	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public void init(IWorkbenchWindow window) {
	}
}
