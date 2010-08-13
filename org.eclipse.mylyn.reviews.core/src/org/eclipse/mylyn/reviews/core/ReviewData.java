package org.eclipse.mylyn.reviews.core;

import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.ITask;

public class ReviewData {
	private boolean outgoing;
	private Review review;
	private ITask task;
	private boolean dirty;

	public ReviewData(ITask task, Review review) {
		this.task=task;
		this.review = review;
	}

	public boolean isOutgoing() {
		return outgoing;
	}

	public void setOutgoing() {
		this.outgoing = true;
	}

	public void setOutgoing(boolean outgoing) {
		this.outgoing = outgoing;
	}

	public ITask getTask() {
		return task;
	}

	public Review getReview() {
		return review;
	}

	public void setDirty(boolean isDirty) {
		this.dirty=isDirty;
	}

	public void setDirty() {
		setDirty(true);
	}
	public boolean isDirty() {
		return this.dirty;
	}
	
}
