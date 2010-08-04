package org.eclipse.mylyn.reviews.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.ITask;

public class ReviewDataManager {
	private Map<ITask, ReviewData> cached = new HashMap<ITask, ReviewData>();
	private ReviewDataStore store;
	
	public ReviewDataManager(ReviewDataStore store) {
		this.store = store;
	}

	public void storeOutgoingTask(ITask task, Review review) {
		ReviewData reviewData = new ReviewData(task, review);
		reviewData.setOutgoing();
		cached.put(task, reviewData);
		store.storeReviewData(task.getRepositoryUrl(), task.getTaskId(), review, "review");
	}

	public ReviewData getReviewData(ITask task) {
		if (task == null)
			return null;
		ReviewData reviewData = cached.get(task);
		if(reviewData == null) {
			reviewData = loadFromDiskAddCache(task);
		}
		return reviewData;
	}

	private ReviewData loadFromDiskAddCache(ITask task) {
		Review review = store.loadReviewData(task.getRepositoryUrl(), task.getTaskId()).get(0);
		
		return new ReviewData(task, review);
	}

	public void storeTask(ITask task, Review review) {
		ReviewData reviewData = new ReviewData(task, review);
		cached.put(task, reviewData);
		store.storeReviewData(task.getRepositoryUrl(), task.getTaskId(), review, "review");
	}

}
