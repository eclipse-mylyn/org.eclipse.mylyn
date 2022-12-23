/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.DownloadSchemeX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SchemeInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountGeneralPreferences.DownloadScheme;
import com.google.gerrit.reviewdb.Project;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 * @author Miles Parker
 */
public class GerritUtil {

	public static String getUserId(AccountInfo user) {
		if (user == null) {
			return Messages.GerritUtil_Anonymous;
		}
		if (user.getPreferredEmail() != null) {
			return user.getPreferredEmail();
		}
		return Messages.GerritUtil_Unknown;
	}

	public static String getUserLabel(AccountInfo user) {
		if (user == null) {
			return Messages.GerritUtil_Anonymous;
		}
		if (user.getFullName() != null) {
			return user.getFullName();
		}
		if (user.getPreferredEmail() != null) {
			String email = user.getPreferredEmail();
			int i = email.indexOf('@');
			return (i > 0) ? email.substring(0, i) : email;
		}
		return Messages.GerritUtil_Unknown;
	}

	public static boolean isPermissionOnlyProject(ProjectDetailX projectDetail, GerritConfig config) {
		if (projectDetail.isPermissionOnly) {
			return true;
		} else if (projectDetail.project.getName().equals(config.getWildProject().get())) {
			return true;
		} else {
			return false;
		}
	}

	public static String shortenText(String t, int minChars, int maxChars) {
		Assert.isTrue(minChars >= 0);
		Assert.isTrue(maxChars >= 0);
		Assert.isTrue(minChars <= maxChars);
		if (t.length() < maxChars) {
			return t;
		}
		for (int i = maxChars - 1; i >= minChars; i--) {
			if (Character.isWhitespace(t.charAt(i))) {
				return NLS.bind(Messages.GerritUtil_X_dot_dot_dot, t.substring(0, i));
			}
		}
		return NLS.bind(Messages.GerritUtil_X_dot_dot_dot, t.substring(0, minChars));
	}

	public static String getSshCloneUri(TaskRepository repository, GerritConfiguration config, Project project)
			throws URISyntaxException {

		if (supportsDownloadScheme(config, DownloadSchemeX.SSH)) {
			return getSchemeUri(config, DownloadSchemeX.SSH, project);
		} else if (config.getGerritConfig().getSchemes() != null) {
			return null;
		}
		Set<DownloadScheme> supportedDownloadSchemes = config.getGerritConfig().getDownloadSchemes();
		if (supportedDownloadSchemes.contains(DownloadScheme.SSH)
				|| supportedDownloadSchemes.contains(DownloadScheme.DEFAULT_DOWNLOADS)) {
			String sshAddress = config.getGerritConfig().getSshdAddress();
			Account account = config.getAccount();
			final StringBuilder sb = new StringBuilder();
			sb.append("ssh://"); //$NON-NLS-1$
			if (account != null) {
				String user = account.getUserName();
				if (user != null && !user.equals("")) { //$NON-NLS-1$
					sb.append(user);
					sb.append('@');
				}
			}
			if (sshAddress.startsWith("*:") || "".equals(sshAddress)) { //$NON-NLS-1$ //$NON-NLS-2$
				sb.append(new URI(repository.getRepositoryUrl()).getHost());
			}
			if (sshAddress.startsWith("*:")) { //$NON-NLS-1$
				sb.append(sshAddress.substring(1));
			} else {
				sb.append(sshAddress);
			}
			sb.append("/"); //$NON-NLS-1$
			sb.append(project.getName());
			return sb.toString();
		} else {
			return null;
		}
	}

	public static String getHttpCloneUri(TaskRepository repository, GerritConfiguration config, Project project) {
		if (supportsDownloadScheme(config, DownloadSchemeX.HTTP)) {
			return getSchemeUri(config, DownloadSchemeX.HTTP, project);
		} else if (config.getGerritConfig().getSchemes() != null) {
			return null;
		}
		Set<DownloadScheme> supportedDownloadSchemes = config.getGerritConfig().getDownloadSchemes();
		if (supportedDownloadSchemes.contains(DownloadScheme.HTTP)
				|| supportedDownloadSchemes.contains(DownloadScheme.DEFAULT_DOWNLOADS)) {
			Account account = config.getAccount();
			final StringBuilder sb = new StringBuilder();
			String httpAddress;
			if (config.getGerritConfig().getGitHttpUrl() != null) {
				httpAddress = config.getGerritConfig().getGitHttpUrl();
			} else {
				httpAddress = repository.getUrl();
			}
			int schemeEndIndex = httpAddress.indexOf("://") + 3; //$NON-NLS-1$
			sb.append(httpAddress.substring(0, schemeEndIndex));
			if (!httpAddress.contains("@") && account != null) { //$NON-NLS-1$
				String user = account.getUserName();
				if (user != null && !user.equals("")) { //$NON-NLS-1$
					sb.append(user);
					sb.append('@');
				}
			}
			sb.append(httpAddress.substring(schemeEndIndex));
			if (!httpAddress.substring(schemeEndIndex).endsWith("/")) { //$NON-NLS-1$
				sb.append("/"); //$NON-NLS-1$
			}
			sb.append("p/"); //$NON-NLS-1$
			sb.append(project.getName());
			return sb.toString();
		} else {
			return null;
		}
	}

	public static String getAnonHttpCloneUri(TaskRepository repository, GerritConfiguration config, Project project) {
		if (supportsDownloadScheme(config, DownloadSchemeX.ANON_HTTP)) {
			return getSchemeUri(config, DownloadSchemeX.ANON_HTTP, project);
		} else if (config.getGerritConfig().getSchemes() != null) {
			return null;
		}
		Set<DownloadScheme> supportedDownloadSchemes = config.getGerritConfig().getDownloadSchemes();
		if (supportedDownloadSchemes.contains(DownloadScheme.ANON_HTTP)
				|| supportedDownloadSchemes.contains(DownloadScheme.DEFAULT_DOWNLOADS)) {
			final StringBuilder sb = new StringBuilder();
			String httpAddress;
			if (config.getGerritConfig().getGitHttpUrl() != null) {
				httpAddress = config.getGerritConfig().getGitHttpUrl();
			} else {
				httpAddress = repository.getUrl();
			}
			sb.append(httpAddress);
			if (!httpAddress.endsWith("/")) { //$NON-NLS-1$
				sb.append("/"); //$NON-NLS-1$
			}
			sb.append("p/"); //$NON-NLS-1$
			sb.append(project.getName());
			return sb.toString();
		} else {
			return null;
		}
	}

	public static String getAnonGitCloneUri(TaskRepository repository, GerritConfiguration config, Project project) {
		if (supportsDownloadScheme(config, DownloadSchemeX.GIT)) {
			return getSchemeUri(config, DownloadSchemeX.GIT, project);
		} else if (config.getGerritConfig().getSchemes() != null) {
			return null;
		}
		Set<DownloadScheme> supportedDownloadSchemes = config.getGerritConfig().getDownloadSchemes();
		String gitAddress = config.getGerritConfig().getGitDaemonUrl();
		if (gitAddress != null && (supportedDownloadSchemes.contains(DownloadScheme.ANON_GIT)
				|| supportedDownloadSchemes.contains(DownloadScheme.DEFAULT_DOWNLOADS))) {
			final StringBuilder sb = new StringBuilder();
			sb.append(gitAddress);
			if (!gitAddress.endsWith("/")) { //$NON-NLS-1$
				sb.append("/"); //$NON-NLS-1$
			}
			sb.append(project.getName());
			return sb.toString();
		} else {
			return null;
		}
	}

	public static HashMap<DownloadScheme, String> getCloneUris(GerritConfiguration config, TaskRepository repository,
			Project project) throws URISyntaxException {
		boolean isAuthenticated = config.getAccount() != null;
		HashMap<DownloadScheme, String> uriMap = new HashMap<DownloadScheme, String>();
		if (isAuthenticated) {
			uriMap.put(DownloadScheme.SSH, getSshCloneUri(repository, config, project));
			uriMap.put(DownloadScheme.HTTP, getHttpCloneUri(repository, config, project));
		}
		uriMap.put(DownloadScheme.ANON_HTTP, getAnonHttpCloneUri(repository, config, project));
		uriMap.put(DownloadScheme.ANON_GIT, getAnonGitCloneUri(repository, config, project));
		return uriMap;
	}

	public static String toChangeId(String id) {
		if (StringUtils.countMatches(id, String.valueOf('~')) == 2) {
			// project~branch~change_id in Gerrit 2.6 and later
			id = id.substring(id.lastIndexOf('~') + 1);
		}
		return id;
	}

	private static boolean supportsDownloadScheme(GerritConfiguration config, DownloadSchemeX scheme) {
		return config.getGerritConfig().getSchemes() != null
				&& config.getGerritConfig().getSchemes().containsKey(scheme);
	}

	private static String getSchemeUri(GerritConfiguration config, DownloadSchemeX scheme, Project project) {
		SchemeInfo info = config.getGerritConfig().getSchemes().get(scheme);
		return info != null ? forProject(info.getUrl(), project) : null;
	}

	public static String forProject(String url, Project project) {
		return url.replaceAll("\\$\\{project\\}", project.getName()); //$NON-NLS-1$
	}
}
