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
package org.eclipse.egit.github.core.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.ListResourceCollector;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Label service class for listing {@link Label} objects in use for a given user
 * and repository.
 */
public class LabelService extends GitHubService {

	/**
	 * Create label service for client
	 * 
	 * @param client
	 */
	public LabelService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get labels
	 * 
	 * @param user
	 * @param repository
	 * @return list of labels
	 * @throws IOException
	 */
	public List<Label> getLabels(String user, String repository)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_LABELS);
		ListResourceCollector<Label> collector = new ListResourceCollector<Label>();
		PagedRequest<Label> request = new PagedRequest<Label>(collector);
		request.setUri(uri).setType(new TypeToken<List<Label>>() {
		}.getType());
		getAll(request);
		return collector.getResources();
	}

	/**
	 * Set the labels for an issue
	 * 
	 * @param user
	 * @param repository
	 * @param issueId
	 * @param labels
	 * @return list of labels
	 * @throws IOException
	 */
	public List<Label> setLabels(String user, String repository,
			String issueId, List<Label> labels) throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Issue id cannot be null", issueId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES).append('/').append(issueId);
		uri.append(IGitHubConstants.SEGMENT_LABELS);

		return this.client.put(uri.toString(), labels,
				new TypeToken<List<Label>>() {
				}.getType());
	}

	/**
	 * Create label
	 * 
	 * @param user
	 * @param repository
	 * @param label
	 * @return created label
	 * @throws IOException
	 */
	public Label createLabel(String user, String repository, Label label)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Label cannot be null", label); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_LABELS);
		return this.client.post(uri.toString(), label, Label.class);
	}

	/**
	 * Get label with given name
	 * 
	 * @param user
	 * @param repository
	 * @param label
	 * @return label
	 * @throws IOException
	 */
	public Label getLabel(String user, String repository, String label)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Label cannot be null", label); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_LABELS);
		uri.append('/').append(label);
		GitHubRequest request = createRequest().setUri(uri)
				.setType(Label.class);
		return (Label) client.get(request).getBody();
	}

	/**
	 * Delete a label with the given id from the given repository
	 * 
	 * @param user
	 * @param repository
	 * @param label
	 * @throws IOException
	 */
	public void deleteLabel(String user, String repository, String label)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Label cannot be null", label); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_LABELS);
		uri.append('/').append(label);
		client.delete(uri.toString());
	}

}
