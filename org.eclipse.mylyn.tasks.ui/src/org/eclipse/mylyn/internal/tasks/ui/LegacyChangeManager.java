/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotification;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;

/**
 * @author Steffen Pingel
 */
@Deprecated
public class LegacyChangeManager {

	static class Change {

		final List<String> added;

		final String field;

		final List<String> removed = new ArrayList<String>();

		public Change(String field, List<String> newValues) {
			this.field = field;
			if (newValues != null) {
				this.added = new ArrayList<String>(newValues);
			} else {
				this.added = new ArrayList<String>();
			}
		}
	}

	private static final int MAX_CHANGED_ATTRIBUTES = 2;

	static String cleanValue(String value) {
		if (value == null) {
			return "";
		}
		String commentText = value.replaceAll("\\s", " ").trim();
		if (commentText.length() > 60) {
			commentText = commentText.substring(0, 55) + "...";
		}
		return commentText;
	}

	static String cleanValues(List<String> values) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String value : values) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(cleanValue(value));
			first = false;
		}
		return sb.toString();
	}

	private static String getChangedAttributes(RepositoryTaskData newTaskData, RepositoryTaskData oldTaskData) {
		List<Change> changes = new ArrayList<Change>();
		for (RepositoryTaskAttribute newAttribute : newTaskData.getAttributes()) {
			if (ignoreAttribute(newTaskData, newAttribute)) {
				continue;
			}

			List<String> newValues = newAttribute.getValues();
			if (newValues != null) {
				RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(newAttribute.getId());
				if (oldAttribute == null) {
					changes.add(getDiff(newTaskData, newAttribute, null, newValues));
				}
				if (oldAttribute != null) {
					List<String> oldValues = oldAttribute.getValues();
					if (!oldValues.equals(newValues)) {
						changes.add(getDiff(newTaskData, newAttribute, oldValues, newValues));
					}
				}
			}
		}

		for (RepositoryTaskAttribute oldAttribute : oldTaskData.getAttributes()) {
			if (ignoreAttribute(oldTaskData, oldAttribute)) {
				continue;
			}

			RepositoryTaskAttribute attribute = newTaskData.getAttribute(oldAttribute.getId());
			List<String> values = oldAttribute.getValues();
			if (attribute == null && values != null && !values.isEmpty()) {
				changes.add(getDiff(oldTaskData, oldAttribute, values, null));
			}
		}

		if (changes.isEmpty()) {
			return "";
		}

		String details = "";
		String sep = "";
		int n = 0;
		for (Change change : changes) {
			String removed = cleanValues(change.removed);
			String added = cleanValues(change.added);
			details += sep + "  " + change.field + " " + removed;
			if (removed.length() > 30) {
				//				details += "\n  ";
				details += "\n  ";
			}
			details += " -> " + added;
			sep = "\n";

			if (++n == MAX_CHANGED_ATTRIBUTES) {
				break;
			}
		}
		//		if (!details.equals("")) {
		//			return details;
		//			return "Attributes Changed:\n" + details;
		//		}
		return details;
	}

	private static String getChangedDescription(RepositoryTaskData newTaskData, RepositoryTaskData oldTaskData) {
		String descriptionText = "";

		if (newTaskData.getComments().size() > oldTaskData.getComments().size()) {
			List<TaskComment> taskComments = newTaskData.getComments();
			if (taskComments != null && taskComments.size() > 0) {
				TaskComment lastComment = taskComments.get(taskComments.size() - 1);
				if (lastComment != null) {
					//					descriptionText += "Comment by " + lastComment.getAuthor() + ":\n  ";
					descriptionText += lastComment.getAuthor() + ":  ";
					descriptionText += cleanValue(lastComment.getText());
				}
			}
		}

		return descriptionText;
	}

	static Change getDiff(RepositoryTaskData taskData, RepositoryTaskAttribute attribute, List<String> oldValues,
			List<String> newValues) {
		//		AbstractAttributeFactory factory = taskData.getAttributeFactory();
		//		if (attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_MODIFIED)) 
		//			|| attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_CREATION))) {
		//			if (newValues != null && newValues.size() > 0) {
		//				for (int i = 0; i < newValues.size(); i++) {
		//					newValues.set(i, factory.getDateForAttributeType(attribute.getId(), newValues.get(i)).toString());
		//				}
		//			}
		//			
		//			Change change = new Change(attribute.getName(), newValues);
		//			if (oldValues != null) {
		//				for (String value : oldValues) {
		//					value = factory.getDateForAttributeType(attribute.getId(), value).toString();
		//					if (change.added.contains(value)) {
		//						change.added.remove(value);
		//					} else {
		//						change.removed.add(value);
		//					}
		//				}
		//			}
		//			return change;		
		//		}

		Change change = new Change(attribute.getName(), newValues);
		if (oldValues != null) {
			for (String value : oldValues) {
				if (change.added.contains(value)) {
					change.added.remove(value);
				} else {
					change.removed.add(value);
				}
			}
		}
		return change;
	}

	/**
	 * TODO 3.0: move, uses and exposes internal class.
	 */
	@SuppressWarnings("restriction")
	@Deprecated
	public static TaskListNotification getIncommingNotification(AbstractRepositoryConnector connector, ITask task) {
		TaskListNotification notification = new TaskListNotification(task);
		RepositoryTaskData newTaskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
				task.getRepositoryUrl(), task.getTaskId());
		RepositoryTaskData oldTaskData = TasksUiPlugin.getTaskDataStorageManager().getOldTaskData(
				task.getRepositoryUrl(), task.getTaskId());
		try {
			if (task.getSynchronizationState().equals(SynchronizationState.INCOMING)
					&& task.getLastReadTimeStamp() == null) {
				notification.setDescription("New unread task ");
			} else if (newTaskData != null && oldTaskData != null) {
				StringBuilder description = new StringBuilder();
				String changedDescription = getChangedDescription(newTaskData, oldTaskData);
				String changedAttributes = getChangedAttributes(newTaskData, oldTaskData);
				if (!"".equals(changedDescription.trim())) {
					description.append(changedDescription);
					if (!"".equals(changedAttributes)) {
						description.append('\n');
					}
				}
				if (!"".equals(changedAttributes)) {
					description.append(changedAttributes);
				}

				notification.setDescription(description.toString());

				if (connector instanceof AbstractLegacyRepositoryConnector) {
					AbstractTaskDataHandler offlineHandler = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler();
					if (offlineHandler != null && newTaskData.getLastModified() != null) {
						Date modified = newTaskData.getAttributeFactory().getDateForAttributeType(
								RepositoryTaskAttribute.DATE_MODIFIED, newTaskData.getLastModified());
						notification.setDate(modified);
					}
				}
			} else {
				notification.setDescription("Unread task");
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format notification for: "
					+ task, t));
		}
		return notification;
	}

	static boolean ignoreAttribute(RepositoryTaskData taskData, RepositoryTaskAttribute attribute) {
		AbstractAttributeFactory factory = taskData.getAttributeFactory();
		return (attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_MODIFIED))
				|| attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_CREATION))
				|| "delta_ts".equals(attribute.getId()) || "longdesclength".equals(attribute.getId()));
	}

}
