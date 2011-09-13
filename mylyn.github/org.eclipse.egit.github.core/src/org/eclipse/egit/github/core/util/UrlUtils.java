/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.util;

import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SUFFIX_GIT;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;
import org.eclipse.egit.github.core.IRepositoryIdProvider;

/**
 * URL utilities
 */
public abstract class UrlUtils {

	/**
	 * Encode given url
	 *
	 * @param url
	 * @return encoded url
	 */
	public static String encode(String url) {
		try {
			return URLEncoder.encode(url, HTTP.DEFAULT_CONTENT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create SSH URL used for repository remote configs
	 *
	 * @param repository
	 * @return URL
	 */
	public static String createRemoteSshUrl(IRepositoryIdProvider repository) {
		return createRemoteSshUrl(repository, HOST_DEFAULT);
	}

	/**
	 * Create SSH URL used for repository remote configs
	 *
	 * @param repository
	 * @param host
	 * @return URL
	 */
	public static String createRemoteSshUrl(IRepositoryIdProvider repository,
			String host) {
		return "git@" + host + ":" + repository.generateId() + SUFFIX_GIT; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create HTTPS URL used for repository remote configs
	 *
	 * @param repository
	 * @param user
	 * @return URL
	 */
	public static String createRemoteHttpsUrl(IRepositoryIdProvider repository,
			String user) {
		return createRemoteHttpsUrl(repository, HOST_DEFAULT, user);
	}

	/**
	 * Create HTTPS URL used for repository remote configs
	 *
	 * @param repository
	 * @param host
	 * @param user
	 * @return URL
	 */
	public static String createRemoteHttpsUrl(IRepositoryIdProvider repository,
			String host, String user) {
		return "https://" + user + "@" + host + "/" + repository.generateId() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ SUFFIX_GIT;
	}

	/**
	 * Create read-only URL used for repository remote configs
	 *
	 * @param repository
	 * @return URL
	 */
	public static String createRemoteReadOnlyUrl(
			IRepositoryIdProvider repository) {
		return createRemoteReadOnlyUrl(repository, HOST_DEFAULT);
	}

	/**
	 * Create read-only URL used for repository remote configs
	 *
	 * @param repository
	 * @param host
	 * @return URL
	 */
	public static String createRemoteReadOnlyUrl(
			IRepositoryIdProvider repository, String host) {
		return "git://" + host + "/" + repository.generateId() + SUFFIX_GIT; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
