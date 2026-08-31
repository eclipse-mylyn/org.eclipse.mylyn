/*******************************************************************************
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
 *    See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.gist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GithubApi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;
import org.kohsuke.github.GHGistUpdater;

/**
 * Handles Gist attatchments
 */
public class GistAttachmentHandler extends AbstractTaskAttachmentHandler {

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#canGetContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask)
	 */
	@Override
	public boolean canGetContent(TaskRepository repository, ITask task) {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#canPostContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask)
	 */
	@Override
	public boolean canPostContent(TaskRepository repository, ITask task) {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#getContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask, org.eclipse.mylyn.tasks.core.data.TaskAttribute, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public InputStream getContent(TaskRepository repository, ITask task, TaskAttribute attachmentAttribute,
			IProgressMonitor monitor) throws CoreException {
		TaskAttribute urlAttribute = attachmentAttribute.getAttribute(GistAttribute.RAW_FILE_URL.getMetadata().getId());
		try {
			if (urlAttribute == null) {
				throw new IOException("Unable to obtain raw file URL from Gist"); //$NON-NLS-1$
			}
			URL url = new URI(urlAttribute.getValue()).toURL();

			GithubApi github = GithubApi.createGithubClient(repository.getCredentials(AuthenticationType.REPOSITORY));
			GHGist gist = github.getGist(task.getTaskId());
			Map<String, GHGistFile> files = gist.getFiles();
			for (Entry<String, GHGistFile> file : files.entrySet()) {
				if (url.toString().equals(file.getValue().getRawUrl())) {
					return new ByteArrayInputStream(file.getValue().getContent().getBytes(StandardCharsets.UTF_8));
				}
			}
			return null;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		} catch (URISyntaxException e) {
			throw new CoreException(GitHub.createWrappedStatus(new IOException(e)));
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler#postContent(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITask, org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource, java.lang.String,
	 *      org.eclipse.mylyn.tasks.core.data.TaskAttribute, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);
		try {
			GithubApi github = GithubApi.createGithubClient(repository);

			GHGistUpdater gist = github.getGist(task.getTaskId()).update();

			gist.description(attachmentAttribute.getParentAttribute()
					.getAttribute(GistAttribute.DESCRIPTION.getMetadata().getId())
					.getValue());

			try (InputStream input = source.createInputStream(monitor)) {
				gist.addFile(mapper.getFileName(), new String(input.readAllBytes(), StandardCharsets.UTF_8));
				gist.update();
			}
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}

	}
}
