/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
// API 3.0 move to core?
public class AttributeManager {

	private final Set<RepositoryTaskAttribute> changedAttributes = new HashSet<RepositoryTaskAttribute>();

	private List<IAttributeManagerListener> listeners;

	private Set<RepositoryTaskAttribute> oldEdits;

	private RepositoryTaskData oldTaskData;

	private String storageId;

	private String storageUrl;

	private RepositoryTaskData taskData;

	private final TaskDataManager taskDataManager;

	private final TaskRepository taskRepository;

	public AttributeManager(TaskRepository taskRepository, String storageUrl, String storageId) {
		this.taskRepository = taskRepository;
		this.storageUrl = storageUrl;
		this.storageId = storageId;
		this.taskDataManager = TasksUiPlugin.getTaskDataManager();
	}

	public void addAttributeManagerListener(IAttributeManagerListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IAttributeManagerListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Invoke upon change to attribute value.
	 * 
	 * @param attribute
	 *            changed attribute
	 */
	public void attributeChanged(RepositoryTaskAttribute attribute) {
		changedAttributes.add(attribute);

		if (listeners != null) {
			for (IAttributeManagerListener listener : listeners.toArray(new IAttributeManagerListener[0])) {
				listener.attributeChanged(attribute);
			}
		}
	}

	private Set<RepositoryTaskAttribute> getOldEdits() {
		return oldEdits;
	}

	/**
	 * Returns the old task data.
	 */
	// TODO make private?
	public RepositoryTaskData getOldTaskData() {
		return oldTaskData;
	}

	public String getStorageId() {
		return storageId;
	}

	public String getStorageUrl() {
		return storageUrl;
	}

	/**
	 * Returns the new editable task data.
	 */
	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public boolean hasIncomingChanges(RepositoryTaskAttribute taskAttribute) {
		RepositoryTaskData oldTaskData = getOldTaskData();
		if (oldTaskData == null) {
			return false;
		}

		if (hasOutgoingChanges(taskAttribute)) {
			return false;
		}

		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(taskAttribute.getId());
		if (oldAttribute == null) {
			return true;
		}
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(taskAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(taskAttribute.getValues())) {
			return true;
		}
		return false;
	}

	public boolean hasOutgoingChanges(RepositoryTaskAttribute taskAttribute) {
		return getOldEdits().contains(taskAttribute);
	}

	/**
	 * If implementing custom attributes you may need to override this method
	 * 
	 * @return true if one or more attributes exposed in the editor have
	 */
	// TODO EDITOR this should be moved somewhere else to iterate over the form parts instead of attributes
	boolean hasVisibleOutgoingChanges(RepositoryTaskData taskData) {
		if (taskData == null) {
			return false;
		}
		for (RepositoryTaskAttribute attribute : taskData.getAttributes()) {
			if (!attribute.isHidden()) {
				if (hasOutgoingChanges(attribute)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDirty() {
		return !changedAttributes.isEmpty();
	}

	public boolean isNewComment(TaskComment comment) {
		// Simple test (will not reveal new comments if offline data was lost
		if (getOldTaskData() != null) {
			return (comment.getNumber() > getOldTaskData().getComments().size());
		}
		return false;

		// OLD METHOD FOR DETERMINING NEW COMMENTS
		// if (repositoryTask != null) {
		// if (repositoryTask.getLastSyncDateStamp() == null) {
		// // new hit
		// return true;
		// }
		// AbstractRepositoryConnector connector = (AbstractRepositoryConnector)
		// TasksUiPlugin.getRepositoryManager()
		// .getRepositoryConnector(taskData.getRepositoryKind());
		// AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
		// if (offlineHandler != null) {
		//
		// Date lastSyncDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.DATE_MODIFIED,
		// repositoryTask.getLastSyncDateStamp());
		//
		// if (lastSyncDate != null) {
		//
		// // reduce granularity to minutes
		// Calendar calLastMod = Calendar.getInstance();
		// calLastMod.setTimeInMillis(lastSyncDate.getTime());
		// calLastMod.set(Calendar.SECOND, 0);
		//
		// Date commentDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.COMMENT_DATE, comment.getCreated());
		// if (commentDate != null) {
		//
		// Calendar calComment = Calendar.getInstance();
		// calComment.setTimeInMillis(commentDate.getTime());
		// calComment.set(Calendar.SECOND, 0);
		// if (calComment.after(calLastMod)) {
		// return true;
		// }
		// }
		// }
		// }
		// }
		// return false;

	}

	public void operationChanged(RepositoryOperation operation) {
		// TODO EDITOR implement
	}

	public void refreshInput() {
		setTaskData(taskDataManager.getEditableCopy(storageUrl, storageId));
		setOldTaskData(taskDataManager.getOldTaskData(storageUrl, storageId));
		setOldEdits(taskDataManager.getEdits(storageUrl, storageId));
	}

	public void removeAttributeManagerListener(IAttributeManagerListener listener) {
		listeners.remove(listener);
	}

	public void save() {
		TasksUiPlugin.getTaskDataManager().saveEdits(storageUrl, storageId,
				Collections.unmodifiableSet(changedAttributes));
		changedAttributes.clear();
	}

	private void setOldEdits(Set<RepositoryTaskAttribute> oldEdits) {
		this.oldEdits = oldEdits;
	}

	private void setOldTaskData(RepositoryTaskData oldTaskData) {
		this.oldTaskData = oldTaskData;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public void setStorageUrl(String storageUrl) {
		this.storageUrl = storageUrl;
	}

	private void setTaskData(RepositoryTaskData taskData) {
		this.taskData = taskData;
	}

}
