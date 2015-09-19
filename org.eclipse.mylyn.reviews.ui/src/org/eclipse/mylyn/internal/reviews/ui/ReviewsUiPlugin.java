/*********************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

	private final String INITIALIZE_TASK_REVIEW_MAPPING_STORE_JOB = "initialize task review mapping store job"; //$NON-NLS-1$

	public ReviewsUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		reviewManager = new ActiveReviewManager();

		//We need to schedule initialization otherwise TasksUiPlugin hasn't finished initialization.
		UIJob job = new UIJob(INITIALIZE_TASK_REVIEW_MAPPING_STORE_JOB) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				taskReviewsMappingStore = new TaskReviewsMappingsStore(TasksUiPlugin.getTaskDataManager(),
						TasksUiPlugin.getRepositoryManager());

				TasksUiPlugin.getTaskList().addChangeListener(taskReviewsMappingStore);
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
