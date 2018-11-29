/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.DownloadSchemeX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SchemeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.DownloadSchemeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.reviewdb.Project.NameKey;

public class GerritClient212 extends GerritClient29 {

	protected GerritClient212(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	protected GerritConfigX getGerritConfigFromServerInfo(IProgressMonitor monitor) throws GerritException {
		String query = "/config/server/info"; //$NON-NLS-1$/
		org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo serverInfo = getRestClient()
				.executeGetRestRequest(query, org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo.class,
						monitor);

		return convertServerInfoToGerritConfig(serverInfo);
	}

	private GerritConfigX convertServerInfoToGerritConfig(ServerInfo serverInfo) {
		GerritConfigX config = new GerritConfigX();

		// gerrit/all_projects + user -> wildproject
		config.setWildProject(new NameKey(serverInfo.getGerrit().getRootProject()));

		// download/schemes -> getGerritConfig().getDownloadSchemes
		Map<DownloadSchemeX, SchemeInfo> schemes = new HashMap<>();

		serverInfo.getDownload().getSchemes().entrySet().forEach(entry -> {
			DownloadSchemeX scheme = DownloadSchemeX.fromString(entry.getKey());
			if (scheme != null) {
				DownloadSchemeInfo info = entry.getValue();
				SchemeInfo schemeInfo = new SchemeInfo(info.getUrl(), info.isAuthRequired(), info.isAuthSupported(),
						info.getCommands(), info.getCloneCommands());

				schemes.put(scheme, schemeInfo);
			}
		});

		config.setSchemes(schemes);

		return config;
	}
}
