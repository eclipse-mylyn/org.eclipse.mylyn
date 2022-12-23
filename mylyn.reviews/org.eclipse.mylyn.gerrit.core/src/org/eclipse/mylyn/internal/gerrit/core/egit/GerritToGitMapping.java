/*******************************************************************************
 * Copyright (c) 2011, 2012 SAP and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sascha Scholz (SAP) - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.egit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;

/**
 * @author Sascha Scholz
 * @author Steffen Pingel
 */
public class GerritToGitMapping {

	private static String cleanGerritHttpPrefix(String path) {
		String httpPathPrefix = "/p/"; //$NON-NLS-1$
		int httpPathPrefixIndex = path.indexOf(httpPathPrefix);
		if (httpPathPrefixIndex >= 0) {
			return path.substring(httpPathPrefixIndex + httpPathPrefix.length());
		} else {
			return path;
		}
	}

	private static String cleanLeadingSlash(String path) {
		if (path.startsWith("/")) { //$NON-NLS-1$
			return path.substring(1);
		} else {
			return path;
		}
	}

	private static String cleanTrailingDotGit(String path) {
		int dotGitIndex = path.lastIndexOf(".git"); //$NON-NLS-1$
		if (dotGitIndex >= 0) {
			return path.substring(0, dotGitIndex);
		} else {
			return path;
		}
	}

	private static boolean isHttpUri(URIish fetchUri) {
		String scheme = fetchUri.getScheme();
		return scheme != null && scheme.toLowerCase().startsWith("http"); //$NON-NLS-1$
	}

	static String calcProjectNameFromUri(URIish uri) {
		String path = uri.getPath();
		path = cleanTrailingDotGit(path);
		if (isHttpUri(uri)) {
			path = cleanGerritHttpPrefix(path);
		}
		return cleanLeadingSlash(path);
	}

	private final GerritConfig config;

	private String gerritHost;

	private final String gerritProject;

	private RemoteConfig remote;

	private Repository repository;

	private final TaskRepository taskRepository;

	public GerritToGitMapping(TaskRepository taskRepository, GerritConfig config, String gerritProject) {
		this.config = config;
		this.taskRepository = taskRepository;
		this.gerritProject = gerritProject;
	}

	public Repository find() throws IOException {
		if (gerritProject == null) {
			return null;
		}

		gerritHost = getHostFromUrl(getGitDaemonUrl());
		if (gerritHost != null) {
			findMatchingRepository();
		}
		if (repository == null) {
			// fall back to repository url
			gerritHost = getHostFromUrl(taskRepository.getRepositoryUrl());
			if (gerritHost != null) {
				findMatchingRepository();
			}
		}
		return repository;
	}

	public String getGerritHost() {
		return gerritHost;
	}

	public String getGerritProjectName() {
		return gerritProject;
	}

	public Project getGerritProject() {
		return new Project(new NameKey(gerritProject));
	}

	public RemoteConfig getRemote() {
		return remote;
	}

	public Repository getRepository() {
		return repository;
	}

	private String getGitDaemonUrl() {
		if (config != null) {
			return config.getGitDaemonUrl();
		} else {
			return null;
		}
	}

	private String getHostFromUrl(String url) {
		if (url == null) {
			return null;
		}
		try {
			return new URI(url).getHost();
		} catch (URISyntaxException e) {
			GerritCorePlugin.logWarning("Error in task repository URL " + url, e); //$NON-NLS-1$
			return null;
		}
	}

	private boolean isMatchingRemoteConfig(RemoteConfig remoteConfig) {
		List<URIish> remoteUris = remoteConfig.getURIs();
		return !remoteUris.isEmpty() && isMatchingUri(remoteUris.get(0));
	}

	private boolean isMatchingUri(URIish uri) {
		String host = uri.getHost();
		return gerritHost.equalsIgnoreCase(host) && gerritProject.equals(calcProjectNameFromUri(uri));
	}

	protected RemoteConfig findMatchingRemote() throws IOException {
		Assert.isNotNull(repository);
		List<RemoteConfig> remotes;
		try {
			remotes = RemoteConfig.getAllRemoteConfigs(repository.getConfig());
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URI in remote configuration", e); //$NON-NLS-1$
		}
		for (RemoteConfig remote : remotes) {
			if (isMatchingRemoteConfig(remote)) {
				return remote;
			}
		}
		return null;
	}

	protected void findMatchingRepository() throws IOException {
		RepositoryUtil repoUtil = getRepositoryUtil();
		RepositoryCache repoCache = getRepositoryCache();
		for (String dirs : repoUtil.getConfiguredRepositories()) {
			repository = repoCache.lookupRepository(new File(dirs));
			remote = findMatchingRemote();
			if (remote != null) {
				return;
			}
		}
		repository = null;
		remote = null;
	}

	RepositoryCache getRepositoryCache() {
		org.eclipse.egit.core.Activator egit = org.eclipse.egit.core.Activator.getDefault();
		return RepositoryCache.INSTANCE;
	}

	RepositoryUtil getRepositoryUtil() {
		org.eclipse.egit.core.Activator egit = org.eclipse.egit.core.Activator.getDefault();
		return RepositoryUtil.INSTANCE;
	}

}
