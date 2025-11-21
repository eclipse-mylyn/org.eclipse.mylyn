/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 * 	This class implements the implementation to adjust the selection of the My starred handler.
 * in the review table view
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the selection of My starred handler
 *   Jacques Bouthillier - Bug 426580 Add the starred functionality
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.table;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jacques Bouthillier
 */
public class AdjustMyStarredHandler extends AbstractHandler {

	private GerritTask item = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		final GerritTableView reviewTableView = GerritTableView.getActiveView(true);
		final TableViewer viewer = reviewTableView.getTableViewer();
		final ISelection tableSelection = viewer.getSelection();

		if (!isEnabled()) {
			return null;
		}

		final Job job = new Job(Messages.AdjustMyStarredHandler_commandMessage) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (tableSelection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) tableSelection).getFirstElement();
					if (obj instanceof GerritTask) {
						item = (GerritTask) obj;

						try {
							// Update the Gerrit Server
							reviewTableView.setStarred(item.getTaskId(),
									!Boolean.valueOf(item.getAttribute(GerritTask.IS_STARRED)), monitor);

							// Toggle the STARRED value for the Dashboard
							item.setAttribute(GerritTask.IS_STARRED,
									Boolean.toString(!Boolean.valueOf(item.getAttribute(GerritTask.IS_STARRED))));
						} catch (CoreException e) {
							UIUtils.showErrorDialog(e.getMessage(), e.getStatus().getException().getMessage());
						}

						Display.getDefault().asyncExec(() -> viewer.update(item, null));
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();

		return null;
	}

	@Override
	public boolean isEnabled() {
		final GerritTableView reviewTableView = GerritTableView.getActiveView(false);
		if (reviewTableView == null) {
			return false;
		}
		final TableViewer viewer = reviewTableView.getTableViewer();
		if (viewer.getSelection().isEmpty()) {
			return false;
		}

		return true;
	}
}
