/*********************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.internal.core.TaskReviewsMappingsStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class ReviewsUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$

	private static ReviewsUiPlugin plugin;

	private ActiveReviewManager reviewManager;

	private TaskReviewsMappingsStore taskReviewsMappingStore;

	public ReviewsUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		//We need to schedule initialization otherwise TasksUiPlugin hasn't finished initialization.
		UIJob job = new UIJob(Messages.ReviewsUiPlugin_Updating_task_review_mapping) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				reviewManager = new ActiveReviewManager();
				taskReviewsMappingStore = new TaskReviewsMappingsStore(TasksUiPlugin.getTaskList(),
						TasksUiPlugin.getRepositoryManager());
				TaskReviewsMappingsStore.setInstance(taskReviewsMappingStore);
				TasksUiPlugin.getTaskList().addChangeListener(taskReviewsMappingStore);
				Job job = new Job(Messages.ReviewsUiPlugin_Updating_task_review_mapping) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						taskReviewsMappingStore.readFromTaskList();
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.setUser(false);
				job.schedule();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	public ActiveReviewManager getReviewManager() {
		return reviewManager;
	}

	public TaskReviewsMappingsStore getTaskReviewsMappingStore() {
		return taskReviewsMappingStore;
	}
}
