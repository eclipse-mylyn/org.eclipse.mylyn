package org.eclipse.mylyn.reviews.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class ReviewDataManager {
	private Map<ITask, ReviewData> cached = new HashMap<ITask, ReviewData>();

	public void storeOutgoingTask(ITask task, Review review) {
		ReviewData reviewData = new ReviewData(task, review);
		reviewData.setOutgoing();
		cached.put(task, reviewData);
	}

	public ReviewData getReviewData(ITask task) {
		if (task == null)
			return null;
		return cached.get(task);
	}

	public void storeTask(ITask task, Review review) {
		ReviewData reviewData = new ReviewData(task, review);
		cached.put(task, reviewData);
	}

}
