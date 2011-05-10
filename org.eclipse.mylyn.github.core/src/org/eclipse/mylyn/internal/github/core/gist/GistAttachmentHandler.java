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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.internal.Gist;
import org.eclipse.mylyn.github.internal.GistFile;
import org.eclipse.mylyn.github.internal.GistService;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubClient;
import org.eclipse.mylyn.github.internal.GitHubRequest;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * Handles Gist attatchments
 */
public class GistAttachmentHandler extends AbstractTaskAttachmentHandler {

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#canGetContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask)
	 */
	public boolean canGetContent(TaskRepository repository, ITask task) {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#canPostContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask)
	 */
	public boolean canPostContent(TaskRepository repository, ITask task) {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#getContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask,
	 *      org.eclipse.mylyn.tasks.core.data.TaskAttribute,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public InputStream getContent(TaskRepository repository, ITask task,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor)
			throws CoreException {
		GitHubClient client = new GitHubClient();
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null)
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());

		TaskAttribute urlAttribute = attachmentAttribute
				.getAttribute(GistAttribute.RAW_FILE_URL.getId());
		try {
			GitHubRequest request = new GitHubRequest();
			request.setUri(urlAttribute.getValue());
			return client.getStream(request);
		} catch (IOException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#postContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask,
	 *      org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource,
	 *      java.lang.String, org.eclipse.mylyn.tasks.core.data.TaskAttribute,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void postContent(TaskRepository repository, ITask task,
			AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor)
			throws CoreException {
		TaskAttachmentMapper mapper = TaskAttachmentMapper
				.createFrom(attachmentAttribute);
		Gist gist = new Gist().setId(task.getTaskId());
		gist.setDescription(attachmentAttribute.getParentAttribute()
				.getAttribute(GistAttribute.DESCRIPTION.getId()).getValue());
		GistFile file = new GistFile();
		file.setFilename(mapper.getFileName());
		gist.setFiles(Collections.singletonMap(file.getFilename(), file));

		GitHubClient client = new GitHubClient();
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null)
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());

		GistService service = new GistService(client);
		InputStream input = source.createInputStream(monitor);
		try {
			byte[] buffer = new byte[8192];
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int read;
			while ((read = input.read(buffer)) != -1)
				output.write(buffer, 0, read);
			file.setContent(output.toString());
			service.updateGist(gist);
		} catch (IOException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
				// Ignored
			}
		}

	}
}
