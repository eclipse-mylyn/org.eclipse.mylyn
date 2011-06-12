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

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Organization service class
 */
public class OrganizationService extends GitHubService {

	/**
	 * @param client
	 */
	public OrganizationService(GitHubClient client) {
		super(client);
	}

	/**
	 * Create org request
	 * 
	 * @param user
	 * @param start
	 * @param size
	 * @return request
	 */
	protected PagedRequest<User> createOrgRequest(String user, int start,
			int size) {
		PagedRequest<User> request = new PagedRequest<User>(start, size);
		if (user == null)
			request.setUri(IGitHubConstants.SEGMENT_USER
					+ IGitHubConstants.SEGMENT_ORGS);
		else {
			StringBuilder uri = new StringBuilder();
			uri.append(IGitHubConstants.SEGMENT_USERS);
			uri.append('/').append(user);
			uri.append(IGitHubConstants.SEGMENT_ORGS);
			request.setUri(uri);
		}
		request.setType(new TypeToken<List<User>>() {
		}.getType());
		return request;
	}

	/**
	 * Get organizations that the currently authenticated user is a member of
	 * 
	 * @return list of organizations
	 * @throws IOException
	 */
	public List<User> getOrganizations() throws IOException {
		PagedRequest<User> request = createOrgRequest(null,
				PagedRequest.PAGE_FIRST, PagedRequest.PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Get organizations that the given user is a member of
	 * 
	 * @param user
	 * @return list of organizations
	 * @throws IOException
	 */
	public List<User> getOrganizations(String user) throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		PagedRequest<User> request = createOrgRequest(user,
				PagedRequest.PAGE_FIRST, PagedRequest.PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Get organization with the given name
	 * 
	 * @param name
	 * @return organization
	 * @throws IOException
	 */
	public User getOrganization(String name) throws IOException {
		Assert.notNull("Name cannot be null", name); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder();
		uri.append(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(name);
		GitHubRequest request = new GitHubRequest();
		request.setUri(uri);
		request.setType(User.class);
		return (User) client.get(request).getBody();
	}

	/**
	 * Edit given organization
	 * 
	 * @param organization
	 * @return edited user
	 * @throws IOException
	 */
	public User editOrganization(User organization) throws IOException {
		Assert.notNull("Organization cannot be null", organization); //$NON-NLS-1$
		final String name = organization.getLogin();
		Assert.notNull("Organization login cannot be null", name); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder();
		uri.append(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(name);
		return client.post(uri.toString(), organization, User.class);
	}

	/**
	 * Get members of organization
	 * 
	 * @param organization
	 * @return list of all organization members
	 * @throws IOException
	 */
	public List<User> getMembers(String organization) throws IOException {
		Assert.notNull("Organization cannot be null", organization); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_MEMBERS);
		PagedRequest<User> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<User>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Check if the given user is a member of the given organization
	 * 
	 * @param organization
	 * @param user
	 * @return true if member, false if not member
	 * @throws IOException
	 */
	public boolean isMember(String organization, String user)
			throws IOException {
		Assert.notNull("Organization cannot be null", organization); //$NON-NLS-1$
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_MEMBERS);
		uri.append('/').append(user);
		return check(uri.toString());
	}
}
