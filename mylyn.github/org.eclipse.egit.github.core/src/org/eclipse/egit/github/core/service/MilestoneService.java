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

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_MILESTONES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Milestone service class for listing the {@link Milestone} objects in use by a
 * repository and user accessed via a {@link GitHubClient}.
 *
 * @see <a href="http://developer.github.com/v3/issues/milestones">GitHub
 *      milestones API documentation</a>
 */
public class MilestoneService extends GitHubService {

	/**
	 * Create milestone service
	 *
	 * @param client
	 *            cannot be null
	 */
	public MilestoneService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get milestones
	 *
	 * @param user
	 * @param repository
	 * @param state
	 * @return list of milestones
	 * @throws IOException
	 */
	public List<Milestone> getMilestones(String user, String repository,
			String state) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_MILESTONES);
		PagedRequest<Milestone> request = createPagedRequest();
		if (state != null)
			request.setParams(Collections.singletonMap(
					IssueService.FILTER_STATE, state));
		request.setUri(uri).setType(new TypeToken<List<Milestone>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Create a milestone
	 *
	 * @param user
	 *            must be non-null
	 * @param repository
	 *            must be non-null
	 * @param milestone
	 *            must be non-null
	 * @return created milestone
	 * @throws IOException
	 */
	public Milestone createMilestone(String user, String repository,
			Milestone milestone) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$
		if (milestone == null)
			throw new IllegalArgumentException("Milestone cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_MILESTONES);
		return client.post(uri.toString(), milestone, Milestone.class);
	}

	/**
	 * Get a milestone
	 *
	 * @param user
	 *            must be non-null
	 * @param repository
	 *            must be non-null
	 * @param number
	 *            must be non-null
	 * @return created milestone
	 * @throws IOException
	 */
	public Milestone getMilestone(String user, String repository, String number)
			throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$
		if (number == null)
			throw new IllegalArgumentException("Milestone cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_MILESTONES);
		uri.append('/').append(number);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(Milestone.class);
		return (Milestone) client.get(request).getBody();
	}

	/**
	 * Delete a milestone with the given id from the given repository
	 *
	 * @param user
	 * @param repository
	 * @param milestone
	 * @throws IOException
	 */
	public void deleteMilestone(String user, String repository, String milestone)
			throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$
		if (milestone == null)
			throw new IllegalArgumentException("Milestone cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_MILESTONES);
		uri.append('/').append(milestone);
		client.delete(uri.toString());
	}
}
