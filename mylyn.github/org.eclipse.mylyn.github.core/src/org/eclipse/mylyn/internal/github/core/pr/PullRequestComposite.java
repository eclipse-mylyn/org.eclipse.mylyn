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
package org.eclipse.mylyn.internal.github.core.pr;

import java.io.IOException;
import java.util.List;

import org.eclipse.mylyn.internal.github.egit.github.core.RepositoryCommit;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Pull request composite that includes commits
 */
public class PullRequestComposite {

	/**
	 * Mix-in that breaks the circular reference between {@link GHRepository} and
	 * its cached commits (each {@code GHCommit} holds a back-reference to its
	 * owner {@link GHRepository}, causing infinite recursion during serialization).
	 */
	@JsonIgnoreProperties("commits")
	private abstract static class GHRepositoryMixin {
	}

	private GHPullRequest request;

	private List<RepositoryCommit> commits;

	public PullRequestComposite() {
	}

	/**
	 * @return request
	 */
	public GHPullRequest getRequest() {
		return request;
	}

	/**
	 * @param pr
	 * @return this pull request composite
	 */
	public PullRequestComposite setRequest(GHPullRequest pr) {
		request = pr;
		return this;
	}

	/**
	 * @return commits
	 */
	public List<RepositoryCommit> getCommits() {
		return commits;
	}

	/**
	 * @param commits
	 * @return this pull request composite
	 */
	public PullRequestComposite setCommits(List<RepositoryCommit> commits) {
		this.commits = commits;
		return this;
	}

	public String valueOf() throws IOException {
		try {
			// Build a mapper that mirrors GitHubClient's configuration but adds a
			// mix-in on GHRepository to ignore the "commits" cache field, which
			// would otherwise cause infinite recursion via GHCommit.owner.
			ObjectMapper mapper = JsonMapper.builder() //
					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) //
					.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) //
					.addMixIn(GHRepository.class, GHRepositoryMixin.class) //
					.visibility(PropertyAccessor.ALL, Visibility.NONE) //
					.visibility(PropertyAccessor.FIELD, Visibility.ANY) //
					.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) //
					.build();

			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new IOException("Failed to serialize pull request composite", e); //$NON-NLS-1$
		}
	}

	public static PullRequestComposite valueOf(String value) throws IOException {
		ObjectReader mapper = org.kohsuke.github.GitHub.getMappingObjectReader();
		return mapper.readValue(value, PullRequestComposite.class);

	}
}
