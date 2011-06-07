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
package org.eclipse.egit.github.core.service;

import java.io.IOException;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;

/**
 * User service class.
 */
public class UserService extends GitHubService {

	/**
	 * @param client
	 */
	public UserService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get user with given login name
	 * 
	 * @param login
	 * @return user
	 * @throws IOException
	 */
	public User getUser(String login) throws IOException {
		Assert.notNull("Login name cannot be null", login);
		GitHubRequest request = new GitHubRequest();
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_USERS);
		uri.append('/').append(login);
		request.setUri(uri);
		request.setType(User.class);
		return (User) client.get(request).getBody();
	}

	/**
	 * Get currently authenticate user
	 * 
	 * @return user
	 * @throws IOException
	 */
	public User getUser() throws IOException {
		GitHubRequest request = new GitHubRequest();
		request.setUri(IGitHubConstants.SEGMENT_USER);
		request.setType(User.class);
		return (User) client.get(request).getBody();
	}

	/**
	 * Edit given user
	 * 
	 * @param user
	 * @return edited user
	 * @throws IOException
	 */
	public User editUser(User user) throws IOException {
		Assert.notNull("User cannot be null", user);
		return client.post(IGitHubConstants.SEGMENT_USER, user, User.class);
	}

}
