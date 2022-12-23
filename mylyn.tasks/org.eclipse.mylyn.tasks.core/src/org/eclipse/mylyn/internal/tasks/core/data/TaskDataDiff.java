/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskAttributeDiff;
import org.eclipse.mylyn.tasks.core.data.ITaskDataDiff;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskDataDiff implements ITaskDataDiff {

	private final String[] ATTRIBUTES_IDS = new String[] { TaskAttribute.SUMMARY, TaskAttribute.DESCRIPTION,
			TaskAttribute.PRODUCT, TaskAttribute.PRIORITY, TaskAttribute.USER_ASSIGNED, TaskAttribute.STATUS, };

	private final TaskData newTaskData;

	private final TaskData oldTaskData;

	private final Set<ITaskComment> newComments = new LinkedHashSet<ITaskComment>();

	private final Set<TaskAttribute> newAttachments = new LinkedHashSet<TaskAttribute>();

	private final Set<ITaskAttributeDiff> changedAttributes = new LinkedHashSet<ITaskAttributeDiff>();

	private final RepositoryModel repositoryModel;

	private final TaskRepository repository;

	public TaskDataDiff(RepositoryModel repositoryModel, TaskData newTaskData, TaskData oldTaskData) {
		Assert.isNotNull(repositoryModel);
		Assert.isNotNull(newTaskData);
		this.repositoryModel = repositoryModel;
		this.repository = newTaskData.getAttributeMapper().getTaskRepository();
		this.newTaskData = newTaskData;
		this.oldTaskData = oldTaskData;
		parse();
	}

	public boolean hasChanged() {
		return !changedAttributes.isEmpty() || !newComments.isEmpty() || !newAttachments.isEmpty();
	}

	@Deprecated
	public void setHasChanged(boolean hasChanged) {

	}

	public TaskRepository getRepository() {
		return repository;
	}

	public TaskData getOldTaskData() {
		return oldTaskData;
	}

	public TaskData getNewTaskData() {
		return newTaskData;
	}

	public Collection<ITaskComment> getNewComments() {
		return newComments;
	}

	public Collection<TaskAttribute> getNewAttachments() {
		return newAttachments;
	}

	public Collection<ITaskAttributeDiff> getChangedAttributes() {
		return changedAttributes;
	}

	private void parse() {
		for (String attributeId : ATTRIBUTES_IDS) {
			TaskAttribute newAttribute = newTaskData.getRoot().getMappedAttribute(attributeId);
			TaskAttribute oldAttribute = null;
			if (oldTaskData != null) {
				oldAttribute = oldTaskData.getRoot().getMappedAttribute(attributeId);
			}
			if (oldAttribute == null && newAttribute == null) {
				continue;
			}
			addChangedAttribute(oldAttribute, newAttribute, true);
		}

		// other attributes that have changed on newTaskData
		for (TaskAttribute newAttribute : newTaskData.getRoot().getAttributes().values()) {
			TaskAttribute oldAttribute = null;
			if (oldTaskData != null) {
				oldAttribute = oldTaskData.getRoot().getMappedAttribute(newAttribute.getPath());
			}
			addChangedAttribute(oldAttribute, newAttribute, false);
		}
		// other attributes that have been removed from newTaskData
		if (oldTaskData != null) {
			for (TaskAttribute oldAttribute : oldTaskData.getRoot().getAttributes().values()) {
				TaskAttribute newAttribute = newTaskData.getRoot().getMappedAttribute(oldAttribute.getPath());
				if (newAttribute == null) {
					addChangedAttribute(oldAttribute, newAttribute, false);
				}
			}
		}
	}

	private void addChangedAttribute(TaskAttribute oldAttribute, TaskAttribute newAttribute, boolean ignoreKind) {
		TaskAttribute attribute;
		if (newAttribute != null) {
			attribute = newAttribute;
		} else {
			attribute = oldAttribute;
		}
		String type = attribute.getMetaData().getType();
		if (TaskAttribute.TYPE_COMMENT.equals(type)) {
			addChangedComment(oldAttribute, newAttribute);
		} else if (TaskAttribute.TYPE_ATTACHMENT.equals(type)) {
			newAttachments.add(attribute);
		} else if (TaskAttribute.TYPE_OPERATION.equals(type)) {
			// ignore
		} else if (ignoreKind || attribute.getMetaData().getKind() != null) {
			TaskAttributeDiff diff = new TaskAttributeDiff(oldAttribute, newAttribute);
			if (diff.hasChanges()) {
				changedAttributes.add(diff);
			}
		}
	}

	private void addChangedComment(TaskAttribute oldAttribute, TaskAttribute newAttribute) {
		if (oldAttribute == null) {
			ITaskComment comment = repositoryModel.createTaskComment(newAttribute);
			if (comment != null) {
				newComments.add(comment);
			}
		}
	}
}
