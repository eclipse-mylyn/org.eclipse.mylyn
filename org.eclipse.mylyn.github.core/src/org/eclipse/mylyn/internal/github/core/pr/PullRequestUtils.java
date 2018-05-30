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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.util.UrlUtils;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.internal.github.core.GitHub;

/**
 * Pull request utilities
 */
public abstract class PullRequestUtils {

	/**
	 * HEAD_SOURCE
	 */
	public static final String HEAD_SOURCE = '+' + Constants.R_HEADS + '*';

	/**
	 * Get destination ref spec
	 *
	 * @param remote
	 * @return ref spec
	 */
	public static String getDesintationRef(RemoteConfig remote) {
		return Constants.R_REMOTES + remote.getName() + "/*"; //$NON-NLS-1$
	}

	/**
	 * Get branch name for pull request
	 *
	 * @param request
	 * @return non-null/non-empty branch name
	 */
	public static String getBranchName(PullRequest request) {
		return "pull-request-" + request.getNumber(); //$NON-NLS-1$
	}

	/**
	 * Get Git repository for pull request
	 *
	 * @param request
	 * @return repository or null if none found
	 */
	public static Repository getRepository(PullRequest request) {
		org.eclipse.egit.github.core.Repository remoteRepo = request.getBase()
				.getRepo();
		String id = remoteRepo.getOwner().getLogin() + '/'
				+ remoteRepo.getName() + Constants.DOT_GIT;
		RepositoryCache cache = Activator.getDefault().getRepositoryCache();
		for (String path : Activator.getDefault().getRepositoryUtil()
				.getConfiguredRepositories())
			try {
				Repository repo = cache.lookupRepository(new File(path));
				RemoteConfig rc = new RemoteConfig(repo.getConfig(),
						Constants.DEFAULT_REMOTE_NAME);
				for (URIish uri : rc.getURIs())
					if (uri.toString().endsWith(id))
						return repo;
			} catch (IOException e) {
				GitHub.logError(e);
				continue;
			} catch (URISyntaxException e) {
				GitHub.logError(e);
				continue;
			}
		return null;
	}

	/**
	 * Configure pull request topic branch to use head remote
	 *
	 * @param repo
	 * @param request
	 * @throws IOException
	 */
	public static void configureTopicBranch(Repository repo, PullRequest request)
			throws IOException {
		String branch = getBranchName(request);
		String remote = request.getHead().getRepo().getOwner().getLogin();
		StoredConfig config = repo.getConfig();
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branch,
				ConfigConstants.CONFIG_KEY_MERGE, getHeadBranch(request));
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branch,
				ConfigConstants.CONFIG_KEY_REMOTE, remote);
		config.save();
	}

	/**
	 * Get owner of marker
	 *
	 * @param marker
	 * @return owner login name, may be null
	 */
	public static String getOwner(PullRequestMarker marker) {
		if (marker == null)
			return null;
		org.eclipse.egit.github.core.Repository repo = marker.getRepo();
		if (repo == null)
			return null;
		User owner = repo.getOwner();
		return owner != null ? owner.getLogin() : null;
	}

	/**
	 * Are the given pull request's source and destination repositories the
	 * same?
	 *
	 * @param request
	 * @return true if same, false otherwise
	 */
	public static boolean isFromSameRepository(PullRequest request) {
		if (request == null)
			return false;
		String headLogin = getOwner(request.getHead());
		if (headLogin == null)
			return false;
		return headLogin.equals(getOwner(request.getBase()));
	}

	/**
	 * Get remote for given pull request
	 *
	 * @param repo
	 * @param request
	 * @return remote config
	 * @throws URISyntaxException
	 */
	public static RemoteConfig getRemote(Repository repo, PullRequest request)
			throws URISyntaxException {
		if (isFromSameRepository(request))
			return getRemoteConfig(repo, Constants.DEFAULT_REMOTE_NAME);
		else
			return getRemoteConfig(repo, getOwner(request.getHead()));
	}

	/**
	 * Get remote config with given name
	 *
	 * @param repo
	 * @param name
	 * @return remote config
	 * @throws URISyntaxException
	 */
	public static RemoteConfig getRemoteConfig(Repository repo, String name)
			throws URISyntaxException {
		for (RemoteConfig candidate : RemoteConfig.getAllRemoteConfigs(repo
				.getConfig()))
			if (name.equals(candidate.getName()))
				return candidate;
		return null;
	}

	/**
	 * Add remote for the head of a pull request if it doesn't exist
	 *
	 * @param repo
	 * @param request
	 * @return remote configuration
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static RemoteConfig addRemote(Repository repo, PullRequest request)
			throws IOException, URISyntaxException {
		RemoteConfig remote = getRemote(repo, request);
		if (remote != null)
			return remote;

		StoredConfig config = repo.getConfig();
		org.eclipse.egit.github.core.Repository head = request.getHead()
				.getRepo();
		remote = new RemoteConfig(config, head.getOwner().getLogin());
		if (head.isPrivate())
			remote.addURI(new URIish(UrlUtils.createRemoteSshUrl(head)));
		else
			remote.addURI(new URIish(UrlUtils.createRemoteReadOnlyUrl(head)));

		remote.addFetchRefSpec(new RefSpec(HEAD_SOURCE
				+ ":" + getDesintationRef(remote))); //$NON-NLS-1$
		remote.update(config);
		config.save();
		return remote;
	}

	/**
	 * Is given branch name the currently checked out branch?
	 *
	 * @param name
	 * @param repo
	 * @return true if checked out branch, false otherwise
	 */
	public static boolean isCurrentBranch(String name, Repository repo) {
		try {
			return name.equals(Repository.shortenRefName(repo.getFullBranch()));
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Get head branch ref for outside repository pull requests
	 *
	 * @param request
	 * @return remote head branch ref name
	 */
	public static String getHeadBranch(PullRequest request) {
		PullRequestMarker head = request.getHead();
		return Constants.R_REMOTES + head.getRepo().getOwner().getLogin() + '/'
				+ head.getRef();
	}
}
