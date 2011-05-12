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
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.ListResourceCollector;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Milestone service class for listing the {@link Milestone} objects in use by a
 * repository and user accessed via a {@link GitHubClient}.
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
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_MILESTONES);
		ListResourceCollector<Milestone> collector = new ListResourceCollector<Milestone>();
		PagedRequest<Milestone> request = new PagedRequest<Milestone>(collector);
		if (state != null)
			request.setParams(Collections.singletonMap(
					IssueService.FILTER_STATE, state));
		request.setUri(uri).setType(new TypeToken<List<Milestone>>() {
		}.getType());
		getAll(request);
		return collector.getResources();
	}
}
