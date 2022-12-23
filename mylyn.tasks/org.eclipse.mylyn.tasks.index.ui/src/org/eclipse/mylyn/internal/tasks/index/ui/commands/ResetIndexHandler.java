/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.ui.IndexReference;

/**
 * @author David Green
 */
public class ResetIndexHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job(Messages.ResetIndexHandler_Refresh_Index_Job_Name) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IndexReference reference = new IndexReference();
				try {
					monitor.beginTask(Messages.ResetIndexHandler_Rebuilding_Index_Progress_Label,
							IProgressMonitor.UNKNOWN);
					final TaskListIndex index = reference.index();
					index.reindex();
					// wait for the reindex job to complete before we dispose of the reference
					index.waitUntilIdle();
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				} finally {
					reference.dispose();
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setUser(false);
		job.setPriority(Job.LONG);
		job.schedule();
		return null;
	}

}
