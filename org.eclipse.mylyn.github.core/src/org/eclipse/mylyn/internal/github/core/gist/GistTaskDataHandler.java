/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.gist;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.issue.IssueAttributeMapper;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Gist task data handler class.
 */
public class GistTaskDataHandler extends AbstractTaskDataHandler {

	/**
	 * Fill task data with comments
	 * 
	 * @param repository
	 * @param data
	 * @param comments
	 * @return specified task data
	 */
	public TaskData fillComments(TaskRepository repository, TaskData data,
			List<Comment> comments) {
		if (comments == null || comments.isEmpty())
			return data;

		int count = 1;
		TaskAttribute root = data.getRoot();
		for (Comment comment : comments) {
			TaskCommentMapper commentMapper = new TaskCommentMapper();
			User author = comment.getUser();
			if (author != null) {
				IRepositoryPerson authorPerson = repository.createPerson(author
						.getLogin());
				authorPerson.setName(author.getName());
				commentMapper.setAuthor(authorPerson);
			}
			commentMapper.setCreationDate(comment.getCreatedAt());
			commentMapper.setText(comment.getBody());
			commentMapper.setCommentId(comment.getUrl());
			commentMapper.setNumber(count);

			TaskAttribute attribute = root
					.createAttribute(TaskAttribute.PREFIX_COMMENT + count);
			commentMapper.applyTo(attribute);
			count++;
		}
		return data;
	}

	/**
	 * Get gist url
	 * 
	 * @param repositoryUrl
	 * @param gist
	 * @return url
	 */
	public String getGistUrl(String repositoryUrl, Gist gist) {
		return repositoryUrl + '/' + gist.getId();
	}

	/**
	 * Fill task data with data from gist
	 * 
	 * @param repository
	 * @param data
	 * @param gist
	 * @return specified task data
	 */
	public TaskData fillTaskData(TaskRepository repository, TaskData data,
			Gist gist) {
		TaskAttributeMapper mapper = data.getAttributeMapper();

		TaskAttribute key = GistAttribute.KEY.create(data);
		mapper.setValue(key, gist.getId());

		TaskAttribute description = GistAttribute.DESCRIPTION.create(data);
		String gistDescription = gist.getDescription();
		if (gistDescription != null)
			mapper.setValue(description, gistDescription);

		TaskAttribute created = GistAttribute.CREATED.create(data);
		mapper.setDateValue(created, gist.getCreatedAt());

		TaskAttribute updated = GistAttribute.UPDATED.create(data);
		mapper.setDateValue(updated, gist.getUpdatedAt());

		TaskAttribute url = GistAttribute.URL.create(data);
		url.setValue(gist.getHtmlUrl());

		TaskAttribute pullUrl = GistAttribute.CLONE_URL.create(data);
		pullUrl.setValue(gist.getGitPushUrl());

		IRepositoryPerson reporterPerson = null;
		User user = gist.getUser();
		if (user != null) {
			TaskAttribute reporter = GistAttribute.AUTHOR.create(data);
			reporterPerson = repository.createPerson(user.getLogin());
			reporterPerson.setName(user.getName());
			mapper.setRepositoryPerson(reporter, reporterPerson);

			TaskAttribute gravatar = GistAttribute.AUTHOR_GRAVATAR.create(data);
			mapper.setValue(gravatar, user.getGravatarUrl());
		}

		Map<String, GistFile> files = gist.getFiles();
		int fileCount = 0;
		long sizeCount = 0;
		if (files != null && !files.isEmpty()) {
			int count = 1;
			for (GistFile file : files.values()) {
				fileCount++;
				sizeCount += file.getSize();
				TaskAttachmentMapper attachmentMapper = new TaskAttachmentMapper();
				attachmentMapper.setFileName(file.getFilename());
				attachmentMapper.setReplaceExisting(true);
				attachmentMapper.setLength((long) file.getSize());
				attachmentMapper.setPatch(false);
				attachmentMapper.setAuthor(reporterPerson);
				attachmentMapper.setAttachmentId(file.getFilename());
				TaskAttribute attribute = data.getRoot().createAttribute(
						TaskAttribute.PREFIX_ATTACHMENT + count);
				attachmentMapper.applyTo(attribute);

				GistAttribute.RAW_FILE_URL.create(attribute).setValue(
						file.getRawUrl());

				count++;
			}
		}

		GistAttribute.COMMENT_NEW.create(data);

		TaskAttribute summary = GistAttribute.SUMMARY.create(data);
		mapper.setValue(summary, generateSummary(fileCount, sizeCount));

		return data;
	}

	private String generateSummary(int files, long size) {
		StringBuilder summaryText = new StringBuilder();
		if (files != 1)
			summaryText.append(MessageFormat.format(
					Messages.GistTaskDataHandler_FilesMultiple, files));
		else
			summaryText.append(Messages.GistTaskDataHandler_FilesSingle);
		summaryText.append(',').append(' ').append(formatSize(size));
		return summaryText.toString();
	}

	private String formatSize(long size) {
		if (size == 1) {
			return Messages.GistTaskDataHandler_SizeByte;
		} else if (size < 1024) {
			return new DecimalFormat(Messages.GistTaskDataHandler_SizeBytes)
					.format(size);
		} else if (size >= 1024 && size <= 1048575) {
			return new DecimalFormat(Messages.GistTaskDataHandler_SizeKilobytes)
					.format(size / 1024.0);
		} else if (size >= 1048576 && size <= 1073741823) {
			return new DecimalFormat(Messages.GistTaskDataHandler_SizeMegabytes)
					.format(size / 1048576.0);
		} else {
			return new DecimalFormat(Messages.GistTaskDataHandler_SizeGigabytes)
					.format(size / 1073741824.0);
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#postTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.data.TaskData, java.util.Set,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		RepositoryResponse response = null;

		Gist gist = new Gist();
		GitHubClient client = new GitHubClient();
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());
			gist.setUser(new User().setLogin(credentials.getUserName()));
		}

		GistService service = new GistService(client);
		TaskAttribute root = taskData.getRoot();
		gist.setId(taskData.getTaskId());
		gist.setDescription(root
				.getAttribute(GistAttribute.DESCRIPTION.getId()).getValue());

		if (taskData.isNew()) {
			try {
				gist = service.createGist(gist);
			} catch (IOException e) {
				throw new CoreException(GitHub.createErrorStatus(e));
			}
			response = new RepositoryResponse(ResponseKind.TASK_CREATED,
					gist.getId());
		} else {
			try {
				String newComment = root.getAttribute(
						GistAttribute.COMMENT_NEW.getId()).getValue();
				if (newComment.length() > 0)
					service.createComment(taskData.getTaskId(), newComment);

				service.updateGist(gist);
			} catch (IOException e) {
				throw new CoreException(GitHub.createErrorStatus(e));
			}
			response = new RepositoryResponse(ResponseKind.TASK_UPDATED,
					taskData.getTaskId());
		}
		return response;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#initializeTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.data.TaskData,
	 *      org.eclipse.mylyn.tasks.core.ITaskMapping,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {
		TaskAttributeMapper mapper = data.getAttributeMapper();

		TaskAttribute summary = GistAttribute.SUMMARY.create(data);
		mapper.setValue(summary, Messages.GistTaskDataHandler_SummaryNewGist);
		GistAttribute.DESCRIPTION.create(data);

		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#getAttributeMapper(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		return new IssueAttributeMapper(taskRepository);
	}

}
