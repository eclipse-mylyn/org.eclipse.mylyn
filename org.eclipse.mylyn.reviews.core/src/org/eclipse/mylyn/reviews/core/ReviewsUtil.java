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
package org.eclipse.mylyn.reviews.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Kilian Matt
 */
public class ReviewsUtil {

	public static List<ReviewSubTask> getReviewSubTasksFor(
			ITaskContainer taskContainer, ITaskDataManager taskDataManager,
			IRepositoryModel repositoryModel, IProgressMonitor monitor) {
		List<ReviewSubTask> resultList = new ArrayList<ReviewSubTask>();
		try {
			for (ITask subTask : taskContainer.getChildren()) {
				
				if (ReviewsUtil.isMarkedAsReview(subTask)) {//.getSummary().startsWith("Review")) { //$NON-NLS-1$
					// change to review data manager
					for (Review review : getReviewAttachmentFromTask(
							taskDataManager, repositoryModel, subTask)) {
						// TODO change to latest etc
						if(review.getResult()!=null)
							resultList.add(new ReviewSubTask(getPatchFile(review
									.getScope()), getPatchCreationDate(review
									.getScope()),
									getAuthorString(review.getScope()), subTask
											.getOwner(), review.getResult()
											.getRating(), review.getResult()
											.getText(), subTask));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;

	}

	private static String getPatchFile(EList<ScopeItem> scope) {
		if (scope.size() == 1 && scope.get(0) instanceof Patch) {
			return ((Patch) scope.get(0)).getFileName();
		} else {
			return "";
		}
	}

	private static Date getPatchCreationDate(EList<ScopeItem> scope) {
		if (scope.size() == 1 && scope.get(0) instanceof Patch) {
			return ((Patch) scope.get(0)).getCreationDate();
		} else {
			return null;
		}
	}

	private static String getAuthorString(EList<ScopeItem> scope) {
		if (scope.size() == 0) {
			return "none";
		} else if (scope.size() == 1) {
			return scope.get(0).getAuthor();
		} else if (scope.size() < 3) {
			StringBuilder sb = new StringBuilder();
			for (ScopeItem item : scope) {
				sb.append(item.getAuthor());
				sb.append(", ");
			}
			return sb.substring(0, sb.length() - 2);
		} else {
			return "Multiple Authors";
		}
	}

	static List<Review> parseAttachments(TaskAttribute attribute,
			IProgressMonitor monitor) {
		List<Review> reviewList = new ArrayList<Review>();
		try {
			URL url = new URL(attribute.getMappedAttribute(
					TaskAttribute.ATTACHMENT_URL).getValue());

			ZipInputStream stream = new ZipInputStream(url.openStream());
			while (!stream.getNextEntry().getName().equals(
					ReviewConstants.REVIEW_DATA_FILE)) {
			}

			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(ReviewPackage.eNS_URI,
					ReviewPackage.eINSTANCE);
			Resource resource = resourceSet.createResource(URI.createURI(""));
			resource.load(stream, null);
			for (EObject item : resource.getContents()) {
				if (item instanceof Review) {
					Review review = (Review) item;
					reviewList.add(review);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reviewList;
	}

	public static List<Review> getReviewAttachmentFromTask(
			ITaskDataManager taskDataManager, IRepositoryModel repositoryModel,
			ITask task) throws CoreException {

		List<Review> reviews = new ArrayList<Review>();
		TaskData taskData = taskDataManager.getTaskData(task);
		if (taskData != null) {
			List<TaskAttribute> attributesByType = taskData
					.getAttributeMapper().getAttributesByType(taskData,
							TaskAttribute.TYPE_ATTACHMENT);
			for (TaskAttribute attribute : attributesByType) {
				// TODO move RepositoryModel.createTaskAttachment to interface?
				ITaskAttachment taskAttachment = ((RepositoryModel) repositoryModel)
						.createTaskAttachment(attribute);
				if (taskAttachment!=null&&taskAttachment.getFileName().equals(
						ReviewConstants.REVIEW_DATA_CONTAINER)) {
					reviews.addAll(parseAttachments(attribute,
							new NullProgressMonitor()));
				}
			}

		}
		return reviews;
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
		boolean isReview = Boolean.parseBoolean(task
				.getAttribute(ReviewConstants.ATTR_REVIEW_FLAG));
		return isReview;
	}

	public static void markAsReview(ITask task) {
		task.setAttribute(ReviewConstants.ATTR_REVIEW_FLAG, Boolean.TRUE.toString());
	}
}
