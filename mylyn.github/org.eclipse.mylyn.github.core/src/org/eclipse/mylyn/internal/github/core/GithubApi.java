/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.github.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistBuilder;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHubAbuseLimitHandler;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.connector.GitHubConnector;
import org.kohsuke.github.connector.GitHubConnectorRequest;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.kohsuke.github.internal.DefaultGitHubConnector;

/**
 * proxy class for the hub4j/github-api library. Minimize name conflicts with the org.kohsuke.github library.
 * @author George
 */
public class GithubApi {

	private final org.kohsuke.github.GitHub github;

	private final String username;

	private final String password;

	private final GitHubConnector connector;

	private GithubApi(org.kohsuke.github.GitHub github, String username, String password, GitHubConnector connector)
			throws IOException {
		this.github = github;
		this.username = username;
		this.password = password;
		this.connector = connector;
	}

	public GHRepository getRepo(String url) throws IOException {
		try {
			URI uri = new URI(url);
			GHRepository repo = github.getRepository(uri.getPath().substring(1));
			return repo;
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	public List<GHGist> listGists() throws IOException {
		return github.getMyself().listGists().toList();
	}

	public GHGist getGist(String gistId) throws IOException {
		return github.getGist(gistId);
	}

	/**
	 * List comments for a gist. Gist comments have the same JSON structure as
	 * {@link GHIssueComment} and are fetched via the library's own
	 * {@link GitHubConnector} using a {@link GitHubConnectorRequest}.
	 *
	 * @param gist
	 * @return list of comments
	 * @throws IOException
	 */
	public List<GHIssueComment> listGistComments(GHGist gist) throws IOException {
		try {
			Map<String, List<String>> headers = new LinkedHashMap<>();
			setupGistPost(headers);
			URL commentsUrl = new URI(gist.getCommentsUrl()).toURL();

			GitHubConnectorRequest request = new GitHubConnectorRequest() {
				@Override public Map<String, List<String>> allHeaders() { return headers; }
				@Override public InputStream body() { return null; }
				@Override public String contentType() { return null; }
				@Override public boolean hasBody() { return false; }
				@Override public String header(String name) {
					List<String> vals = headers.get(name);
					return vals != null && !vals.isEmpty() ? vals.get(0) : null;
				}
				@Override public String method() { return "GET"; } //$NON-NLS-1$
				@Override public URL url() { return commentsUrl; }
			};


			try (GitHubConnectorResponse response = connector.send(request)) {
				GHIssueComment[] comments = org.kohsuke.github.GitHub.getMappingObjectReader()
						.forType(GHIssueComment[].class)
						.readValue(response.bodyStream());
				return Arrays.asList(comments);
			}
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	public GHIssueComment addGistComment(GHGist gist, String comment) throws IOException {
		try {
			Map<String, List<String>> headers = new LinkedHashMap<>();
			setupGistPost(headers);
			headers.put("Content-Type", List.of("application/json")); //$NON-NLS-1$ //$NON-NLS-2$
			URL commentsUrl = new URI(gist.getCommentsUrl()).toURL();
			byte[] bodyBytes = ("{\"body\":" + org.kohsuke.github.GitHub.getMappingObjectWriter().writeValueAsString(comment) + "}").getBytes(java.nio.charset.StandardCharsets.UTF_8); //$NON-NLS-1$ //$NON-NLS-2$

			GitHubConnectorRequest request = new GitHubConnectorRequest() {
				@Override public Map<String, List<String>> allHeaders() { return headers; }
				@Override public InputStream body() { return new java.io.ByteArrayInputStream(bodyBytes); }
				@Override public String contentType() { return "application/json"; } //$NON-NLS-1$
				@Override public boolean hasBody() { return true; }
				@Override public String header(String name) {
					List<String> vals = headers.get(name);
					return vals != null && !vals.isEmpty() ? vals.get(0) : null;
				}
				@Override public String method() { return "POST"; } //$NON-NLS-1$
				@Override public URL url() { return commentsUrl; }
			};

			try (GitHubConnectorResponse response = connector.send(request)) {
				return org.kohsuke.github.GitHub.getMappingObjectReader()
						.forType(GHIssueComment.class)
						.readValue(response.bodyStream());
			}
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	private void setupGistPost(Map<String, List<String>> headers) {
		headers.put("Accept", List.of("application/vnd.github+json")); //$NON-NLS-1$ //$NON-NLS-2$
		headers.put("X-GitHub-Api-Version", List.of("2022-11-28")); //$NON-NLS-1$ //$NON-NLS-2$
		if (password != null && !password.isEmpty()) {
			if (username != null && !username.isEmpty()) {
				String credentials = Base64.getEncoder()
						.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
				headers.put("Authorization", List.of("Basic " + credentials)); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				headers.put("Authorization", List.of("Bearer " + password)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	public GHMyself getMyself() throws IOException {
		return github.getMyself();
	}

	public static GithubApi createGithubClient(TaskRepository repository) throws IOException {
		return createGithubClient(repository.getCredentials(AuthenticationType.REPOSITORY));
	}

	public static GithubApi createGithubClient(@NonNull AuthenticationCredentials credentials) throws IOException {
		return createGithubClient(credentials.getUserName(), credentials.getPassword());
	}

	public static GithubApi createGithubClient(String username, String password)
			throws IOException {
		GitHubConnector connector = DefaultGitHubConnector.create();
		GitHubAbuseLimitHandler limitHandler = new GitHubAbuseLimitHandler() {
			@Override
			public void onError(GitHubConnectorResponse connectorResponse) throws IOException {
				throw new IOException(
						"GitHub abuse limit exceeded"); //$NON-NLS-1$
			}
		};
		GitHubBuilder githubBuilder = new GitHubBuilder() //
				.withConnector(connector) //
				.withAbuseLimitHandler(limitHandler);

		if (password != null && !password.isEmpty()) {
			githubBuilder.withOAuthToken(password, username);
		}

		org.kohsuke.github.GitHub client = githubBuilder.build();
		return new GithubApi(client, username, password, connector);
	}

	public GHGistBuilder createGist() {
		return github.createGist();
	}

	public static GithubApi createAnonymousGithubClient() throws IOException {
		return createGithubClient(null, null);
	}

	public GHRepositorySearchBuilder searchRepositories() {
		return github.searchRepositories();
	}
}
