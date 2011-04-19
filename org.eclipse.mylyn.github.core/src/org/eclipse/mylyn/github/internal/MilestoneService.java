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
package org.eclipse.mylyn.github.internal;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * Milestone service class for listing the {@link Milestone} objects in use by a
 * repository and user accessed via a {@link GitHubClient}.
 */
public class MilestoneService {

	private GitHubClient client;

	/**
	 * Create milestone service
	 * 
	 * @param client
	 *            cannot be null
	 */
	public MilestoneService(GitHubClient client) {
		Assert.isNotNull(client, "Client cannot be null"); //$NON-NLS-1$
		this.client = client;
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
		Assert.isNotNull(user, "User cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_MILESTONES).append(
				IGitHubConstants.SUFFIX_JSON);
		Map<String, String> params = null;
		if (state != null) {
			params = Collections.singletonMap(IssueService.FILTER_STATE, state);
		}
		TypeToken<List<Milestone>> milestoneToken = new TypeToken<List<Milestone>>() {
		};
		return this.client
				.get(uri.toString(), params, milestoneToken.getType());
	}

}
