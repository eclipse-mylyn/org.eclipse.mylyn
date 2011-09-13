/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.egit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;

public class GerritProjectToGitRepositoryMapping {

	private final String gerritHostName;

	private final String gerritProjectName;

	public GerritProjectToGitRepositoryMapping(String gerritHostName, String gerritProjectName) {
		this.gerritHostName = gerritHostName;
		this.gerritProjectName = gerritProjectName;
	}

	public Repository findRepository() throws IOException {
		RepositoryUtil repoUtil = getRepositoryUtil();
		RepositoryCache repoCache = getRepositoryCache();
		for (String dirs : repoUtil.getConfiguredRepositories()) {
			Repository repo = repoCache.lookupRepository(new File(dirs));
			if (isMatchingRepository(repo)) {
				return repo;
			}
		}
		return null;
	}

	RepositoryUtil getRepositoryUtil() {
		org.eclipse.egit.core.Activator egit = org.eclipse.egit.core.Activator.getDefault();
		return egit.getRepositoryUtil();
	}

	RepositoryCache getRepositoryCache() {
		org.eclipse.egit.core.Activator egit = org.eclipse.egit.core.Activator.getDefault();
		return egit.getRepositoryCache();
	}

	private boolean isMatchingRepository(Repository repo) {
		List<RemoteConfig> remotes;
		try {
			remotes = RemoteConfig.getAllRemoteConfigs(repo.getConfig());
		} catch (URISyntaxException e) {
			GerritCorePlugin.logWarning("Invalid URI in remote configuration", e);
			return false;
		}
		for (RemoteConfig remote : remotes) {
			if (isMatchingRemoteConfig(remote)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatchingRemoteConfig(RemoteConfig remoteConfig) {
		List<URIish> remoteUris = remoteConfig.getURIs();
		return !remoteUris.isEmpty() && isMatchingUri(remoteUris.get(0));
	}

	private boolean isMatchingUri(URIish uri) {
		String host = uri.getHost();
		return gerritHostName.equalsIgnoreCase(host) && gerritProjectName.equals(calcProjectNameFromUri(uri));
	}

	static String calcProjectNameFromUri(URIish uri) {
		String path = uri.getPath();
		path = cleanTrailingDotGit(path);
		if (isHttpUri(uri)) {
			path = cleanGerritHttpPrefix(path);
		}
		return cleanLeadingSlash(path);
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
		return fetchUri.getScheme().toLowerCase().startsWith("http"); //$NON-NLS-1$
	}

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

}
