/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;
import com.google.gson.reflect.TypeToken;

public class GerritClient27 extends GerritClient26 {

	protected GerritClient27(TaskRepository repository, Version version) {
		super(repository, version);
	}

	private Map<String, ProjectInfo> listProjects(IProgressMonitor monitor) throws GerritException {
		final String uri = "/projects/"; //$NON-NLS-1$
		TypeToken<Map<String, ProjectInfo>> resultType = new TypeToken<Map<String, ProjectInfo>>() {
		};
		return executeGetRestRequest(uri, resultType.getType(), monitor);
	}

	@Override
	protected void addProjectsWhenNoSuchService(IProgressMonitor monitor, GerritConfig gerritConfig,
			List<Project> result) throws GerritException {
		Map<String, ProjectInfo> projects = listProjects(monitor);
		for (String projectName : projects.keySet()) {
			result.add(new Project(new NameKey(projectName)));
		}
	}
}
