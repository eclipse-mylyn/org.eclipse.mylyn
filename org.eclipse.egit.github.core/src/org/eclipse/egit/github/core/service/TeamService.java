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

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_MEMBERS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ORGS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_TEAMS;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Service class for working with organization teams
 *
 * @see <a href="http://developer.github.com/v3/orgs/teams">GitHub team API
 *      documentation</a>
 */
public class TeamService extends GitHubService {

	/**
	 * @param client
	 */
	public TeamService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get team with given id
	 *
	 * @param id
	 * @return team
	 * @throws IOException
	 */
	public Team getTeam(int id) throws IOException {
		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(Team.class);
		return (Team) client.get(request).getBody();
	}

	/**
	 * Get all teams in the given organization
	 *
	 * @param organization
	 * @return list of teams
	 * @throws IOException
	 */
	public List<Team> getTeams(String organization) throws IOException {
		if (organization == null)
			throw new IllegalArgumentException("Organization cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_TEAMS);
		PagedRequest<Team> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<Team>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Create the given team
	 *
	 * @param organization
	 * @param team
	 * @return created team
	 * @throws IOException
	 */
	public Team createTeam(String organization, Team team) throws IOException {
		if (organization == null)
			throw new IllegalArgumentException("Organization cannot be null"); //$NON-NLS-1$
		if (team == null)
			throw new IllegalArgumentException("Team cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_TEAMS);
		return client.post(uri.toString(), team, Team.class);
	}

	/**
	 * Edit the given team
	 *
	 * @param team
	 * @return edited team
	 * @throws IOException
	 */
	public Team editTeam(Team team) throws IOException {
		if (team == null)
			throw new IllegalArgumentException("Team cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(team.getId());
		return client.post(uri.toString(), team, Team.class);
	}

	/**
	 * Delete the team with the given id
	 *
	 * @param id
	 * @throws IOException
	 */
	public void deleteTeam(int id) throws IOException {
		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		client.delete(uri.toString());
	}

	/**
	 * Get members of team with given id
	 *
	 * @param id
	 * @return team members
	 * @throws IOException
	 */
	public List<User> getMembers(int id) throws IOException {
		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		uri.append(SEGMENT_MEMBERS);
		PagedRequest<User> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<User>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Is the given user a member of the team with the given id
	 *
	 * @param id
	 * @param user
	 * @return true if member, false if not member
	 * @throws IOException
	 */
	public boolean isMember(int id, String user) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		uri.append(SEGMENT_MEMBERS);
		uri.append('/').append(user);
		return check(uri.toString());
	}

	/**
	 * Add given user to team with given id
	 *
	 * @param id
	 * @param user
	 * @throws IOException
	 */
	public void addMember(int id, String user) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		uri.append(SEGMENT_MEMBERS);
		uri.append('/').append(user);
		client.put(uri.toString(), null, null);
	}

	/**
	 * Remove given user from team with given id
	 *
	 * @param id
	 * @param user
	 * @throws IOException
	 */
	public void removeMember(int id, String user) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_TEAMS);
		uri.append('/').append(id);
		uri.append(SEGMENT_MEMBERS);
		uri.append('/').append(user);
		client.delete(uri.toString());
	}
}
