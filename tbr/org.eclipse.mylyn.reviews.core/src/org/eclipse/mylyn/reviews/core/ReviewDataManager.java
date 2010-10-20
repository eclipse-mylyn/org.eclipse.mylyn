package org.eclipse.mylyn.reviews.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;

public class ReviewDataManager {
	private Map<ITask, ReviewData> cached = new HashMap<ITask, ReviewData>();
	private ReviewDataStore store;
	private ITaskDataManager taskDataManager;
	private IRepositoryModel repositoryModel;

	public ReviewDataManager(ReviewDataStore store,
			ITaskDataManager taskDataManager, IRepositoryModel repositoryModel) {
		this.store = store;
		this.taskDataManager = taskDataManager;
		this.repositoryModel = repositoryModel;

	}

	public void storeOutgoingTask(ITask task, Review review) {
		ReviewData reviewData = new ReviewData(task, review);
		reviewData.setOutgoing();
		cached.put(task, reviewData);
		store.storeReviewData(task.getRepositoryUrl(), task.getTaskId(),
				review, "review");
	}

	public ReviewData getReviewData(ITask task) {
		if (task == null)
			return null;
		ReviewData reviewData = cached.get(task);
		if (reviewData == null) {
			reviewData = loadFromDiskAddCache(task);
		}
		if (reviewData == null) {
			reviewData = loadFromTask(task);
		}
		return reviewData;
	}

	private ReviewData loadFromTask(ITask task) {
		try {
			List<Review> reviews = ReviewsUtil.getReviewAttachmentFromTask(
					taskDataManager, repositoryModel, task);
			storeTask(task, reviews.get(reviews.size() - 1));

			return getReviewData(task);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ReviewData loadFromDiskAddCache(ITask task) {
		List<Review> reviews = store.loadReviewData(task.getRepositoryUrl(),
				task.getTaskId());
		if (reviews.size() > 0) {
			Review review = reviews.get(0);
			storeTask(task, review);
			return getReviewData(task);
		}
		return null;
	}

	public void storeTask(ITask task, Review review) {
		final ReviewData reviewData = new ReviewData(task, review);
		review.eAdapters().add(new AdapterImpl() {
			public void notifyChanged(Notification notification) {
				System.err.println("notification "+notification);
				reviewData.setDirty(true);
			}

			});
		cached.put(task, reviewData);
		store.storeReviewData(task.getRepositoryUrl(), task.getTaskId(),
				review, "review");
	}

}
