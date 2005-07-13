/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Ken Sueda
 */
public class RefreshBugzillaAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.tasks.actions.refresh.bugzilla";
	
	private final TaskListView view;
	public RefreshBugzillaAction(TaskListView view) {
		this.view = view;
		setText("Bugzilla Rrefresh");
        setToolTipText("Bugzilla Refresh");
        setId(ID);
        setImageDescriptor(BugzillaImages.TASK_BUG_REFRESH);
	}
	@Override
	public void run() {
		ISelection selection = this.view.getViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		final BugzillaQueryCategory cat = (BugzillaQueryCategory) obj;
		if (obj instanceof BugzillaQueryCategory) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						public void run() {
							cat.refreshBugs();
							RefreshBugzillaAction.this.view.getViewer().refresh();
						}
					});
				}
			};
			// Use the progess service to execute the runnable
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, op);
			} catch (InvocationTargetException e) {
				// Operation was canceled
				MylarPlugin.log(e, e.getMessage());
			} catch (InterruptedException e) {
				// Handle the wrapped exception
				MylarPlugin.log(e, e.getMessage());
			}
		}
	}
}
