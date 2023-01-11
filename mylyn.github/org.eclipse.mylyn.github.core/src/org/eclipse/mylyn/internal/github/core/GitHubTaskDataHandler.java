/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * Core task data handler
 */
public abstract class GitHubTaskDataHandler extends AbstractTaskDataHandler {

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		return new GitHubTaskAttributeMapper(taskRepository);
	}

	/**
	 * Set repository person as value of given attribute
	 *
	 * @param data
	 * @param attribute
	 * @param user
	 * @param repository
	 * @return task attribute
	 */
	protected TaskAttribute setPersonValue(TaskData data,
			TaskAttribute attribute, User user, TaskRepository repository) {
		if (user != null)
			data.getAttributeMapper().setRepositoryPerson(attribute,
					createPerson(user, repository));
		return attribute;
	}

	/**
	 * Create repository person from user
	 *
	 * @param user
	 * @param repository
	 * @return repository person
	 */
	protected IRepositoryPerson createPerson(User user,
			TaskRepository repository) {
		IRepositoryPerson person = repository.createPerson(user.getLogin());
		person.setName(user.getName());
		return person;
	}

	/**
	 * Set date value of given task attribute
	 *
	 * @param data
	 * @param attribute
	 * @param date
	 * @return task attribute
	 */
	protected TaskAttribute setDateValue(TaskData data,
			TaskAttribute attribute, Date date) {
		if (date != null)
			data.getAttributeMapper().setDateValue(attribute, date);
		return attribute;
	}

	/**
	 * Add task attributes for given comments under given parent
	 *
	 * @param parent
	 * @param comments
	 * @param repository
	 */
	protected void addComments(final TaskAttribute parent,
			final List<Comment> comments, final TaskRepository repository) {
		if (comments == null || comments.isEmpty())
			return;

		int count = 1;
		for (Comment comment : comments) {
			TaskCommentMapper commentMapper = new TaskCommentMapper();
			commentMapper
					.setAuthor(createPerson(comment.getUser(), repository));
			commentMapper.setCreationDate(comment.getCreatedAt());
			commentMapper.setText(comment.getBody());
			commentMapper.setCommentId(comment.getUrl());
			commentMapper.setNumber(Integer.valueOf(count));

			TaskAttribute attribute = parent
					.createAttribute(TaskAttribute.PREFIX_COMMENT + count);
			commentMapper.applyTo(attribute);
			count++;
		}
	}

	/**
	 * Get attribute value
	 *
	 * @param taskData
	 * @param attr
	 * @return value
	 */
	protected String getAttributeValue(TaskData taskData,
			GitHubAttributeMetadata attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.getId());
		return attribute == null ? null : attribute.getValue();
	}

	/**
	 * Create task attribute from metadata
	 *
	 * @param data
	 * @param attribute
	 * @return created task attribute
	 */
	protected TaskAttribute createAttribute(TaskData data,
			GitHubAttributeMetadata attribute) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());
		return attr;
	}

	/**
	 * Create task attribute and set value
	 *
	 * @param data
	 * @param metadata
	 * @param value
	 * @return created attribute
	 */
	protected TaskAttribute createAttribute(TaskData data,
			GitHubAttributeMetadata metadata, String value) {
		TaskAttribute attr = createAttribute(data, metadata);
		if (value != null)
			data.getAttributeMapper().setValue(attr, value);
		return attr;
	}

	/**
	 * Create attribute and set value
	 *
	 * @param data
	 * @param metadata
	 * @param date
	 * @return created attribute
	 */
	protected TaskAttribute createAttribute(TaskData data,
			GitHubAttributeMetadata metadata, Date date) {
		return setDateValue(data, createAttribute(data, metadata), date);
	}

	/**
	 * Create attribute and set value
	 *
	 * @param data
	 * @param metadata
	 * @param user
	 * @param repository
	 * @return created attribute
	 */
	protected TaskAttribute createAttribute(TaskData data,
			GitHubAttributeMetadata metadata, User user,
			TaskRepository repository) {
		return setPersonValue(data, createAttribute(data, metadata), user,
				repository);
	}

	/**
	 * Create standard operation task attribute
	 *
	 * @param data
	 * @return created attribute
	 */
	protected TaskAttribute createOperationAttribute(TaskData data) {
		TaskAttribute attribute = data.getRoot().createAttribute(
				TaskAttribute.OPERATION);
		attribute.getMetaData().setType(TaskAttribute.TYPE_OPERATION);
		return attribute;
	}

	/**
	 * Add operation with label and id
	 *
	 * @param data
	 * @param id
	 * @param label
	 * @param isDefault
	 * @return created task attribute
	 */
	protected TaskAttribute addOperation(TaskData data, String id,
			String label, boolean isDefault) {
		final TaskAttribute root = data.getRoot();
		TaskAttribute attribute = root
				.createAttribute(TaskAttribute.PREFIX_OPERATION + id);
		TaskOperation.applyTo(attribute, id, label);

		if (isDefault)
			TaskOperation.applyTo(root.getAttribute(TaskAttribute.OPERATION),
					id, label);
		return attribute;
	}

	/**
	 * Does the attribute's value in the given task data match the authenticated
	 * client user?
	 *
	 * @param client
	 * @param metadata
	 * @param data
	 * @return true if match, false otherwise
	 */
	protected boolean attributeMatchesUser(GitHubClient client,
			GitHubAttributeMetadata metadata, TaskData data) {
		if (client == null || metadata == null || data == null)
			return false;
		String user = client.getUser();
		if (user == null || user.length() == 0)
			return false;
		return metadata.getValue(data).equals(user);
	}

	/**
	 * Is configured client user a collaborator on the given repository?
	 *
	 * @param client
	 * @param repo
	 * @return true if collaborator, false otherwise
	 * @throws IOException
	 */
	protected boolean isCollaborator(GitHubClient client, RepositoryId repo)
			throws IOException {
		String user = client.getUser();
		if (user == null || user.length() == 0)
			return false;
		return new CollaboratorService(client).isCollaborator(repo, user);
	}
}
