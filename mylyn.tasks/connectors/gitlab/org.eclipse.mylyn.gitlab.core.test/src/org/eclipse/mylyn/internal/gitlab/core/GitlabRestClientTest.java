/*******************************************************************************
 * Copyright © 2024 max
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GitlabRestClientTest {

	@SuppressWarnings("nls")
	@Test
	public void ignoreMilestonesForProjectsWithIssuesDisabled() throws Exception {
		RepositoryLocation location = new RepositoryLocation("http://localhost");
		CommonHttpClient httpClient = new CommonHttpClient(location);
		GitlabRepositoryConnector connector = new GitlabRepositoryConnector();
		TaskRepository repository = new TaskRepository(GitlabCoreActivator.CONNECTOR_KIND, "http://localhost");
		GitlabRestClient client = new GitlabRestClient(location, httpClient, connector, repository);

		JsonObject project = new Gson().fromJson("""
				{
				"id": 20240428,
				"issues_enabled": false
				}
				""", JsonObject.class);
		JsonArray emptyArray = new JsonArray();
		JsonArray milestones = client.getProjectMilestones(project, new NullOperationMonitor());
		assertEquals(emptyArray, milestones);
	}
}
