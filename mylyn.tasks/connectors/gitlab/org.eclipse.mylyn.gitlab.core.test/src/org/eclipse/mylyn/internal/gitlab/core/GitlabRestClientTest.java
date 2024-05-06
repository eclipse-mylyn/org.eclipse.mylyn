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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

class GitlabRestClientTest {

	@SuppressWarnings("nls")
	@Test
	void ignoreMilestonesForProjectsWithIssuesDisabled() throws Exception {
		RepositoryLocation location = new RepositoryLocation("http://localhost");
		CommonHttpClient httpClient = Mockito.mock(CommonHttpClient.class);
		when(httpClient.getLocation()).thenReturn(location);

		GitlabRepositoryConnector connector = new GitlabRepositoryConnector();
		String repoUrl = "http://localhost";
		TaskRepository repository = new TaskRepository(GitlabCoreActivator.CONNECTOR_KIND, repoUrl);
		repository.setProperty(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN, "true");
		repository.setProperty(GitlabCoreActivator.PERSONAL_ACCESS_TOKEN, "test-token");
		GitlabRestClient client = new GitlabRestClient(location, httpClient, connector, repository);

		JsonObject project = new Gson().fromJson("""
				{
				"id": 20240428,
				"issues_enabled": false
				}
				""", JsonObject.class);
		client.getProjectMilestones(project, mock(IOperationMonitor.class, Mockito.RETURNS_SELF));
		verify(httpClient, times(0)).execute(any(HttpRequestBase.class), any(IOperationMonitor.class));
	}
}
