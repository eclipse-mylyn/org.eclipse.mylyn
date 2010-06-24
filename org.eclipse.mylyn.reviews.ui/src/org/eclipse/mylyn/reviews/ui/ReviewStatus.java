package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITask;

public class ReviewStatus extends Status {
	private ITask task;

	public ReviewStatus(String message, ITask task) {
		super(IStatus.OK, ReviewsUiPlugin.PLUGIN_ID, message);
		this.task = task;
	}

	public ITask getTask() {
		return task;
	}

}