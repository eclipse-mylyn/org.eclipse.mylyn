/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.patch.GitPatchPathFindingStrategy;
import org.eclipse.mylyn.reviews.tasks.core.patch.ITargetPathStrategy;
import org.eclipse.mylyn.reviews.tasks.core.patch.SimplePathFindingStrategy;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslMapper;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslSerializer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;

/**
 * @author Kilian Matt
 */
public class ReviewsUtil {
	public static ITreeNode getReviewSubTasksFor(ITask task,
			ITaskDataManager taskDataManager, IReviewMapper mapper,
			IProgressMonitor monitor) throws CoreException {

		ITaskProperties taskProperties = TaskProperties.fromTaskData(
				taskDataManager, taskDataManager.getTaskData(task));
		if (ReviewsUtil.isMarkedAsReview(task)) {

			ReviewScope scope = mapper.mapTaskToScope(taskProperties);
			List<ReviewResult> results = mapper
					.mapTaskToResults(taskProperties);
			return new ReviewScopeNode(taskProperties, scope, results);
		} else {
			TaskNode current = new TaskNode(taskProperties);
			if (task instanceof ITaskContainer) {
				ITaskContainer taskContainer = (ITaskContainer) task;
				for (ITask subTask : taskContainer.getChildren()) {
					current.addChildren(getReviewSubTasksFor(subTask,
							taskDataManager, mapper, monitor));
				}
			}
			return current;
		}
	}

	private static List<ITargetPathStrategy> strategies;
	static {
		strategies = new ArrayList<ITargetPathStrategy>();
		strategies.add(new SimplePathFindingStrategy());
		strategies.add(new GitPatchPathFindingStrategy());
	}

	public static List<? extends ITargetPathStrategy> getPathFindingStrategies() {
		return strategies;
	}

	public static boolean isMarkedAsReview(ITask task) {
		return task.getSummary().startsWith(ReviewConstants.REVIEW_MARKER);
	}

	public static boolean hasReviewMarker(ITask task) {
		return task.getAttribute(ReviewConstants.ATTR_REVIEW_FLAG) != null;
	}

	public static Attachment findAttachment(String filename, String author,
			String createdDate, ITaskProperties task) {
		for (Attachment att : task.getAttachments()) {
			if (filename.equals(att.getFileName())) {
				return att;
			}

		}
		return null;
	}

	public static IReviewMapper createMapper() {
		return new ReviewTaskMapper(new ReviewDslMapper(),
				new ReviewDslSerializer());
	}

}
