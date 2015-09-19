package org.eclipse.mylyn.reviews.internal.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

/**
 * This class is used by review connectors to provide a mapping from tasks to reviews. These mappings are used by
 * TaskEditorReviewPart to give a table of the reviews pertaining to a task. The class is limited as it maps one task to
 * many reviews. It is however possible (albeit strange) to have multiple tasks for one review. This is a limitation by
 * design.
 *
 * @author Blaine Lewis
 */

@SuppressWarnings("restriction")
public class TaskReviewsMappingsStore implements ITaskListChangeListener {

	private final SetMultimap<String, String> taskReviewsMap;

	private final TaskRepositoryManager repositoryManager;

	private final TaskDataManager taskDataManager;

	public TaskReviewsMappingsStore(TaskDataManager taskDataManager, TaskRepositoryManager repositoryManager) {
		//BUG: This class is actually really volatile at the moment we have no
		//	   collision rules for if we serialize and deserialize at the same time.

		this.repositoryManager = repositoryManager;
		this.taskDataManager = taskDataManager;
		taskReviewsMap = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.<String, String> create());
	}

	/*
	 * Updates or adds a mapping given a reviewUrl and a taskDescription. If no URL is found in the description we do nothing.
	 */
	private void updateMapping(String reviewUrl, String reviewDescription) {
		String oldTaskUrl = getTaskUrl(reviewUrl);
		String newTaskUrl = this.extractTaskUrl(reviewDescription);

		if (newTaskUrl == null && oldTaskUrl != null) {
			taskReviewsMap.remove(oldTaskUrl, reviewUrl);
		} else if (newTaskUrl != null) {
			if (oldTaskUrl != null && !oldTaskUrl.equals(newTaskUrl)) {
				taskReviewsMap.remove(oldTaskUrl, reviewUrl);
			}
			if (newTaskUrl != null) {
				taskReviewsMap.put(newTaskUrl, reviewUrl);
			}
		}
	}

	public Collection<String> getReviewUrls(String taskUrl) {
		return taskReviewsMap.get(taskUrl);
	}

	/*
	 * This method of extracting URLs is deficient because if we have "(www.helloworld.com)" it will be
	 * a valid URL. This is difficult to format though so we won't handle that case.
	 */
	private String extractTaskUrl(String description) {

		for (String token : description.split("\\s+")) { //$NON-NLS-1$
			if (token.contains("://")) { //$NON-NLS-1$

				try {
					@SuppressWarnings("unused")
					URL url = new URL(token);

					AbstractRepositoryConnector connector = repositoryManager.getConnectorForRepositoryTaskUrl(token);

					if (connector != null) {
						return token;
					}
				} catch (MalformedURLException e) {
					//Do nothing, this is expected behavior when there is no URL
				}
			}
		}

		return null;
	}

	public String getTaskUrl(String reviewUrlToFind) {
		for (Entry<String, String> mapping : taskReviewsMap.entries()) {
			int index = mapping.getValue().indexOf(reviewUrlToFind);

			if (index != -1) {
				return mapping.getKey();
			}
		}

		return null;
	}

	@Override
	public void containersChanged(Set<TaskContainerDelta> containers) {

		for (TaskContainerDelta delta : containers) {
			IRepositoryElement reviewRepoElement = delta.getElement();

			if (!(reviewRepoElement instanceof ITask)) {
				return;
			}

			ITask review = (ITask) reviewRepoElement;

			AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(review.getConnectorKind());

			if (review != null && connector instanceof ReviewsConnector) {

				try {
					String reviewUrl = review.getUrl();

					switch (delta.getKind()) {
					case DELETED:
						deleteMappingsTo(reviewUrl);
						break;
					case ADDED:
					case CONTENT:
						TaskData taskData = taskDataManager.getTaskData(review);

						if (taskData == null) {
							continue;
						}

						TaskAttribute attr = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);

						if (attr == null) {
							continue;
						}
						String reviewDescription = attr.getValue();

						updateMapping(reviewUrl, reviewDescription);
						break;
					}
				} catch (CoreException e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, ReviewsCoreConstants.PLUGIN_ID, "Error getting taskData.", e)); //$NON-NLS-1$

				}
			}
		}
	}

	private void deleteMappingsTo(String reviewUrl) {
		String taskUrl = getTaskUrl(reviewUrl);
		if (taskUrl != null) {
			Set<String> reviews = taskReviewsMap.get(taskUrl);
			if (reviews != null) {
				reviews.remove(reviewUrl);
			}
		}
	}
}